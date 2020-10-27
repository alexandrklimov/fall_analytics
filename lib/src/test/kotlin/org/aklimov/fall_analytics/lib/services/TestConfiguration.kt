package org.aklimov.fall_analytics.lib.services

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.core.JdbcTemplate
import javax.sql.DataSource

@Configuration
class TestConfiguration {

    @Bean
    fun jdbcTemplate(ds: DataSource) = JdbcTemplate(ds)

}
