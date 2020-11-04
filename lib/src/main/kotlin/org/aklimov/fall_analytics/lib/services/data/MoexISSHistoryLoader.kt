package org.aklimov.fall_analytics.lib.services.data

import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.ktor.client.*
import io.ktor.client.request.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import mu.KLogging
import org.aklimov.fall_analytics.lib.services.domain.AlreadyLoadedInfo
import org.aklimov.fall_analytics.shared.Ticker

/**
 * MOEX ISS allows to load data in paged-mode, no more then 100 rows per page.
 *
 * MOEX ISS uses zero-based offset convention (start URL's param.)
 *
 * MOEX ISS allow following numbers as value of the **limit** parameter: 100, 50, 20, 10, 5, 1
 *
 * For MOEX ISS see [Programming interface to the ISS][https://www.moex.com/a2920]
 */
interface HistoryLoader {
    /**
     * @param ticker
     * @param start zero-based offset
     * @param dataArrayExtractor
     * @param limit 100, 50, 20, 10, 5, 1
     */
    suspend fun loadPages(
        ticker: Ticker,
        start: Long,
        dataArrayExtractor: (ObjectNode) -> ArrayNode,
        limit: Int = 100
    ): Flow<ObjectNode>

    suspend fun reqDataForCheckLoaded(ticker: Ticker, alreadyLoadedInfo: AlreadyLoadedInfo): ObjectNode

    suspend fun loadRecord(ticker: Ticker, recIdx: Long): ObjectNode
}

class MoexISSHistoryLoader(
    private val httpClient: HttpClient
) : HistoryLoader {

    override suspend fun loadPages(
        ticker: Ticker,
        start: Long,
        dataArrayExtractor: (ObjectNode) -> ArrayNode,
        limit: Int
    ): Flow<ObjectNode> = flow {
        logger.info {
            """
                Start Load Pages
                ticker=$ticker
                start=$start
                limit=$limit
            """.trimMargin()
        }

        var offset = start
        while(true){
            logger.info { "#loadPages: offset=$offset" }
            val res = httpClient.get<String>(buildUrl(ticker, offset, limit)).run{
                mapper.readValue(this) as ObjectNode
            }

            if(dataArrayExtractor(res).isEmpty){
                break
            } else {
                emit(res)
                offset += limit
            }
        }

    }

    override suspend fun reqDataForCheckLoaded(ticker: Ticker, alreadyLoadedInfo: AlreadyLoadedInfo):ObjectNode{
        logger.info {"#reqDataForCheckLoaded: ticker=$ticker | alreadyLoadedInfo=$alreadyLoadedInfo"}
        return loadRecord(ticker, alreadyLoadedInfo.lastRowNum - 1)
    }


    override suspend fun loadRecord(ticker: Ticker, recIdx: Long): ObjectNode {
        logger.info { "#loadRecords: ticker=$ticker | recIdx=$recIdx" }
        return httpClient.get<String>(buildUrl(ticker, recIdx, 1)).run {
            mapper.readValue(this) as ObjectNode
        }
    }

    companion object: KLogging() {
        val mapper = jacksonObjectMapper()
        val TICKER_LOAD_URL_TPL =
            "http://iss.moex.com/iss/history/engines/stock/markets/shares/boards/tqbr/securities/%s.json?start=%d&limit=%d&lang=en"

        fun buildUrl(ticker: Ticker, start: Long = 0, limit: Int = 100) =
            TICKER_LOAD_URL_TPL.format(ticker.value, start, limit)
    }
}
