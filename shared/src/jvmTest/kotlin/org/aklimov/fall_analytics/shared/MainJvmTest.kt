package org.aklimov.fall_analytics.shared

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Test

class MainJvmTest {
    @Test
    fun serializationJvmTest(){
        println("Run platform-specific test: ${Runtime.version()}")
        println(
            Json.encodeToString(
                TestDto("MainJvmTest")
            )
        )
    }

}
