package org.aklimov.fall_analytics.lib.services.copmutation

import org.aklimov.fall_analytics.lib.services.domain.FallDetectResult
import org.aklimov.fall_analytics.lib.services.domain.Point
import org.aklimov.fall_analytics.lib.services.domain.Ticker
import org.jetbrains.exposed.sql.transactions.transaction
import java.sql.SQLException
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.util.*
import kotlin.math.abs

class Utils private constructor() {

    companion object{

        @Throws(SQLException::class)
        fun loadData(ticker: Ticker): Set<Point>{
            val data = mutableSetOf<Point>()

            transaction{
                connection.createStatement().executeQuery("SELECT tradedate, close FROM ${ticker.value}").use{
                    while (it.next()) {
                        data.add(
                            Point(
                                LocalDate.ofInstant(
                                    Instant.ofEpochMilli(
                                        it.getTimestamp("tradedate").time
                                    ),
                                    ZoneId.systemDefault()
                                ),
                                it.getDouble("close")
                            )
                        )
                    }
                }
            }

            return data
        }


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

                    if (p.price >= startPoint.price) {
                        break
                    } else if (computeAbsChng(startPoint.price, p.price) > fallChng) {
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

        //#### END COMPANION OBJECT #####
    }
}
