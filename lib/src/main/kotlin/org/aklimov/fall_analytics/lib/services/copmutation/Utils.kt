package org.aklimov.fall_analytics.lib.services.copmutation

import org.aklimov.fall_analytics.lib.services.domain.FallDetectResult
import org.aklimov.fall_analytics.lib.services.domain.Point
import java.util.*
import kotlin.math.abs

class Utils private constructor() {

    companion object {

        fun fallDetect(data: NavigableSet<Point>, fallChng: Double): List<FallDetectResult> {
            if (data.size < 2) return emptyList()

            val resList: MutableList<FallDetectResult> = ArrayList<FallDetectResult>()

            var startPoint: Point = data.first()
            while (true) {
                val tail = data.tailSet(startPoint, false)
                if (tail.isEmpty()) break

                lateinit var lastCheckedPointAfterStart: Point
                for (p in tail) {
                    lastCheckedPointAfterStart = p

                    if (p.close >= startPoint.close) {
                        break
                    } else if (computeAbsChng(startPoint.close, p.close) > fallChng) {
                        resList.add(FallDetectResult(startPoint, p))
                        break
                    }
                }

                startPoint = lastCheckedPointAfterStart
            }

            return resList
        }


        private fun computeAbsChng(base: Double, other: Double): Double = abs(computeChng(base, other))


        fun computeChng(base: Double, other: Double): Double = (other - base) / base

        fun checkListOfGrow(points: List<Point>): Boolean {
            val priceList =
                listOf(points.first().close, points[1].open) + points.subList(1, points.size).map(Point::close)
            val minPrice = requireNotNull(priceList.minOrNull())
            return (points.first().close == minPrice) or (points[1].open == minPrice)
        }

        fun computeChngOfWnd(points: List<Point>): Double {
            val base = minOf(points.first().close, points[1].open)
            val maxCloseOfWnd = requireNotNull(
                points.subList(1, points.size).map(Point::close).maxOrNull()
            )
            return computeChng(base, maxCloseOfWnd)
        }

        //#### END COMPANION OBJECT #####
    }
}
