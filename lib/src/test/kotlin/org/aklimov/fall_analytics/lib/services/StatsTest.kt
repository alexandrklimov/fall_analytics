package org.aklimov.fall_analytics.lib.services

import org.aklimov.fall_analytics.lib.services.copmutation.Stats
import org.aklimov.fall_analytics.lib.services.dao.SqlOhlcDao
import org.aklimov.fall_analytics.lib.services.domain.Ticker
import org.aklimov.fall_analytics.lib.services.domain.ValidPercentilesEnum
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate

@SpringBootTest(
    args = [
        "--spring.datasource.url=jdbc:postgresql://localhost/fall_analytics?user=postgres&password=q1",
        "--spring.datasource.driver-class-name=org.postgresql.Driver",
        "--spring.main.web-application-type=none"
    ],
    classes = [TestConfiguration::class]
)
@EnableAutoConfiguration
@Disabled
class StatsTest {

    @Autowired
    lateinit var jdbcTpl: NamedParameterJdbcTemplate

    @Test
    fun test() {
        val growChangeStat = Stats(SqlOhlcDao(jdbcTpl)).growChangeStat(3, Ticker("gazp"))
        println(growChangeStat)

        Stats(SqlOhlcDao(jdbcTpl)).searchGrowMoreThenPercentile(growChangeStat, ValidPercentilesEnum.P_95)
            .growPercentiles.forEach(::println)
    }

}
