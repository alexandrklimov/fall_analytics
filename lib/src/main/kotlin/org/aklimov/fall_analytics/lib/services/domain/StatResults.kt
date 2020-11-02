package org.aklimov.fall_analytics.lib.services.domain

data class GrowChangeStatRes(
    val period: Int,
    val mean: Double,
    val median: Double,
    val quartiles: List<Double>,
    val percentiles: List<Double>,
    val ticker: Ticker
)

data class GrowPercentile(
    val lastPeriodPoint: Point,
    val chage: Double,
    val percentile: ValidPercentilesEnum,
    val percentileVal: Double
)

data class GrowMoreThenPercentileResult(
    val growChangeStatResult: GrowChangeStatRes,
    val percentile: ValidPercentilesEnum,
    val growPercentiles: List<GrowPercentile>
)
