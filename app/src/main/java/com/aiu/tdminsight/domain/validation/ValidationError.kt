package com.aiu.tdminsight.domain.validation

/**
 * A single validation failure, with a message already phrased for
 * display to the user (Case Study §8: "Error messages should be
 * understandable to the user").
 */
sealed class ValidationError(val message: String) {
    data class Required(val field: String) :
        ValidationError("$field is required.")

    data class InvalidNumber(val field: String) :
        ValidationError("$field must be a valid number.")

    data class OutOfRange(val field: String, val min: Double?, val max: Double?) :
        ValidationError(
            when {
                min != null && max != null -> "$field must be between $min and $max."
                min != null -> "$field must be at least $min."
                max != null -> "$field must be at most $max."
                else -> "$field is out of range."
            }
        )

    data class InvalidTiming(val field: String, val reason: String) :
        ValidationError("$field: $reason")
}
