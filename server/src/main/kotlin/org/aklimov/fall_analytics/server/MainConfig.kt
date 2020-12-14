package org.aklimov.fall_analytics.server

import org.aklimov.fall_analytics.lib.services.copmutation.RequiredGainFromEndOfFallFinder
import org.aklimov.fall_analytics.lib.services.dao.OhlcDao
import org.aklimov.fall_analytics.lib.services.dao.SqlOhlcDao
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.jdbc.support.JdbcTransactionManager
import javax.sql.DataSource

@Configuration
class MainConfig{

    @Bean
    fun jdbcNamedTemplate(ds: DataSource) = NamedParameterJdbcTemplate(ds)
    @Bean
    fun txManager(ds: DataSource) = JdbcTransactionManager(ds)
    @Bean
    fun ohlcDao(jdbcTpl: NamedParameterJdbcTemplate): OhlcDao = SqlOhlcDao(jdbcTpl)
    @Bean
    fun requiredGainFromEndOfFallFinder(ohlcDao: OhlcDao) = RequiredGainFromEndOfFallFinder(ohlcDao)
}
