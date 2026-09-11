package com.aiu.tdminsight.domain.validation

import com.aiu.tdminsight.domain.model.BiologicalSex
import com.aiu.tdminsight.domain.model.DoseInfo
import com.aiu.tdminsight.domain.model.PatientInfo
import com.aiu.tdminsight.domain.model.WorkflowInput

/**
 * Raw, as-typed form values for [PatientInfo], before parsing/validation.
 */
data class PatientInfoRawInput(
    val weightKg: String,
    val ageYears: String,
    val sex: BiologicalSex?,
    val serumCreatinineMgDl: String
)

/**
 * Raw, as-typed form values for [DoseInfo], before parsing/validation.
 */
data class DoseInfoRawInput(
    val doseMg: String,
    val infusionDurationMinutes: String,
    val dosingIntervalHours: String
)

/**
 * Validates and parses the fields common to every workflow.
 *
 * NOTE ON RANGE BOUNDS: the min/max values below (e.g. weight 1–400 kg)
 * are broad human-physiology sanity bounds chosen as a reasonable
 * software default — they are NOT lecturer-specified clinical reference
 * ranges. If the lecturer provides specific acceptable ranges, replace
 * these. Per CLAUDE.md, no clinical target/reference values are assumed
 * here.
 */
fun validatePatientInfo(raw: PatientInfoRawInput): ValidationResult<PatientInfo> {
    val errors = mutableListOf<ValidationError>()

    val weight = validateRequiredDouble(raw.weightKg, "Weight (kg)", min = 1.0, max = 400.0)
    val age = validateRequiredInt(raw.ageYears, "Age (years)", min = 0, max = 120)
    val sex = validateRequiredSelection(raw.sex, "Sex")
    val serumCreatinine = validateRequiredDouble(
        raw.serumCreatinineMgDl,
        "Serum creatinine (mg/dL)",
        min = 0.1,
        max = 20.0
    )

    errors += weight.errorsOrEmpty()
    errors += age.errorsOrEmpty()
    errors += sex.errorsOrEmpty()
    errors += serumCreatinine.errorsOrEmpty()

    if (errors.isNotEmpty()) return ValidationResult.Invalid(errors)

    return ValidationResult.Valid(
        PatientInfo(
            weightKg = weight.valueOrThrow(),
            ageYears = age.valueOrThrow(),
            sex = sex.valueOrThrow(),
            serumCreatinineMgDl = serumCreatinine.valueOrThrow()
        )
    )
}

/**
 * NOTE ON RANGE BOUNDS: same caveat as [validatePatientInfo] — these are
 * broad sanity bounds, not lecturer-specified dosing limits.
 */
fun validateDoseInfo(raw: DoseInfoRawInput): ValidationResult<DoseInfo> {
    val errors = mutableListOf<ValidationError>()

    val dose = validateRequiredDouble(raw.doseMg, "Dose (mg)", min = 1.0, max = 10000.0)
    val infusionDuration = validateRequiredDouble(
        raw.infusionDurationMinutes,
        "Infusion duration (minutes)",
        min = 1.0,
        max = 600.0
    )
    val interval = validateRequiredDouble(
        raw.dosingIntervalHours,
        "Dosing interval (hours)",
        min = 1.0,
        max = 168.0
    )

    errors += dose.errorsOrEmpty()
    errors += infusionDuration.errorsOrEmpty()
    errors += interval.errorsOrEmpty()

    if (errors.isNotEmpty()) return ValidationResult.Invalid(errors)

    return ValidationResult.Valid(
        DoseInfo(
            doseMg = dose.valueOrThrow(),
            infusionDurationMinutes = infusionDuration.valueOrThrow(),
            dosingIntervalHours = interval.valueOrThrow()
        )
    )
}

/**
 * Validates a Vancomycin Pre workflow's full input: patient + dose +
 * the pre-dose concentration and its sample timing.
 *
 * Cross-field/timing check: the pre-dose sample must have been drawn
 * at some point before this dose (> 0 hours before) and no earlier than
 * one full dosing interval before it — otherwise it isn't really a
 * "pre-dose" sample for *this* dose.
 */
fun validatePreWorkflowInput(
    patientRaw: PatientInfoRawInput,
    doseRaw: DoseInfoRawInput,
    preDoseConcentrationMgLRaw: String,
    preDoseSampleTimeBeforeDoseHoursRaw: String
): ValidationResult<WorkflowInput.Pre> {
    val patient = validatePatientInfo(patientRaw)
    val dose = validateDoseInfo(doseRaw)
    val concentration = validateRequiredDouble(
        preDoseConcentrationMgLRaw,
        "Pre-dose concentration (mg/L)",
        min = 0.01,
        max = 200.0
    )
    val sampleTime = validateRequiredDouble(
        preDoseSampleTimeBeforeDoseHoursRaw,
        "Pre-dose sample time before dose (hours)",
        min = 0.01
    )

    val errors = mutableListOf<ValidationError>()
    errors += patient.errorsOrEmpty()
    errors += dose.errorsOrEmpty()
    errors += concentration.errorsOrEmpty()
    errors += sampleTime.errorsOrEmpty()

    if (errors.isEmpty() && dose is ValidationResult.Valid && sampleTime is ValidationResult.Valid) {
        if (sampleTime.value > dose.value.dosingIntervalHours) {
            errors += ValidationError.InvalidTiming(
                "Pre-dose sample time before dose",
                "must not be longer ago than one dosing interval (${dose.value.dosingIntervalHours}h)."
            )
        }
    }

    if (errors.isNotEmpty()) return ValidationResult.Invalid(errors)

    return ValidationResult.Valid(
        WorkflowInput.Pre(
            patient = patient.valueOrThrow(),
            dose = dose.valueOrThrow(),
            preDoseConcentrationMgL = concentration.valueOrThrow(),
            preDoseSampleTimeBeforeDoseHours = sampleTime.valueOrThrow()
        )
    )
}

/**
 * Validates a Vancomycin Post workflow's full input: patient + dose +
 * the post-dose concentration and its sample timing.
 *
 * Cross-field/timing check: the post-dose sample must have been drawn
 * after the infusion actually finished, and before the next dose is due.
 */
fun validatePostWorkflowInput(
    patientRaw: PatientInfoRawInput,
    doseRaw: DoseInfoRawInput,
    postDoseConcentrationMgLRaw: String,
    postDoseSampleTimeAfterInfusionHoursRaw: String
): ValidationResult<WorkflowInput.Post> {
    val patient = validatePatientInfo(patientRaw)
    val dose = validateDoseInfo(doseRaw)
    val concentration = validateRequiredDouble(
        postDoseConcentrationMgLRaw,
        "Post-dose concentration (mg/L)",
        min = 0.01,
        max = 200.0
    )
    val sampleTime = validateRequiredDouble(
        postDoseSampleTimeAfterInfusionHoursRaw,
        "Post-dose sample time after infusion (hours)",
        min = 0.01
    )

    val errors = mutableListOf<ValidationError>()
    errors += patient.errorsOrEmpty()
    errors += dose.errorsOrEmpty()
    errors += concentration.errorsOrEmpty()
    errors += sampleTime.errorsOrEmpty()

    if (errors.isEmpty() && dose is ValidationResult.Valid && sampleTime is ValidationResult.Valid) {
        if (sampleTime.value >= dose.value.dosingIntervalHours) {
            errors += ValidationError.InvalidTiming(
                "Post-dose sample time after infusion",
                "must be before the next dose is due (dosing interval is ${dose.value.dosingIntervalHours}h)."
            )
        }
    }

    if (errors.isNotEmpty()) return ValidationResult.Invalid(errors)

    return ValidationResult.Valid(
        WorkflowInput.Post(
            patient = patient.valueOrThrow(),
            dose = dose.valueOrThrow(),
            postDoseConcentrationMgL = concentration.valueOrThrow(),
            postDoseSampleTimeAfterInfusionHours = sampleTime.valueOrThrow()
        )
    )
}

/**
 * Validates a combined Pre + Post workflow's full input. Applies both
 * the Pre and Post timing checks above to the same patient/dose.
 */
fun validatePrePostWorkflowInput(
    patientRaw: PatientInfoRawInput,
    doseRaw: DoseInfoRawInput,
    preDoseConcentrationMgLRaw: String,
    preDoseSampleTimeBeforeDoseHoursRaw: String,
    postDoseConcentrationMgLRaw: String,
    postDoseSampleTimeAfterInfusionHoursRaw: String
): ValidationResult<WorkflowInput.PrePost> {
    val patient = validatePatientInfo(patientRaw)
    val dose = validateDoseInfo(doseRaw)
    val preConcentration = validateRequiredDouble(
        preDoseConcentrationMgLRaw,
        "Pre-dose concentration (mg/L)",
        min = 0.01,
        max = 200.0
    )
    val preSampleTime = validateRequiredDouble(
        preDoseSampleTimeBeforeDoseHoursRaw,
        "Pre-dose sample time before dose (hours)",
        min = 0.01
    )
    val postConcentration = validateRequiredDouble(
        postDoseConcentrationMgLRaw,
        "Post-dose concentration (mg/L)",
        min = 0.01,
        max = 200.0
    )
    val postSampleTime = validateRequiredDouble(
        postDoseSampleTimeAfterInfusionHoursRaw,
        "Post-dose sample time after infusion (hours)",
        min = 0.01
    )

    val errors = mutableListOf<ValidationError>()
    errors += patient.errorsOrEmpty()
    errors += dose.errorsOrEmpty()
    errors += preConcentration.errorsOrEmpty()
    errors += preSampleTime.errorsOrEmpty()
    errors += postConcentration.errorsOrEmpty()
    errors += postSampleTime.errorsOrEmpty()

    if (errors.isEmpty() && dose is ValidationResult.Valid) {
        if (preSampleTime is ValidationResult.Valid && preSampleTime.value > dose.value.dosingIntervalHours) {
            errors += ValidationError.InvalidTiming(
                "Pre-dose sample time before dose",
                "must not be longer ago than one dosing interval (${dose.value.dosingIntervalHours}h)."
            )
        }
        if (postSampleTime is ValidationResult.Valid && postSampleTime.value >= dose.value.dosingIntervalHours) {
            errors += ValidationError.InvalidTiming(
                "Post-dose sample time after infusion",
                "must be before the next dose is due (dosing interval is ${dose.value.dosingIntervalHours}h)."
            )
        }
        // Combined check: the calculation engine needs real time left over
        // between the infusion ending and the next dose after BOTH sample
        // offsets are accounted for, not just each one individually.
        if (preSampleTime is ValidationResult.Valid && postSampleTime is ValidationResult.Valid) {
            val infusionHours = dose.value.infusionDurationMinutes / 60.0
            val remaining = dose.value.dosingIntervalHours - infusionHours -
                postSampleTime.value - preSampleTime.value
            if (remaining <= 0.0) {
                errors += ValidationError.InvalidTiming(
                    "Sample timing",
                    "the pre-dose and post-dose sample times together leave no time before the " +
                        "next dose — reduce one or both, or lengthen the dosing interval."
                )
            }
        }
    }

    if (errors.isNotEmpty()) return ValidationResult.Invalid(errors)

    return ValidationResult.Valid(
        WorkflowInput.PrePost(
            patient = patient.valueOrThrow(),
            dose = dose.valueOrThrow(),
            preDoseConcentrationMgL = preConcentration.valueOrThrow(),
            preDoseSampleTimeBeforeDoseHours = preSampleTime.valueOrThrow(),
            postDoseConcentrationMgL = postConcentration.valueOrThrow(),
            postDoseSampleTimeAfterInfusionHours = postSampleTime.valueOrThrow()
        )
    )
}

/**
 * Non-blocking, informational checks — plausibility flags a user should
 * review, not hard errors (Case Study §8: "distinguish an error from
 * information that simply requires review"). These are structural/
 * logical checks only, not clinical target ranges.
 */
fun reviewWarnings(input: WorkflowInput.PrePost): List<String> {
    val warnings = mutableListOf<String>()
    if (input.postDoseConcentrationMgL <= input.preDoseConcentrationMgL) {
        warnings += "Post-dose concentration is not higher than the pre-dose concentration — " +
            "double-check the sample order and timing before reviewing the result."
    }
    return warnings
}

private fun <T> ValidationResult<T>.errorsOrEmpty(): List<ValidationError> =
    (this as? ValidationResult.Invalid)?.errors.orEmpty()

private fun <T> ValidationResult<T>.valueOrThrow(): T =
    (this as ValidationResult.Valid).value
