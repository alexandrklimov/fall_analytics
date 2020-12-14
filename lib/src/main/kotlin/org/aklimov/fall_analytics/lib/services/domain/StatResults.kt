package org.aklimov.fall_analytics.lib.services.domain

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
