package org.aklimov.fall_analytics.server

import org.aklimov.fall_analytics.lib.services.copmutation.RequiredGainFromEndOfFallFinder
import org.aklimov.fall_analytics.lib.services.dao.OhlcDao
import org.aklimov.fall_analytics.lib.services.domain.GainFromEndOfFallResult
import org.aklimov.fall_analytics.lib.services.domain.OHLC
import org.aklimov.fall_analytics.lib.services.domain.Ticker
import org.springframework.web.bind.annotation.*

@RestController
@CrossOrigin
@RequestMapping("/rest")
class OhlcDataController(
    private val ohlcDao: OhlcDao,
    private val requiredGainFromEndOfFallFinder: RequiredGainFromEndOfFallFinder
) {
    @GetMapping("/ping")
    fun ping() = "OK";

    @GetMapping("/v1/tickers")
    fun listTickers(): List<String> {
        println("тест текс тест")
        return ohlcDao.listTickers().map { it.value }
    }

    @GetMapping("/v1/data/{ticker}")
    fun getData(@PathVariable ticker: String): List<OHLC> {
        return ohlcDao.getData(Ticker(ticker))
    }

    @GetMapping("/v1/compute/req-gain-from-fall/{ticker}/{fallChngPtc}")
    fun findGainFromEndOfFall(
        @PathVariable ticker: String,
        @PathVariable fallChngPtc: Double
    ): GainFromEndOfFallResult {
        return requiredGainFromEndOfFallFinder.compute(Ticker(ticker), fallChngPtc/100)
    }
}
