package org.aklimov.fall_analytics.lib.services.data

import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.ktor.client.*
import io.ktor.client.request.*
import mu.KLogging
import org.aklimov.fall_analytics.lib.services.domain.Ticker

interface InfoProvider{
    suspend fun loadTqbrShares(): List<Ticker>
}

class MoexISSInfoProvider(
    private val httpClient: HttpClient
): InfoProvider {
    override suspend fun loadTqbrShares(): List<Ticker> {
        logger.info {"Start Loading TQBR Shares..."}

        return httpClient.get<String>(TQBR_SHARES_INFO_URL).run{
            mapper.readValue(this) as ObjectNode
        }.run{
            (this["securities"] as ObjectNode)["data"] as ArrayNode
        }.map {
            Ticker(
                requireNotNull(it.get(0)).textValue()
            )
        }
    }

    companion object: KLogging(){
        val mapper = jacksonObjectMapper()
        const val TQBR_SHARES_INFO_URL =
            "http://iss.moex.com/iss/engines/stock/markets/shares/boards/tqbr/securities.json?lang=en"
    }
}
