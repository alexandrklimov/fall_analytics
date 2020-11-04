package org.aklimov.fall_analytics.lib.services

import kotlinx.coroutines.runBlocking
import org.aklimov.fall_analytics.lib.services.copmutation.RequiredGainFromEndOfFall
import org.aklimov.fall_analytics.shared.Ticker
import org.jetbrains.exposed.sql.Database
import org.junit.jupiter.api.Test
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import java.sql.DriverManager

@SpringBootTest(
    args = [
        "--spring.datasource.url=jdbc:postgresql://localhost/fall_analytics?user=postgres&password=q1",
        "--spring.datasource.driver-class-name=org.postgresql.Driver",
        "--spring.main.web-application-type=none"
    ],
    classes = [TestConfiguration::class]
)
@EnableAutoConfiguration
class RequiredGainFromEndOfFallTest {

    @Test
    fun test() {
        Database.connect({
            DriverManager.getConnection("jdbc:postgresql://localhost/fall_analytics?user=postgres&password=q1")
        })

        runBlocking {
            RequiredGainFromEndOfFall().compute(Ticker("gazp"))
        }
    }

}
