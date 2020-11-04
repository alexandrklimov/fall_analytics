package org.aklimov.fall_analytics.shared

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.test.Test

class MainJsTest {

    @Test
    fun serializationJsTest(){
        println("Run platform-specific test: (1+2) eval result is [${eval("1+2")}]")
        println(
            Json.encodeToString(
                TestDto("MainJsTest")
            )
        )
    }
}
