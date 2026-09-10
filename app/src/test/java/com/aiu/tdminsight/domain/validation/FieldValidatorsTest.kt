package com.aiu.tdminsight.domain.validation

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class FieldValidatorsTest {

    @Test
    fun `blank input is required error`() {
        val result = validateRequiredDouble("", "Weight")
        assertTrue(result is ValidationResult.Invalid)
        val errors = (result as ValidationResult.Invalid).errors
        assertEquals(1, errors.size)
        assertTrue(errors[0] is ValidationError.Required)
    }

    @Test
    fun `whitespace-only input is required error`() {
        val result = validateRequiredDouble("   ", "Weight")
        assertTrue(result is ValidationResult.Invalid)
    }

    @Test
    fun `non-numeric input is invalid number error`() {
        val result = validateRequiredDouble("abc", "Weight")
        assertTrue(result is ValidationResult.Invalid)
        val errors = (result as ValidationResult.Invalid).errors
        assertTrue(errors[0] is ValidationError.InvalidNumber)
    }

    @Test
    fun `NaN and infinity are rejected as invalid numbers`() {
        assertTrue(validateRequiredDouble("NaN", "Weight") is ValidationResult.Invalid)
        assertTrue(validateRequiredDouble("Infinity", "Weight") is ValidationResult.Invalid)
    }

    @Test
    fun `value below minimum is out of range error`() {
        val result = validateRequiredDouble("0.5", "Weight", min = 1.0, max = 400.0)
        assertTrue(result is ValidationResult.Invalid)
        val errors = (result as ValidationResult.Invalid).errors
        assertTrue(errors[0] is ValidationError.OutOfRange)
    }

    @Test
    fun `value above maximum is out of range error`() {
        val result = validateRequiredDouble("500", "Weight", min = 1.0, max = 400.0)
        assertTrue(result is ValidationResult.Invalid)
    }

    @Test
    fun `value within range is valid`() {
        val result = validateRequiredDouble("70.5", "Weight", min = 1.0, max = 400.0)
        assertEquals(ValidationResult.Valid(70.5), result)
    }

    @Test
    fun `valid integer within range is valid`() {
        val result = validateRequiredInt("45", "Age", min = 0, max = 120)
        assertEquals(ValidationResult.Valid(45), result)
    }

    @Test
    fun `decimal input for an integer field is invalid number`() {
        val result = validateRequiredInt("45.5", "Age", min = 0, max = 120)
        assertTrue(result is ValidationResult.Invalid)
    }

    @Test
    fun `null selection is required error`() {
        val result = validateRequiredSelection<String>(null, "Sex")
        assertTrue(result is ValidationResult.Invalid)
    }

    @Test
    fun `non-null selection is valid`() {
        val result = validateRequiredSelection("MALE", "Sex")
        assertEquals(ValidationResult.Valid("MALE"), result)
    }
}
