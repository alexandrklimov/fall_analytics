package org.aklimov.fall_analytics.lib.services.domain

class Ticker(tickerArg: String) {
    val value = tickerArg.toLowerCase()
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Ticker) return false

        if (value != other.value) return false

        return true
    }

    override fun hashCode(): Int {
        return value.hashCode()
    }

    override fun toString(): String {
        return "Ticker(value='$value')"
    }
}
