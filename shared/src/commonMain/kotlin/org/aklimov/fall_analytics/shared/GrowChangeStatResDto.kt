package org.aklimov.fall_analytics.shared

import kotlinx.serialization.Serializable
import kotlin.js.JsExport

//@kotlin.js.ExperimentalJsExport
//@JsExport
@Serializable
data class GrowChangeStatResDto(
    val period: Int,
    val mean: Double,
    val median: Double,
    val quartiles: Array<Double>,
    val percentiles: Array<Double>,
    val ticker: TickerDto
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is GrowChangeStatResDto) return false

        if (period != other.period) return false
        if (mean != other.mean) return false
        if (median != other.median) return false
        if (!quartiles.contentEquals(other.quartiles)) return false
        if (!percentiles.contentEquals(other.percentiles)) return false
        if (ticker != other.ticker) return false

        return true
    }

    override fun hashCode(): Int {
        var result = period
        result = 31 * result + mean.hashCode()
        result = 31 * result + median.hashCode()
        result = 31 * result + quartiles.contentHashCode()
        result = 31 * result + percentiles.contentHashCode()
        result = 31 * result + ticker.hashCode()
        return result
    }
}
