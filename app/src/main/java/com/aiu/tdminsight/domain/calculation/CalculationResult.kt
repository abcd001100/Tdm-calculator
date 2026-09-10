package com.aiu.tdminsight.domain.calculation

import com.aiu.tdminsight.domain.model.TdmResult

/**
 * Outcome of running the calculation engine. A [Failure] is a real,
 * expected outcome (e.g. a mathematically invalid input slipped past
 * validation, or a downstream guard tripped) — not something the UI
 * should treat as a crash. Case Study §8 asks for clear messages that
 * distinguish an error from information that just needs review; this
 * type is what carries that message back to the UI.
 */
sealed class CalculationResult {
    data class Success(val result: TdmResult) : CalculationResult()
    data class Failure(val message: String) : CalculationResult()
}
