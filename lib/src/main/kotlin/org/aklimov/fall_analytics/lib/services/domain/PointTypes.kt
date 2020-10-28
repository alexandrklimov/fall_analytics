package org.aklimov.fall_analytics.lib.services.domain

import org.aklimov.fall_analytics.lib.services.copmutation.Utils
import java.time.LocalDate
import java.util.*

data class FallDetectResult(val base: Point, val end: Point)

data class Point(val date: LocalDate, val price: Double)

class PossibleProfitPoint(
    val fdr: FallDetectResult,
    val maxChngDuringProfitLvlSearch: Double,
    val point: Point?,
    val lowest: Point?,
    val maxPossibleProfit: Point?
) {
    val lowestChangeFromStartPct: Double? = if (lowest == null) {
        null
    } else {
        Utils.computeChng(fdr.base.price, lowest.price) * 100
    }

    val maxPossibleProfitFromEndPct: Double? = if (maxPossibleProfit == null) {
        null
    } else {
        Utils.computeChng(fdr.end.price, maxPossibleProfit.price) * 100
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is PossibleProfitPoint) return false

        if (fdr != other.fdr) return false
        if (maxChngDuringProfitLvlSearch != other.maxChngDuringProfitLvlSearch) return false
        if (point != other.point) return false
        if (lowest != other.lowest) return false
        if (lowestChangeFromStartPct != other.lowestChangeFromStartPct) return false
        if (maxPossibleProfit != other.maxPossibleProfit) return false
        if (maxPossibleProfitFromEndPct != other.maxPossibleProfitFromEndPct) return false

        return true
    }

    override fun hashCode(): Int {
        var result = fdr.hashCode()
        result = 31 * result + maxChngDuringProfitLvlSearch.hashCode()
        result = 31 * result + (point?.hashCode() ?: 0)
        result = 31 * result + (lowest?.hashCode() ?: 0)
        result = 31 * result + (lowestChangeFromStartPct?.hashCode() ?: 0)
        result = 31 * result + (maxPossibleProfit?.hashCode() ?: 0)
        result = 31 * result + (maxPossibleProfitFromEndPct?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String {
        return """PossibleProfitPoint(
                    fdr=$fdr, 
                    maxChngDuringProfitLvlSearch=$maxChngDuringProfitLvlSearch, 
                    point=$point, 
                    lowest=$lowest, 
                    lowestChangeFromStartPct=$lowestChangeFromStartPct, 
                    maxPossibleProfit=$maxPossibleProfit, 
                    maxPossibleProfitFromEndPct=$maxPossibleProfitFromEndPct
                )""".trimIndent()
    }


}
