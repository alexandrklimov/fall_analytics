package org.aklimov.fall_analytics.lib.services.data

import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode
import kotlinx.coroutines.flow.collect
import mu.KLogging
import org.aklimov.fall_analytics.lib.services.domain.AlreadyLoadedInfo
import org.aklimov.fall_analytics.shared.Ticker
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import java.sql.Timestamp
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

interface HistorySaver {
    suspend fun actualize(ticker: Ticker)
}

/**
 * This saver stores daily OHLC history by ticker into DB.
 */
class MoexISSHistorySaverImpl(
    jdbcTemplateArg: JdbcTemplate,
    private val historyLoader: HistoryLoader
) : HistorySaver {
    private val jdbcTemplate = NamedParameterJdbcTemplate(jdbcTemplateArg)

    override suspend fun actualize(ticker: Ticker) {
        logger.info { "Start Actualize Ticker [${ticker.value}] " }

        val isTblExists = transaction {
            db.dialect.allTablesNames().any { it == ticker.value }
        }

        lateinit var cachedMetaData: Map<FieldName, Pair<Int, FieldTypesEnum>>
        if (isTblExists) {
            logger.info { "${ticker.value} table exists" }

            val alreadyLoadedInfo = obtainAlreadyLoadedInfo(ticker)
            logger.info { "Already loaded info: $alreadyLoadedInfo" }

            //Check the last row number is coincide with data on the MOEX side
            //At the same time extract mata-info form response
            val jsonCheckHistResp: ObjectNode = historyLoader.reqDataForCheckLoaded(ticker, alreadyLoadedInfo)
                .run { this["history"] as ObjectNode }

            cachedMetaData = extractMeta(jsonCheckHistResp)

            val dataJson = requireNotNull(jsonCheckHistResp["data"]) as ArrayNode
            require(dataJson.size() == 1)

            val checkDate: LocalDate = (dataJson.get(0) as ArrayNode).get(
                cachedMetaData.getValue(FieldName("tradedate")).first
            ).run {
                LocalDate.parse(this.asText())
            }
            require(alreadyLoadedInfo.tradeDate == checkDate)

            historyLoader.loadPages(ticker, alreadyLoadedInfo.lastRowNum, DATA_ARR_EXTRACTOR).collect {
                saveHistoryPage(ticker, it, cachedMetaData)
            }

        } else {
            logger.info { "${ticker.value} table does not exist - it will be created" }

            //Obtain metadata
            val metaHistResp: ObjectNode = historyLoader.loadRecord(ticker, 0).run {
                this["history"] as ObjectNode
            }
            cachedMetaData = extractMeta(metaHistResp)
            createTableByMeta(ticker, cachedMetaData)

            historyLoader.loadPages(ticker, 0, DATA_ARR_EXTRACTOR).collect {
                saveHistoryPage(ticker, it, cachedMetaData)
            }
        }
    }

    /**
     * Get the last loaded date and row number
     */
    private fun obtainAlreadyLoadedInfo(ticker: Ticker): AlreadyLoadedInfo {
        return jdbcTemplate.queryForMap(
            "SELECT COUNT(tradedate) as row_num, MAX(tradedate) as tradedate FROM ${ticker.value}",
            emptyMap<String, Any>()
        ).run {
            AlreadyLoadedInfo(
                LocalDate.ofInstant(
                    Instant.ofEpochMilli(
                        (this["tradedate"] as Timestamp).time
                    ),
                    ZoneId.systemDefault()
                ),
                this["row_num"] as Long
            )
        }
    }

    private fun extractMeta(jsonHistoryResp: ObjectNode): Map<FieldName, Pair<Int, FieldTypesEnum>> {
        val nameToTypeMap = jsonHistoryResp.run {
            this["metadata"] as ObjectNode
        }.run {
            this.fields().asSequence().associate {
                FieldName(it.key) to FieldTypesEnum.resolve(
                    requireNotNull(
                        (it.value as ObjectNode)["type"].textValue()
                    )
                )
            }
        }

        val columns = (jsonHistoryResp["columns"] as ArrayNode).map { it.asText().toLowerCase() }

        return nameToTypeMap.mapValues {
            Pair(
                columns.indexOf(it.key.value), it.value
            )
        }
    }

    private fun createTableByMeta(
        ticker: Ticker,
        metadata: Map<FieldName, Pair<Int, FieldTypesEnum>>
    ) {
        val colDefinitionBlock = metadata.entries.sortedBy { it.value.first }
            .map {
                val pkString = if (it.key.value == "tradedata") "PRIMARY KEY" else ""
                "${it.key.value} ${it.value.second.inPgSql} $pkString"
            }.joinToString()

        jdbcTemplate.update(
            """
                CREATE TABLE ${ticker.value}(
                    $colDefinitionBlock
                )
            """.trimIndent(),
            emptyMap<String, Any>()
        )
    }

    private fun saveHistoryPage(ticker: Ticker, page: ObjectNode, metadata: Map<FieldName, Pair<Int, FieldTypesEnum>>) {
        val metaEntries = metadata.entries
        val idxToName: Map<Int, String> = metaEntries.associate { it.value.first to it.key.value }
        val idxToType: Map<Int, FieldTypesEnum> = metaEntries.associate { it.value.first to it.value.second }
        val colsBlock: String = metaEntries.sortedBy { it.value.first }.map { ":${it.key.value}" }.joinToString()

        val batchVals: Array<Map<String, Any?>> = DATA_ARR_EXTRACTOR(page).map {
            it.mapIndexed { idx, value ->
                Pair(
                    idxToName.getValue(idx),
                    idxToType.getValue(idx).parse(value.asText())
                )
            }.toMap()
        }.toTypedArray()

        jdbcTemplate.batchUpdate(
            "INSERT INTO ${ticker.value} VALUES($colsBlock)",
            batchVals
        )
    }

    companion object : KLogging() {
        val DATA_ARR_EXTRACTOR = fun(rootNode: ObjectNode): ArrayNode {
            return (rootNode["history"] as ObjectNode)["data"] as ArrayNode
        }
    }
}

class FieldName(fieldNameArg: String) {
    val value = fieldNameArg.toLowerCase()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is FieldName) return false

        if (value != other.value) return false

        return true
    }

    override fun hashCode(): Int {
        return value.hashCode()
    }

    override fun toString(): String {
        return "FieldName(value='$value')"
    }

}

enum class FieldTypesEnum(
    val inJsonMeta: String,
    val inPgSql: String
) {
    STRING("string", "varchar(1000)") {
        override fun parse(str: String) = if (isJsonNull(str)) null else str
    },
    DATE("date", "timestamp without time zone") {
        override fun parse(str: String) = if (isJsonNull(str)) null else LocalDate.parse(str)
    },
    DOUBLE("double", "double precision") {
        override fun parse(str: String) = if (isJsonNull(str)) null else str.toDouble()
    },
    INT("int32", "integer") {
        override fun parse(str: String) = if (isJsonNull(str)) null else str.toInt()
    };

    abstract fun parse(str: String): Any?

    companion object {
        fun resolve(type: String): FieldTypesEnum = when (type) {
            STRING.inJsonMeta -> STRING
            DATE.inJsonMeta -> DATE
            DOUBLE.inJsonMeta -> DOUBLE
            INT.inJsonMeta -> INT
            else -> throw RuntimeException("Unknown field type: $type")
        }

        fun isJsonNull(str: String) = str.isBlank() || str.toLowerCase() == "null"
    }
}
