package org.aklimov.fall_analytics.lib.services.domain

import java.util.*

data class GainFromEndOfFallResult(
    val success: SortedSet<PossibleProfitPoint>,
    val fail: SortedSet<PossibleProfitPoint>
)
