package com.aiu.tdminsight.domain.validation

import com.aiu.tdminsight.domain.model.BiologicalSex
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class WorkflowInputValidatorTest {

    private val validPatient = PatientInfoRawInput(
        weightKg = "70",
        ageYears = "45",
        sex = BiologicalSex.MALE,
        serumCreatinineMgDl = "1.0"
    )

    private val validDose = DoseInfoRawInput(
        doseMg = "1000",
        infusionDurationMinutes = "60",
        dosingIntervalHours = "12"
    )

    @Test
    fun `valid pre workflow input parses successfully`() {
        val result = validatePreWorkflowInput(
            patientRaw = validPatient,
            doseRaw = validDose,
            preDoseConcentrationMgLRaw = "12.5",
            preDoseSampleTimeBeforeDoseHoursRaw = "0.5"
        )
        assertTrue(result is ValidationResult.Valid)
    }

    @Test
    fun `pre workflow rejects zero concentration to guard against later log-domain errors`() {
        val result = validatePreWorkflowInput(
            patientRaw = validPatient,
            doseRaw = validDose,
            preDoseConcentrationMgLRaw = "0",
            preDoseSampleTimeBeforeDoseHoursRaw = "0.5"
        )
        assertTrue(result is ValidationResult.Invalid)
    }

    @Test
    fun `pre workflow rejects a sample time older than one dosing interval`() {
        val result = validatePreWorkflowInput(
            patientRaw = validPatient,
            doseRaw = validDose, // 12h interval
            preDoseConcentrationMgLRaw = "12.5",
            preDoseSampleTimeBeforeDoseHoursRaw = "13"
        )
        assertTrue(result is ValidationResult.Invalid)
        val errors = (result as ValidationResult.Invalid).errors
        assertTrue(errors.any { it is ValidationError.InvalidTiming })
    }

    @Test
    fun `valid post workflow input parses successfully`() {
        val result = validatePostWorkflowInput(
            patientRaw = validPatient,
            doseRaw = validDose,
            postDoseConcentrationMgLRaw = "30",
            postDoseSampleTimeAfterInfusionHoursRaw = "1"
        )
        assertTrue(result is ValidationResult.Valid)
    }

    @Test
    fun `post workflow rejects a sample time at or after the next dose is due`() {
        val result = validatePostWorkflowInput(
            patientRaw = validPatient,
            doseRaw = validDose, // 12h interval
            postDoseConcentrationMgLRaw = "30",
            postDoseSampleTimeAfterInfusionHoursRaw = "12"
        )
        assertTrue(result is ValidationResult.Invalid)
        val errors = (result as ValidationResult.Invalid).errors
        assertTrue(errors.any { it is ValidationError.InvalidTiming })
    }

    @Test
    fun `valid pre-post workflow input parses successfully`() {
        val result = validatePrePostWorkflowInput(
            patientRaw = validPatient,
            doseRaw = validDose,
            preDoseConcentrationMgLRaw = "12.5",
            preDoseSampleTimeBeforeDoseHoursRaw = "0.5",
            postDoseConcentrationMgLRaw = "30",
            postDoseSampleTimeAfterInfusionHoursRaw = "1"
        )
        assertTrue(result is ValidationResult.Valid)
    }

    @Test
    fun `pre-post workflow accumulates errors from both concentration fields at once`() {
        val result = validatePrePostWorkflowInput(
            patientRaw = validPatient,
            doseRaw = validDose,
            preDoseConcentrationMgLRaw = "",
            preDoseSampleTimeBeforeDoseHoursRaw = "0.5",
            postDoseConcentrationMgLRaw = "",
            postDoseSampleTimeAfterInfusionHoursRaw = "1"
        )
        assertTrue(result is ValidationResult.Invalid)
        val errors = (result as ValidationResult.Invalid).errors
        assertEquals(2, errors.count { it is ValidationError.Required })
    }

    @Test
    fun `review warning is raised when post concentration is not higher than pre concentration`() {
        val result = validatePrePostWorkflowInput(
            patientRaw = validPatient,
            doseRaw = validDose,
            preDoseConcentrationMgLRaw = "20",
            preDoseSampleTimeBeforeDoseHoursRaw = "0.5",
            postDoseConcentrationMgLRaw = "15",
            postDoseSampleTimeAfterInfusionHoursRaw = "1"
        )
        val valid = result as ValidationResult.Valid
        val warnings = reviewWarnings(valid.value)
        assertEquals(1, warnings.size)
    }

    @Test
    fun `missing patient sex is a required error`() {
        val result = validatePreWorkflowInput(
            patientRaw = validPatient.copy(sex = null),
            doseRaw = validDose,
            preDoseConcentrationMgLRaw = "12.5",
            preDoseSampleTimeBeforeDoseHoursRaw = "0.5"
        )
        assertTrue(result is ValidationResult.Invalid)
    }
}
