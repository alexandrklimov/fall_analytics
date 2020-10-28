package org.aklimov.fall_analytics.lib.services

import io.ktor.client.*
import io.ktor.client.engine.apache.*
import io.ktor.client.features.*
import io.ktor.client.request.*
import kotlinx.coroutines.runBlocking
import org.aklimov.fall_analytics.lib.services.data.MoexISSHistoryLoader
import org.aklimov.fall_analytics.lib.services.data.MoexISSHistorySaverImpl
import org.aklimov.fall_analytics.lib.services.data.MoexISSInfoProvider
import org.jetbrains.exposed.sql.Database
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.jdbc.core.JdbcTemplate
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
class MoexISSHistoryLoaderTest {

    @Autowired
    lateinit var jdbcTemplate: JdbcTemplate

    val httpClient = HttpClient(Apache) {
        defaultRequest {
            header("Connection", "keep-alive")
        }
    }

    @Test
    fun test() {
        Database.connect({
            DriverManager.getConnection("jdbc:postgresql://localhost/fall_analytics?user=postgres&password=q1")
        })

        runBlocking {
            val loader = MoexISSHistorySaverImpl(jdbcTemplate, MoexISSHistoryLoader(httpClient))

            MoexISSInfoProvider(httpClient).loadTqbrShares().forEach {
                print("\n##### ${it.value.toUpperCase()} #####\n")
                loader.actualize(it)
            }
        }
    }

}
