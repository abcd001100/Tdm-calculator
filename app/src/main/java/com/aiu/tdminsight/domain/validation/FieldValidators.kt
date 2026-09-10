package com.aiu.tdminsight.domain.validation

/**
 * Reusable single-field validators. Every workflow-level validator below
 * is built out of these instead of re-parsing/re-checking numbers by
 * hand, so a fix to "what counts as a valid number" only has to happen
 * in one place.
 */

fun validateRequiredDouble(
    raw: String,
    field: String,
    min: Double? = null,
    max: Double? = null
): ValidationResult<Double> {
    val trimmed = raw.trim()
    if (trimmed.isEmpty()) {
        return ValidationResult.Invalid(listOf(ValidationError.Required(field)))
    }

    val parsed = trimmed.toDoubleOrNull()
    if (parsed == null || parsed.isNaN() || parsed.isInfinite()) {
        return ValidationResult.Invalid(listOf(ValidationError.InvalidNumber(field)))
    }

    if ((min != null && parsed < min) || (max != null && parsed > max)) {
        return ValidationResult.Invalid(listOf(ValidationError.OutOfRange(field, min, max)))
    }

    return ValidationResult.Valid(parsed)
}

fun validateRequiredInt(
    raw: String,
    field: String,
    min: Int? = null,
    max: Int? = null
): ValidationResult<Int> {
    val trimmed = raw.trim()
    if (trimmed.isEmpty()) {
        return ValidationResult.Invalid(listOf(ValidationError.Required(field)))
    }

    val parsed = trimmed.toIntOrNull()
        ?: return ValidationResult.Invalid(listOf(ValidationError.InvalidNumber(field)))

    if ((min != null && parsed < min) || (max != null && parsed > max)) {
        return ValidationResult.Invalid(listOf(ValidationError.OutOfRange(field, min?.toDouble(), max?.toDouble())))
    }

    return ValidationResult.Valid(parsed)
}

fun <T> validateRequiredSelection(value: T?, field: String): ValidationResult<T> {
    return if (value == null) {
        ValidationResult.Invalid(listOf(ValidationError.Required(field)))
    } else {
        ValidationResult.Valid(value)
    }
}
