package com.aiu.tdminsight.ui.input

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.aiu.tdminsight.domain.model.BiologicalSex
import com.aiu.tdminsight.domain.validation.DoseInfoRawInput
import com.aiu.tdminsight.domain.validation.PatientInfoRawInput

/**
 * Holds the raw, as-typed text field values for the patient + dose form
 * (Phase 3.2) and, once Phase 3.3 lands, the workflow-specific fields
 * too. Kept as one small state holder rather than a full ViewModel —
 * this screen has no async work or process-death concerns yet that
 * would justify the extra layer.
 */
@Stable
class PatientFormState {
    var weightKg by mutableStateOf("")
    var ageYears by mutableStateOf("")
    var sex by mutableStateOf<BiologicalSex?>(null)
    var serumCreatinineMgDl by mutableStateOf("")

    var doseMg by mutableStateOf("")
    var infusionDurationMinutes by mutableStateOf("")
    var dosingIntervalHours by mutableStateOf("")

    // Workflow-specific (Phase 3.3) — which of these are shown/required
    // depends on the selected VancomycinWorkflow.
    var preDoseConcentrationMgL by mutableStateOf("")
    var preDoseSampleTimeBeforeDoseHours by mutableStateOf("")
    var postDoseConcentrationMgL by mutableStateOf("")
    var postDoseSampleTimeAfterInfusionHours by mutableStateOf("")

    fun toPatientInfoRawInput() = PatientInfoRawInput(
        weightKg = weightKg,
        ageYears = ageYears,
        sex = sex,
        serumCreatinineMgDl = serumCreatinineMgDl
    )

    fun toDoseInfoRawInput() = DoseInfoRawInput(
        doseMg = doseMg,
        infusionDurationMinutes = infusionDurationMinutes,
        dosingIntervalHours = dosingIntervalHours
    )
}

@Composable
fun rememberPatientFormState(): PatientFormState = remember { PatientFormState() }
