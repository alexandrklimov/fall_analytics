package org.aklimov.fall_analytics.lib.services.dao

import org.aklimov.fall_analytics.lib.services.dao.Utils.LIST_TICKERS_SQL_TPL
import org.aklimov.fall_analytics.lib.services.dao.Utils.tmstmpToLocalDate
import org.aklimov.fall_analytics.lib.services.domain.AlreadyLoadedInfo
import org.aklimov.fall_analytics.lib.services.domain.OHLC
import org.aklimov.fall_analytics.lib.services.domain.Point
import org.aklimov.fall_analytics.lib.services.domain.Ticker
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import java.sql.SQLException
import java.sql.Timestamp
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

interface OhlcDao{
    fun getData(ticker: Ticker): List<OHLC>
    fun listTickers(): List<Ticker>
    fun obtainAlreadyLoadedInfo(ticker: Ticker): AlreadyLoadedInfo
    fun loadPoints(ticker: Ticker): Set<Point>
    fun checkTickerTableExists(ticker: Ticker): Boolean
}

class SqlOhlcDao(private val jdbcTemplate: NamedParameterJdbcTemplate): OhlcDao {
    override fun getData(ticker: Ticker): List<OHLC> {
        if( ! listTickers().contains(ticker)){
            throw RuntimeException("$ticker is not valid ticker")
        }

        return jdbcTemplate.query(
            //language=sql
            """
                SELECT tradedate, open, high, low, close FROM ${ticker.value}
            """.trimIndent(),
            emptyMap<String, Any?>()
        ) { row, _ ->
            OHLC(
                row.getLocalDate("tradedate"),
                row.getDouble("open"),
                row.getDouble("high"),
                row.getDouble("low"),
                row.getDouble("close"),
            )
        }
    }

    override fun listTickers(): List<Ticker> {
        return jdbcTemplate.queryForList(
            LIST_TICKERS_SQL_TPL,
            emptyMap<String, Any?>(),
            String::class.java
        ).map(::Ticker)
    }

    /**
     * Get the last loaded date and row number
     */
    override fun obtainAlreadyLoadedInfo(ticker: Ticker): AlreadyLoadedInfo {
        return jdbcTemplate.queryForMap(
            "SELECT COUNT(tradedate) as row_num, MAX(tradedate) as tradedate FROM ${ticker.value}",
            emptyMap<String, Any>()
        ).run {
            AlreadyLoadedInfo(
                Utils.tmstmpToLocalDate((this["tradedate"] as Timestamp).time),
                this["row_num"] as Long
            )
        }
    }

    override fun loadPoints(ticker: Ticker): Set<Point> {
        return requireNotNull(
            jdbcTemplate.query(
                "SELECT tradedate, close, open FROM ${ticker.value}",
                emptyMap<String, Any?>()
            ) { rs, _ ->
                Point(
                    rs.getLocalDate("tradedate"),
                    rs.getDouble("close"),
                    rs.getDouble("open"),
                )
            }
        ).toSet()
    }

    override fun checkTickerTableExists(ticker: Ticker) = jdbcTemplate.queryForObject(
        Utils.CHECK_TABLE_SQL_TPL,
        mapOf("tableName" to ticker.value),
        Boolean::class.java
    )!!
}
