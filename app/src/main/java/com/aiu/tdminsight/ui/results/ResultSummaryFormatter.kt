package com.aiu.tdminsight.ui.results

import com.aiu.tdminsight.domain.model.PharmacokineticParameters
import com.aiu.tdminsight.domain.model.TdmResult
import com.aiu.tdminsight.domain.model.VancomycinWorkflow
import com.aiu.tdminsight.domain.model.WorkflowInput

/**
 * Plain-text rendering of a [TdmResult] for sharing/export (Case Study
 * §10, optional enhancement). Kept as an ordinary function rather than
 * a Composable — no UI dependency, so it's directly unit testable —
 * and mirrors what ResultsScreen already shows on-screen: input values,
 * then pharmacokinetic parameters. Always ends with the mandatory
 * academic-prototype disclaimer (Case Study §14), since a shared
 * summary may be read outside the app by someone who never saw the
 * in-app warning.
 */
fun formatTdmResultSummary(result: TdmResult, isSampleData: Boolean = false): String {
    val lines = mutableListOf<String>()
    lines += "TDM Insight — ${result.workflow.displayName()} Result"
    if (isSampleData) {
        lines += "(Sample data — no calculation has been run yet.)"
    }
    lines += ""
    lines += "Input Values"
    lines += inputValueLines(result.input)
    lines += ""
    lines += "Pharmacokinetic Parameters"
    lines += parameterLines(result.parameters)
    lines += ""
    lines += "TDM Insight is an academic software prototype for educational and " +
        "software development purposes only. It is not a clinically validated " +
        "prescribing, diagnostic, or autonomous treatment-decision system."
    return lines.joinToString("\n")
}

private fun VancomycinWorkflow.displayName(): String = when (this) {
    VancomycinWorkflow.PRE -> "Vancomycin Pre"
    VancomycinWorkflow.POST -> "Vancomycin Post"
    VancomycinWorkflow.PRE_POST -> "Vancomycin Pre + Post"
}

private fun inputValueLines(input: WorkflowInput): List<String> {
    val patient = input.patient
    val dose = input.dose
    val lines = mutableListOf(
        "Weight: ${patient.weightKg} kg",
        "Age: ${patient.ageYears} years",
        "Sex: ${patient.sex.name}",
        "Serum creatinine: ${patient.serumCreatinineMgDl} mg/dL",
        "Dose: ${dose.doseMg} mg",
        "Infusion duration: ${dose.infusionDurationMinutes} min",
        "Dosing interval: ${dose.dosingIntervalHours} h"
    )

    when (input) {
        is WorkflowInput.Pre -> lines += listOf(
            "Pre-dose concentration: ${input.preDoseConcentrationMgL} mg/L",
            "Sample time before dose: ${input.preDoseSampleTimeBeforeDoseHours} h"
        )
        is WorkflowInput.Post -> lines += listOf(
            "Post-dose concentration: ${input.postDoseConcentrationMgL} mg/L",
            "Sample time after infusion: ${input.postDoseSampleTimeAfterInfusionHours} h"
        )
        is WorkflowInput.PrePost -> lines += listOf(
            "Pre-dose concentration: ${input.preDoseConcentrationMgL} mg/L",
            "Pre sample time before dose: ${input.preDoseSampleTimeBeforeDoseHours} h",
            "Post-dose concentration: ${input.postDoseConcentrationMgL} mg/L",
            "Post sample time after infusion: ${input.postDoseSampleTimeAfterInfusionHours} h"
        )
    }

    return lines
}

private fun parameterLines(parameters: PharmacokineticParameters): List<String> {
    val lines = mutableListOf<String>()
    parameters.creatinineClearanceMlPerMin?.let { lines += "Creatinine clearance: $it mL/min" }
    lines += "Elimination rate constant (Ke): ${parameters.eliminationRateConstantPerHour.orDashText("/h")}"
    lines += "Elimination half-life: ${parameters.eliminationHalfLifeHours.orDashText("h")}"
    lines += "Volume of distribution (Vd): ${parameters.volumeOfDistributionL.orDashText("L")}"
    lines += "Clearance: ${parameters.clearanceLPerHour.orDashText("L/h")}"
    return lines
}

private fun Double?.orDashText(unit: String): String = this?.let { "$it $unit" } ?: "—"
