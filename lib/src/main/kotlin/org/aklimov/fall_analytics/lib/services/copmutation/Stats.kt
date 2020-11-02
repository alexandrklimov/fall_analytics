package org.aklimov.fall_analytics.lib.services.copmutation

import mu.KLogging
import org.aklimov.fall_analytics.lib.services.domain.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.nield.kotlinstatistics.median
import org.nield.kotlinstatistics.percentile

class Stats {

    fun growChangeStat(period: Int, ticker: Ticker): GrowChangeStatRes {
        require(period > 0)

        return transaction {
            val lst = Utils.loadData(ticker).sortedBy(Point::date)

            if (lst.isEmpty()) throw RuntimeException("No data for ticker ${ticker.value}")
            if (lst.size < period) throw RuntimeException("not enough data for period's window $period")

            val onlyGrowChngs = lst.windowed(period + 1) // We take period+1 for getting OPEN last cndl. before period batch
                .filter(Utils.Companion::checkListOfGrow)
                .map(Utils.Companion::computeChngOfWnd)

            GrowChangeStatRes(
                ticker = ticker,
                period = period,
                mean = onlyGrowChngs.sum() / lst.size,
                median = onlyGrowChngs.median(),
                quartiles = onlyGrowChngs.quartiles(),
                percentiles = onlyGrowChngs.percentiles()
            )
        }
    }

    fun searchGrowMoreThenPercentile(stat: GrowChangeStatRes, growMoreThenPercentile: ValidPercentilesEnum): GrowMoreThenPercentileResult {

        logger.info { "Search periods [by ${stat.period}] has grow more than ot equals $growMoreThenPercentile percentile" }

        val growsPercentiles = Utils.loadData(stat.ticker)
            .sortedBy(Point::date)
            .windowed(stat.period + 1) // We take period+1 for getting OPEN last cndl. before period batch
            .filter(Utils.Companion::checkListOfGrow)
            .map {
                Pair(
                    it.last(),
                    Utils.computeChngOfWnd(it)
                )
            }.map {
                val percIdx = stat.percentiles.indexOfFirst { perc -> perc >= it.second }
                GrowPercentile(it.first, it.second, ValidPercentilesEnum.all[percIdx], stat.percentiles[percIdx])
            }.filter {
                //TODO: onptimization necessary - filter along with a mapping
                it.percentile.value >= growMoreThenPercentile.value
            }

        return GrowMoreThenPercentileResult(
            growChangeStatResult = stat,
            percentile = growMoreThenPercentile,
            growPercentiles = growsPercentiles
        )
    }

    companion object : KLogging()
}

fun Iterable<Double>.quartiles(): List<Double> {
    return listOf(
        this.percentile(25.0),
        this.percentile(50.0),
        this.percentile(75.0),
        this.percentile(100.0)
    )
}

fun Iterable<Double>.percentiles(): List<Double> {
    return ValidPercentilesEnum.all.map {
        this.percentile(it.value.toDouble())
    }
}


