package org.aklimov.fall_analytics.lib.services.dao

import java.sql.ResultSet
import java.sql.Timestamp
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

object Utils {
    //language=sql
    val CHECK_TABLE_SQL_TPL = """
        SELECT COUNT(*) AS is_exists
        FROM information_schema.tables
        WHERE LOWER(table_name)=:tableName
    """.trimIndent()

    //language=sql
    val LIST_TICKERS_SQL_TPL = """
        SELECT LOWER(table_name) as table_name
        FROM information_schema.tables
        WHERE table_schema='public' AND table_type='BASE TABLE'
        ORDER BY table_name
    """.trimIndent()

    fun tmstmpToLocalDate(timestamp: Long, zoneId: ZoneId = ZoneId.systemDefault()): LocalDate {
        return LocalDate.ofInstant(Instant.ofEpochMilli(timestamp), zoneId)
    }
}

fun ResultSet.getLocalDate(columnIndex: Int, zoneId: ZoneId = ZoneId.systemDefault()) =
    Utils.tmstmpToLocalDate(this.getTimestamp(columnIndex).time, zoneId)

fun ResultSet.getLocalDate(columnLabel: String, zoneId: ZoneId = ZoneId.systemDefault()) =
    Utils.tmstmpToLocalDate(this.getTimestamp(columnLabel).time, zoneId)
