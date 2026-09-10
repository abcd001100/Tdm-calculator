package com.aiu.tdminsight.ui.input

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.aiu.tdminsight.domain.calculation.CalculationResult
import com.aiu.tdminsight.domain.calculation.VancomycinCalculationEngine
import com.aiu.tdminsight.domain.model.BiologicalSex
import com.aiu.tdminsight.domain.model.TdmResult
import com.aiu.tdminsight.domain.model.VancomycinWorkflow
import com.aiu.tdminsight.domain.validation.ValidationResult
import com.aiu.tdminsight.domain.validation.validatePostWorkflowInput
import com.aiu.tdminsight.domain.validation.validatePreWorkflowInput
import com.aiu.tdminsight.domain.validation.validatePrePostWorkflowInput

/**
 * Patient + dose input, common to every Vancomycin workflow (Case Study
 * §7), plus the workflow-specific fields (concentration, sample timing)
 * for whichever [workflow] was chosen in Phase 3.1 — only the fields
 * that workflow actually needs are shown, per Case Study §3 ("do not
 * display every possible input field on one screen").
 *
 * On a valid submission, this also runs the calculation engine
 * (UI → Input State → Validation → Engine, per the architecture in
 * CLAUDE.md) and hands the resulting [TdmResult] to [onCalculated] —
 * the engine call itself lives in TdmCalculationEngine.kt, not here.
 */
@Composable
fun PatientFormScreen(
    workflow: VancomycinWorkflow,
    state: PatientFormState = rememberPatientFormState(),
    onCalculated: (TdmResult) -> Unit
) {
    var errors by remember { mutableStateOf<List<String>>(emptyList()) }
    val engine = remember { VancomycinCalculationEngine() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = "Patient & Dose", style = MaterialTheme.typography.headlineSmall)

        OutlinedTextField(
            value = state.weightKg,
            onValueChange = { state.weightKg = it },
            label = { Text("Weight (kg)") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = state.ageYears,
            onValueChange = { state.ageYears = it },
            label = { Text("Age (years)") },
            modifier = Modifier.fillMaxWidth()
        )

        Text(text = "Sex", style = MaterialTheme.typography.labelLarge)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            SexOption(
                label = "Male",
                selected = state.sex == BiologicalSex.MALE,
                onClick = { state.sex = BiologicalSex.MALE }
            )
            SexOption(
                label = "Female",
                selected = state.sex == BiologicalSex.FEMALE,
                onClick = { state.sex = BiologicalSex.FEMALE }
            )
        }

        OutlinedTextField(
            value = state.serumCreatinineMgDl,
            onValueChange = { state.serumCreatinineMgDl = it },
            label = { Text("Serum creatinine (mg/dL)") },
            modifier = Modifier.fillMaxWidth()
        )

        HorizontalDivider()

        OutlinedTextField(
            value = state.doseMg,
            onValueChange = { state.doseMg = it },
            label = { Text("Dose (mg)") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = state.infusionDurationMinutes,
            onValueChange = { state.infusionDurationMinutes = it },
            label = { Text("Infusion duration (minutes)") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = state.dosingIntervalHours,
            onValueChange = { state.dosingIntervalHours = it },
            label = { Text("Dosing interval (hours)") },
            modifier = Modifier.fillMaxWidth()
        )

        HorizontalDivider()

        when (workflow) {
            VancomycinWorkflow.PRE -> {
                Text(text = "Pre-dose Sample", style = MaterialTheme.typography.titleMedium)
                OutlinedTextField(
                    value = state.preDoseConcentrationMgL,
                    onValueChange = { state.preDoseConcentrationMgL = it },
                    label = { Text("Pre-dose concentration (mg/L)") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = state.preDoseSampleTimeBeforeDoseHours,
                    onValueChange = { state.preDoseSampleTimeBeforeDoseHours = it },
                    label = { Text("Sample time before dose (hours)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            VancomycinWorkflow.POST -> {
                Text(text = "Post-dose Sample", style = MaterialTheme.typography.titleMedium)
                OutlinedTextField(
                    value = state.postDoseConcentrationMgL,
                    onValueChange = { state.postDoseConcentrationMgL = it },
                    label = { Text("Post-dose concentration (mg/L)") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = state.postDoseSampleTimeAfterInfusionHours,
                    onValueChange = { state.postDoseSampleTimeAfterInfusionHours = it },
                    label = { Text("Sample time after infusion ends (hours)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            VancomycinWorkflow.PRE_POST -> {
                Text(text = "Pre-dose Sample", style = MaterialTheme.typography.titleMedium)
                OutlinedTextField(
                    value = state.preDoseConcentrationMgL,
                    onValueChange = { state.preDoseConcentrationMgL = it },
                    label = { Text("Pre-dose concentration (mg/L)") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = state.preDoseSampleTimeBeforeDoseHours,
                    onValueChange = { state.preDoseSampleTimeBeforeDoseHours = it },
                    label = { Text("Sample time before dose (hours)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Text(text = "Post-dose Sample", style = MaterialTheme.typography.titleMedium)
                OutlinedTextField(
                    value = state.postDoseConcentrationMgL,
                    onValueChange = { state.postDoseConcentrationMgL = it },
                    label = { Text("Post-dose concentration (mg/L)") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = state.postDoseSampleTimeAfterInfusionHours,
                    onValueChange = { state.postDoseSampleTimeAfterInfusionHours = it },
                    label = { Text("Sample time after infusion ends (hours)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        if (errors.isNotEmpty()) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                errors.forEach { message ->
                    Text(
                        text = message,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }

        Button(onClick = {
            val result = when (workflow) {
                VancomycinWorkflow.PRE -> validatePreWorkflowInput(
                    patientRaw = state.toPatientInfoRawInput(),
                    doseRaw = state.toDoseInfoRawInput(),
                    preDoseConcentrationMgLRaw = state.preDoseConcentrationMgL,
                    preDoseSampleTimeBeforeDoseHoursRaw = state.preDoseSampleTimeBeforeDoseHours
                )
                VancomycinWorkflow.POST -> validatePostWorkflowInput(
                    patientRaw = state.toPatientInfoRawInput(),
                    doseRaw = state.toDoseInfoRawInput(),
                    postDoseConcentrationMgLRaw = state.postDoseConcentrationMgL,
                    postDoseSampleTimeAfterInfusionHoursRaw = state.postDoseSampleTimeAfterInfusionHours
                )
                VancomycinWorkflow.PRE_POST -> validatePrePostWorkflowInput(
                    patientRaw = state.toPatientInfoRawInput(),
                    doseRaw = state.toDoseInfoRawInput(),
                    preDoseConcentrationMgLRaw = state.preDoseConcentrationMgL,
                    preDoseSampleTimeBeforeDoseHoursRaw = state.preDoseSampleTimeBeforeDoseHours,
                    postDoseConcentrationMgLRaw = state.postDoseConcentrationMgL,
                    postDoseSampleTimeAfterInfusionHoursRaw = state.postDoseSampleTimeAfterInfusionHours
                )
            }

            when (result) {
                is ValidationResult.Valid -> {
                    when (val calculation = engine.calculate(result.value)) {
                        is CalculationResult.Success -> {
                            errors = emptyList()
                            onCalculated(calculation.result)
                        }
                        is CalculationResult.Failure -> {
                            errors = listOf(calculation.message)
                        }
                    }
                }
                is ValidationResult.Invalid -> {
                    errors = result.errors.map { it.message }
                }
            }
        }) {
            Text("Continue")
        }
    }
}

@Composable
private fun SexOption(label: String, selected: Boolean, onClick: () -> Unit) {
    FilterChip(selected = selected, onClick = onClick, label = { Text(label) })
}
