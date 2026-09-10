package com.aiu.tdminsight.ui.results

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.aiu.tdminsight.domain.model.PharmacokineticParameters
import com.aiu.tdminsight.domain.model.TdmResult
import com.aiu.tdminsight.domain.model.WorkflowInput

/**
 * Displays a completed TDM calculation: input values, then the
 * pharmacokinetic parameters produced from them (Case Study §9). Does
 * not show only a final number — every input and intermediate value
 * stays visible. A "View Explanation" button opens the step-by-step
 * breakdown (Phase 4.2).
 *
 * [result] is sample/fixture data until Phase 5 (Integration) wires in
 * real calculation engine output — see SampleTdmResults.kt.
 */
@Composable
fun ResultsScreen(result: TdmResult, onOpenExplanation: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = "Results", style = MaterialTheme.typography.headlineSmall)
        Text(
            text = "Sample data — pending calculation engine integration (Phase 5).",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.error
        )

        SectionCard(title = "Input Values") {
            InputValuesSummary(result.input)
        }

        SectionCard(title = "Pharmacokinetic Parameters") {
            ParametersSummary(result.parameters)
        }

        Button(onClick = onOpenExplanation) {
            Text("View Explanation")
        }
    }
}

@Composable
private fun SectionCard(title: String, content: @Composable () -> Unit) {
    Card {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(text = title, style = MaterialTheme.typography.titleMedium)
            HorizontalDivider()
            content()
        }
    }
}

@Composable
private fun InputValuesSummary(input: WorkflowInput) {
    val patient = input.patient
    val dose = input.dose

    LabelledValue("Weight", "${patient.weightKg} kg")
    LabelledValue("Age", "${patient.ageYears} years")
    LabelledValue("Sex", patient.sex.name)
    LabelledValue("Serum creatinine", "${patient.serumCreatinineMgDl} mg/dL")
    LabelledValue("Dose", "${dose.doseMg} mg")
    LabelledValue("Infusion duration", "${dose.infusionDurationMinutes} min")
    LabelledValue("Dosing interval", "${dose.dosingIntervalHours} h")

    when (input) {
        is WorkflowInput.Pre -> {
            LabelledValue("Pre-dose concentration", "${input.preDoseConcentrationMgL} mg/L")
            LabelledValue("Sample time before dose", "${input.preDoseSampleTimeBeforeDoseHours} h")
        }
        is WorkflowInput.Post -> {
            LabelledValue("Post-dose concentration", "${input.postDoseConcentrationMgL} mg/L")
            LabelledValue("Sample time after infusion", "${input.postDoseSampleTimeAfterInfusionHours} h")
        }
        is WorkflowInput.PrePost -> {
            LabelledValue("Pre-dose concentration", "${input.preDoseConcentrationMgL} mg/L")
            LabelledValue("Pre sample time before dose", "${input.preDoseSampleTimeBeforeDoseHours} h")
            LabelledValue("Post-dose concentration", "${input.postDoseConcentrationMgL} mg/L")
            LabelledValue("Post sample time after infusion", "${input.postDoseSampleTimeAfterInfusionHours} h")
        }
    }
}

@Composable
private fun ParametersSummary(parameters: PharmacokineticParameters) {
    LabelledValue("Elimination rate constant (Ke)", parameters.eliminationRateConstantPerHour.orDash("/h"))
    LabelledValue("Elimination half-life", parameters.eliminationHalfLifeHours.orDash("h"))
    LabelledValue("Volume of distribution (Vd)", parameters.volumeOfDistributionL.orDash("L"))
    LabelledValue("Clearance", parameters.clearanceLPerHour.orDash("L/h"))
}

@Composable
private fun LabelledValue(label: String, value: String) {
    Column {
        Text(text = label, style = MaterialTheme.typography.labelMedium)
        Text(text = value, style = MaterialTheme.typography.bodyLarge)
    }
}

private fun Double?.orDash(unit: String): String = this?.let { "$it $unit" } ?: "—"
