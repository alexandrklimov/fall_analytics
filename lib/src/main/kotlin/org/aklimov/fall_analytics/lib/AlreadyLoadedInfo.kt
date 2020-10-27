package org.aklimov.fall_analytics.lib

import java.time.LocalDate

data class AlreadyLoadedInfo(val tradeDate: LocalDate, val lastRowNum: Long)
