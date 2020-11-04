package org.aklimov.fall_analytics.server

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.aklimov.fall_analytics.shared.TestDto
import org.junit.jupiter.api.Test

class TestDtoTest {

    @Test
    fun testSerialization(){
        println(
            Json.encodeToString(
                TestDto("Server serialization")
            )
        )
    }

}
