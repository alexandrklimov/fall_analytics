package org.aklimov.fall_analytics.lib.services.domain

import java.time.LocalDate

data class OHLC(
    val date: LocalDate,
    val open: Double,
    val high: Double,
    val low: Double,
    val close: Double
)
