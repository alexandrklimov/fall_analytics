package org.aklimov.fall_analytics.lib.services.domain

import java.time.LocalDate

data class AlreadyLoadedInfo(val tradeDate: LocalDate, val lastRowNum: Long)
