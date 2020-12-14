package org.aklimov.fall_analytics.lib.services

import kotlinx.coroutines.runBlocking
import org.aklimov.fall_analytics.lib.services.copmutation.RequiredGainFromEndOfFallFinder
import org.aklimov.fall_analytics.lib.services.dao.SqlOhlcDao
import org.aklimov.fall_analytics.lib.services.domain.Ticker
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
class RequiredGainFromEndOfFallFinderTest {

    @Autowired
    lateinit var jdbcTpl: NamedParameterJdbcTemplate

    @Test
    fun test() {
        runBlocking {
            RequiredGainFromEndOfFallFinder(SqlOhlcDao(jdbcTpl)).compute(Ticker("gazp"))
        }
    }

}
