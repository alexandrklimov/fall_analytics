import io.ktor.client.*
import io.ktor.client.engine.js.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.request.*
import io.ktor.utils.io.core.*
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.coroutines.*
import kotlinx.html.button
import kotlinx.html.div
import kotlinx.html.dom.append
import kotlinx.html.dom.create
import kotlinx.html.js.onClickFunction
import kscience.plotly.layout
import kscience.plotly.models.ScatterMode
import kscience.plotly.models.TraceType
import kscience.plotly.plot
import kscience.plotly.scatter
import org.aklimov.fall_analytics.shared.TestDto
import kotlin.random.Random

fun main() {
    window.addEventListener("load", {

        with(requireNotNull(document.body)) {
            append {
                div {
                    id = "panel"
                    button {
                        +"Test Dto"
                        title = "Test DTO"
                        onClickFunction = { _ ->
                            GlobalScope.launch {
                                HttpClient(Js) {
                                    install(JsonFeature) {
                                        serializer = KotlinxSerializer()
                                    }
                                }.use { client ->
                                    val resp = client.get<TestDto>("//localhost:8082/api/echo?message=ololo11")
                                    document.getElementById("panel")?.append {
                                        div { +"Response message: ${resp.message}" }
                                    }
                                }
                            }

                        }
                    }
                }

                /*val chart = document.create.div {
                    id = "chart"
                }
                append(chart)
                println("<chart> div created and attached")

                chart.plot {
                    scatter {
                        x(1, 2, 3, 4)
                        y(10, 15, 13, 17)
                        mode = ScatterMode.markers
                        type = TraceType.scatter
                    }
                    scatter {
                        x(2, 3, 4, 5)
                        y(10, 15, 13, 17)
                        mode = ScatterMode.lines
                        type = TraceType.scatter
                        marker {
                            GlobalScope.launch {
                                while (isActive) {
                                    delay(500)
                                    if (Random.nextBoolean()) {
                                        color("magenta")
                                    } else {
                                        color("blue")
                                    }
                                }
                            }
                        }
                    }
                    scatter {
                        x(1, 2, 3, 4)
                        y(12, 5, 2, 12)
                        mode = ScatterMode.`lines+markers`
                        type = TraceType.scatter
                        marker {
                            color("red")
                        }
                    }
                    layout {
                        title = "Line and Scatter Plot"
                    }
                }*/
            }
        }
    })
}

