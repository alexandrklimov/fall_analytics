package org.aklimov.fall_analytics.server

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.util.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement

import org.aklimov.fall_analytics.shared.TestDto

fun main(){
    val server = embeddedServer(Netty, 8080) {
        routing {
            route("/api"){
                get("/echo") {
                    val respDto = TestDto(call.parameters.getOrFail("message"))
                    call.respondText(
                        Json.encodeToString(respDto),
                        ContentType.Application.Json
                    )
                }
            }
        }
    }
    server.start(wait = true)
}
