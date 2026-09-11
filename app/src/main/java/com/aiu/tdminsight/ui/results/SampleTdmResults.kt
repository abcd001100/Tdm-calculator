package com.aiu.tdminsight.ui.results

import com.aiu.tdminsight.domain.model.BiologicalSex
import com.aiu.tdminsight.domain.model.DoseInfo
import com.aiu.tdminsight.domain.model.ExplanationStep
import com.aiu.tdminsight.domain.model.PatientInfo
import com.aiu.tdminsight.domain.model.PharmacokineticParameters
import com.aiu.tdminsight.domain.model.TdmResult
import com.aiu.tdminsight.domain.model.VancomycinWorkflow
import com.aiu.tdminsight.domain.model.WorkflowInput

/**
 * FICTIONAL, HAND-WRITTEN placeholder data — not produced by the
 * calculation engine (which is still blocked, see
 * TdmCalculationEngine.kt), and not derived from any real formula.
 * Exists purely so the Results/Explanation screens (Phase 4.1/4.2) have
 * something realistic-looking to lay out and test against before Phase
 * 5 wires in real engine output. Every screen that uses this must make
 * clear to the user that it is sample data, not a real result.
 */
fun sampleTdmResult(workflow: VancomycinWorkflow): TdmResult {
    val patient = PatientInfo(
        weightKg = 70.0,
        ageYears = 45,
        sex = BiologicalSex.MALE,
        serumCreatinineMgDl = 1.0
    )
    val dose = DoseInfo(
        doseMg = 1000.0,
        infusionDurationMinutes = 60.0,
        dosingIntervalHours = 12.0
    )

    val input: WorkflowInput = when (workflow) {
        VancomycinWorkflow.PRE -> WorkflowInput.Pre(
            patient = patient,
            dose = dose,
            preDoseConcentrationMgL = 8.0,
            preDoseSampleTimeBeforeDoseHours = 0.5
        )
        VancomycinWorkflow.POST -> WorkflowInput.Post(
            patient = patient,
            dose = dose,
            postDoseConcentrationMgL = 28.0,
            postDoseSampleTimeAfterInfusionHours = 1.0
        )
        VancomycinWorkflow.PRE_POST -> WorkflowInput.PrePost(
            patient = patient,
            dose = dose,
            preDoseConcentrationMgL = 8.0,
            preDoseSampleTimeBeforeDoseHours = 0.5,
            postDoseConcentrationMgL = 28.0,
            postDoseSampleTimeAfterInfusionHours = 1.0
        )
    }

    // Placeholder numbers only — NOT computed by any approved formula.
    val parameters = PharmacokineticParameters(
        eliminationRateConstantPerHour = 0.15,
        eliminationHalfLifeHours = 4.6,
        volumeOfDistributionL = 50.0,
        clearanceLPerHour = 7.5
    )

    val explanation = listOf(
        ExplanationStep(
            label = "Input Values",
            detail = "Patient weight, dose, and sample concentrations as entered.",
        ),
        ExplanationStep(
            label = "Elimination rate constant (Ke)",
            detail = "Sample value for layout preview only — not computed by the calculation engine.",
            value = parameters.eliminationRateConstantPerHour,
            unit = "/h"
        ),
        ExplanationStep(
            label = "Elimination half-life",
            detail = "Sample value for layout preview only — not computed by the calculation engine.",
            value = parameters.eliminationHalfLifeHours,
            unit = "h"
        ),
        ExplanationStep(
            label = "Volume of distribution (Vd)",
            detail = "Sample value for layout preview only — not computed by the calculation engine.",
            value = parameters.volumeOfDistributionL,
            unit = "L"
        ),
        ExplanationStep(
            label = "Clearance",
            detail = "Sample value for layout preview only — not computed by the calculation engine.",
            value = parameters.clearanceLPerHour,
            unit = "L/h"
        )
    )

    return TdmResult(
        workflow = workflow,
        input = input,
        parameters = parameters,
        explanation = explanation
    )
}
