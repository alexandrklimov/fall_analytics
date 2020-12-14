package org.aklimov.fall_analytics.shared

import kotlinx.serialization.Serializable
import kotlin.js.JsExport

/*@kotlin.js.ExperimentalJsExport
@JsExport*/
@Serializable
data class TestDto(val message: String)
