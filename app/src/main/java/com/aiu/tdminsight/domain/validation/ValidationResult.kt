package com.aiu.tdminsight.domain.validation

/**
 * Outcome of validating one piece of input. Deliberately not a single
 * nullable value + error list — [Invalid] always carries every error
 * found, not just the first one, so a form can show all problems at
 * once instead of one at a time.
 */
sealed class ValidationResult<out T> {
    data class Valid<T>(val value: T) : ValidationResult<T>()
    data class Invalid(val errors: List<ValidationError>) : ValidationResult<Nothing>()
}
