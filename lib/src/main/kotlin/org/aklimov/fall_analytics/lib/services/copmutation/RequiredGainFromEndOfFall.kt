package org.aklimov.fall_analytics.lib.services.copmutation

import com.google.gson.GsonBuilder
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializer
import org.aklimov.fall_analytics.lib.services.copmutation.Utils.Companion.computeChng
import org.aklimov.fall_analytics.lib.services.copmutation.Utils.Companion.fallDetect
import org.aklimov.fall_analytics.lib.services.copmutation.Utils.Companion.loadData
import org.aklimov.fall_analytics.lib.services.domain.FallDetectResult
import org.aklimov.fall_analytics.lib.services.domain.Point
import org.aklimov.fall_analytics.lib.services.domain.PossibleProfitPoint
import org.aklimov.fall_analytics.shared.Ticker
import java.time.LocalDate
import java.util.*
import java.util.stream.Collectors

class RequiredGainFromEndOfFall {
    fun compute(
        ticker: Ticker,
        fallChng: Double = 0.15,
        printResult: Boolean = true
    ): Pair<SortedSet<PossibleProfitPoint>, SortedSet<PossibleProfitPoint>> {
        val data: NavigableSet<Point> = loadData(ticker).run {
            val ts = TreeSet(Comparator.comparing(Point::date))
            ts.addAll(this)
            ts
        }

        val listOfFalls: List<FallDetectResult> = fallDetect(data, fallChng)

        println("############################")
        println("Detected Falls: " + listOfFalls.size)
        listOfFalls.forEach(System.out::println)
        println("############################")

        val possibleProfitPoints = listOfFalls.stream().map { fallDetectResult ->
            //Search the first point after fall's end one
            //gives us required profit
            var maxChngDuringProfitLvlSearch = Double.NEGATIVE_INFINITY
            val profitPoint: Point? = data.tailSet(fallDetectResult.end, false).firstOrNull { p ->
                val tmpChng = computeChng(fallDetectResult.end.close, p.close)

                if (tmpChng > maxChngDuringProfitLvlSearch) maxChngDuringProfitLvlSearch = tmpChng

                tmpChng >= fallChng
            }

            //If there is a profit point
            //search a minimum one
            //between fall's end an the profit point
            //AND
            //search max possible profit
            var lowest: Point? = null
            var maxPossibleProfitPoint: Point? = null
            if (profitPoint != null) {
                lowest = data.subSet(fallDetectResult.end, profitPoint).minByOrNull(Point::close)
                for (p in data.tailSet(profitPoint)) {
                    if (p.close < fallDetectResult.end.close && p.close < profitPoint.close) {
                        break
                    } else if (p.close > profitPoint.close &&
                        (maxPossibleProfitPoint == null || p.close > maxPossibleProfitPoint.close)
                    ) {
                        maxPossibleProfitPoint = p
                    }
                }
            }
            PossibleProfitPoint(
                fallDetectResult, maxChngDuringProfitLvlSearch, profitPoint, lowest, maxPossibleProfitPoint
            )
        }.collect(Collectors.toList())

        val success = possibleProfitPoints.filter { it.point != null }
            .toSortedSet(Comparator.comparing { it.fdr.base.date })
        val fail = possibleProfitPoints.filter { it.point == null }
            .toSortedSet(Comparator.comparing { it.fdr.base.date })

        if (printResult) printResult(fallChng, success, fail)

        return Pair(success, fail)
    }


    private fun printResult(
        fallChngLevel: Double,
        success: SortedSet<PossibleProfitPoint>,
        fail: SortedSet<PossibleProfitPoint>)
    {
        System.out.println(
            """
                
            #############################################################       
            ${"\t"}PROFIT POINTS (${success.size}) for Fall Change ${fallChngLevel * 100}%
            #############################################################
            """.trimIndent()
        )
        println("\n\t>>> POSITIVES <<<\n")
        success.forEach { possibleProfitPoint ->
            System.out.println(
                GsonBuilder().setPrettyPrinting()
                    .registerTypeAdapter(
                        LocalDate::class.java,
                        JsonSerializer<String> { src, _, _ -> JsonPrimitive(src) }
                    )
                    .registerTypeAdapter(
                        Point::class.java,
                        JsonSerializer<Point> { src, _, _ ->
                            JsonPrimitive("${src.date.toString()} | ${src.close}")
                        }
                    ).create().toJson(possibleProfitPoint)
            )
        }

        println("\n\t>>> NEGATIVES <<<\n")
        fail.forEach { p ->
            println(
                "${p.fdr.base} -> ${p.fdr.end} | max profit during sarch ${p.maxChngDuringProfitLvlSearch}"
            )
        }
    }
}
