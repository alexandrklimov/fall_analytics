package org.aklimov.fall_analytics.lib.services.domain

data class GrowChangeStatRes(
    val period: Int,
    val mean: Double,
    val median: Double,
    val quartiles: List<Double>,
    val percentiles: List<Double>,
    val ticker: Ticker
)
