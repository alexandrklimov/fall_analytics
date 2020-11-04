package org.aklimov.fall_analytics.lib.services

import org.aklimov.fall_analytics.lib.services.copmutation.Stats
import org.aklimov.fall_analytics.lib.services.domain.ValidPercentilesEnum
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
class StatsTest {

    @Test
    fun test() {
        Database.connect({
            DriverManager.getConnection("jdbc:postgresql://localhost/fall_analytics?user=postgres&password=q1")
        })

        val growChangeStat = Stats().growChangeStat(3, Ticker("gazp"))
        println(growChangeStat)

        Stats().searchGrowMoreThenPercentile(growChangeStat, ValidPercentilesEnum.P_95)
            .growPercentiles.forEach(::println)
    }

}
