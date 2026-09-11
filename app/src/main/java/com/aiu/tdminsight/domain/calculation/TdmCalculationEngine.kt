package com.aiu.tdminsight.domain.calculation

import com.aiu.tdminsight.domain.model.PharmacokineticParameters
import com.aiu.tdminsight.domain.model.TdmResult
import com.aiu.tdminsight.domain.model.WorkflowInput
import com.aiu.tdminsight.domain.model.ExplanationStep
import kotlin.math.exp
import kotlin.math.ln

/**
 * Contract for turning validated input into a [CalculationResult]. The
 * UI layer depends on this interface, not a concrete implementation.
 */
interface TdmCalculationEngine {
    fun calculate(input: WorkflowInput): CalculationResult
}

/**
 * Implements the three Vancomycin workflows using formulas sourced from
 * standard, cited pharmacokinetics references — see
 * docs/Calculation_Method_Proposal.md for the full write-up, sources,
 * and one remaining known gap (the CrCl input unit doesn't match
 * myTDM Calculator's own tool — documented there, not hidden).
 *
 * Method by workflow:
 *  - Pre-only / Post-only: only one concentration is measured, so Ke
 *    and Vd are population estimates (Matzke / 0.7 L/kg), used to
 *    project the missing peak or trough from the one value we have.
 *  - Pre+Post: two concentrations spanning the same dosing interval
 *    give a patient-specific Ke via the two-point (Sawchuk-Zaske)
 *    method, and Vd from Dose/(true Cmax - true Cmin) using
 *    back-extrapolated concentrations — verified line-by-line against
 *    myTDM Calculator's own generated worksheet.
 */
class VancomycinCalculationEngine : TdmCalculationEngine {

    override fun calculate(input: WorkflowInput): CalculationResult {
        return when (input) {
            is WorkflowInput.Pre -> calculatePre(input)
            is WorkflowInput.Post -> calculatePost(input)
            is WorkflowInput.PrePost -> calculatePrePost(input)
        }
    }

    private fun calculatePre(input: WorkflowInput.Pre): CalculationResult {
        val crCl = cockcroftGaultCrClMlPerMin(input.patient)
        if (crCl <= 0.0) {
            return CalculationResult.Failure(
                "Calculated creatinine clearance was zero or negative — check patient inputs."
            )
        }
        val ke = populationKePerHour(crCl)
        if (ke <= 0.0) {
            return CalculationResult.Failure("Calculated elimination rate constant was zero or negative.")
        }

        val tau = input.dose.dosingIntervalHours
        val infusionHours = input.dose.infusionDurationMinutes / 60.0
        val timeAfterInfusionUntilNextDose = tau - infusionHours
        if (timeAfterInfusionUntilNextDose <= 0.0) {
            return CalculationResult.Failure(
                "Dosing interval must be longer than the infusion duration."
            )
        }

        val vd = populationVdLiters(input.patient.weightKg)
        val halfLife = halfLifeHours(ke)
        val clearance = ke * vd

        // Project the measured trough forward through the rest of the
        // interval to estimate the peak, per the PhIS Pre-only
        // calculator's described behaviour ("Expected Cmax if assuming
        // Expected Cmin = Pre level result").
        val estimatedCmax = input.preDoseConcentrationMgL * exp(ke * timeAfterInfusionUntilNextDose)

        val parameters = PharmacokineticParameters(
            eliminationRateConstantPerHour = ke,
            eliminationHalfLifeHours = halfLife,
            volumeOfDistributionL = vd,
            clearanceLPerHour = clearance,
            creatinineClearanceMlPerMin = crCl
        )

        val explanation = listOf(
            ExplanationStep(
                "Input values",
                "Measured pre-dose (trough) concentration ${input.preDoseConcentrationMgL} mg/L, " +
                    "drawn ${input.preDoseSampleTimeBeforeDoseHours} h before this dose."
            ),
            ExplanationStep(
                "Creatinine clearance (Cockcroft-Gault)",
                "Estimated renal function from age, weight, sex, and serum creatinine.",
                crCl, "mL/min"
            ),
            ExplanationStep(
                "Elimination rate constant (Ke) — population estimate",
                "Only one concentration is available, so Ke comes from the Matzke population " +
                    "equation (Ke = 0.00083 x CrCl + 0.0044) rather than this patient's own data.",
                ke, "/h"
            ),
            ExplanationStep(
                "Elimination half-life",
                "t1/2 = ln(2) / Ke.",
                halfLife, "h"
            ),
            ExplanationStep(
                "Volume of distribution (Vd) — population estimate",
                "0.7 L/kg x weight — a standard population average, not measured for this patient.",
                vd, "L"
            ),
            ExplanationStep(
                "Clearance",
                "CL = Ke x Vd.",
                clearance, "L/h"
            ),
            ExplanationStep(
                "Final result: estimated peak concentration (Cmax)",
                "The measured trough projected forward through the remaining interval using the " +
                    "population Ke.",
                estimatedCmax, "mg/L"
            )
        )

        return CalculationResult.Success(TdmResult(input.workflow, input, parameters, explanation))
    }

    private fun calculatePost(input: WorkflowInput.Post): CalculationResult {
        val crCl = cockcroftGaultCrClMlPerMin(input.patient)
        if (crCl <= 0.0) {
            return CalculationResult.Failure(
                "Calculated creatinine clearance was zero or negative — check patient inputs."
            )
        }
        val ke = populationKePerHour(crCl)
        if (ke <= 0.0) {
            return CalculationResult.Failure("Calculated elimination rate constant was zero or negative.")
        }

        val tau = input.dose.dosingIntervalHours
        val infusionHours = input.dose.infusionDurationMinutes / 60.0
        // The sample is drawn this many hours after the infusion ends, so
        // its absolute time since dose start is infusionHours + sample
        // time — both must be subtracted to get the time actually left
        // before the next dose (previously only the sample time was
        // subtracted, which silently ignored the infusion duration).
        val remainingTime = tau - infusionHours - input.postDoseSampleTimeAfterInfusionHours
        if (remainingTime <= 0.0) {
            return CalculationResult.Failure(
                "The post-dose sample time plus the infusion duration must leave time before " +
                    "the next dose — check the sampling time, infusion duration, and dosing interval."
            )
        }

        val vd = populationVdLiters(input.patient.weightKg)
        val halfLife = halfLifeHours(ke)
        val clearance = ke * vd

        // Project the measured peak backward through the remaining
        // interval to estimate the trough.
        val estimatedCmin = input.postDoseConcentrationMgL * exp(-ke * remainingTime)

        val parameters = PharmacokineticParameters(
            eliminationRateConstantPerHour = ke,
            eliminationHalfLifeHours = halfLife,
            volumeOfDistributionL = vd,
            clearanceLPerHour = clearance,
            creatinineClearanceMlPerMin = crCl
        )

        val explanation = listOf(
            ExplanationStep(
                "Input values",
                "Measured post-dose (peak) concentration ${input.postDoseConcentrationMgL} mg/L, " +
                    "drawn ${input.postDoseSampleTimeAfterInfusionHours} h after the infusion ended."
            ),
            ExplanationStep(
                "Creatinine clearance (Cockcroft-Gault)",
                "Estimated renal function from age, weight, sex, and serum creatinine.",
                crCl, "mL/min"
            ),
            ExplanationStep(
                "Elimination rate constant (Ke) — population estimate",
                "Only one concentration is available, so Ke comes from the Matzke population " +
                    "equation (Ke = 0.00083 x CrCl + 0.0044) rather than this patient's own data.",
                ke, "/h"
            ),
            ExplanationStep(
                "Elimination half-life",
                "t1/2 = ln(2) / Ke.",
                halfLife, "h"
            ),
            ExplanationStep(
                "Volume of distribution (Vd) — population estimate",
                "0.7 L/kg x weight — a standard population average, not measured for this patient.",
                vd, "L"
            ),
            ExplanationStep(
                "Clearance",
                "CL = Ke x Vd.",
                clearance, "L/h"
            ),
            ExplanationStep(
                "Final result: estimated trough concentration (Cmin)",
                "The measured peak projected backward through the remaining interval using the " +
                    "population Ke.",
                estimatedCmin, "mg/L"
            )
        )

        return CalculationResult.Success(TdmResult(input.workflow, input, parameters, explanation))
    }

    private fun calculatePrePost(input: WorkflowInput.PrePost): CalculationResult {
        if (input.postDoseConcentrationMgL <= input.preDoseConcentrationMgL) {
            return CalculationResult.Failure(
                "Post-dose concentration must be higher than the pre-dose concentration to " +
                    "calculate an elimination rate — check the sample order and timing."
            )
        }

        val infusionHours = input.dose.infusionDurationMinutes / 60.0
        val tau = input.dose.dosingIntervalHours

        // The pre-dose sample is a steady-state trough — at steady state
        // that same concentration recurs at the end of every interval.
        // So the two measured points sit on ONE continuous elimination
        // curve running from the post-dose sample forward to the next
        // occurrence of the trough, not across the dose infusion itself
        // (a dose is administered between the two draws, which adds
        // drug rather than eliminating it, so that gap can't be used
        // directly).
        val deltaT = tau - infusionHours - input.postDoseSampleTimeAfterInfusionHours -
            input.preDoseSampleTimeBeforeDoseHours
        if (deltaT <= 0.0) {
            return CalculationResult.Failure(
                "The post-dose sample must be drawn early enough in the interval to leave time " +
                    "before the next dose — check the sampling time and dosing interval."
            )
        }

        // Two-point (Sawchuk-Zaske) method: both concentrations were
        // actually measured for this patient, so Ke here is
        // patient-specific, not a population estimate.
        val ke = ln(input.postDoseConcentrationMgL / input.preDoseConcentrationMgL) / deltaT
        if (ke <= 0.0) {
            return CalculationResult.Failure("Calculated elimination rate constant was zero or negative.")
        }
        val halfLife = halfLifeHours(ke)

        // Back-extrapolate each measured concentration to the "true"
        // peak (right at the end of infusion) and "true" trough (right
        // at the moment the dose is given), correcting for the delay
        // between when each sample was actually drawn and that
        // reference point.
        val trueCmax = input.postDoseConcentrationMgL * exp(ke * input.postDoseSampleTimeAfterInfusionHours)
        val trueCmin = input.preDoseConcentrationMgL * exp(-ke * input.preDoseSampleTimeBeforeDoseHours)


        val vd = input.dose.doseMg / (trueCmax - trueCmin)
        val clearance = ke * vd

        val parameters = PharmacokineticParameters(
            eliminationRateConstantPerHour = ke,
            eliminationHalfLifeHours = halfLife,
            volumeOfDistributionL = vd,
            clearanceLPerHour = clearance
        )

        val explanation = listOf(
            ExplanationStep(
                "Input values",
                "Pre-dose (trough) concentration ${input.preDoseConcentrationMgL} mg/L and " +
                    "post-dose (peak) concentration ${input.postDoseConcentrationMgL} mg/L for the " +
                    "same dose."
            ),
            ExplanationStep(
                "Elimination rate constant (Ke) — patient-specific",
                "Two-point (Sawchuk-Zaske) method: Ke = ln(Cpost / Cpre) / Δt, where Δt is the " +
                    "dosing interval minus the infusion duration minus both sample-timing " +
                    "offsets (${"%.2f".format(deltaT)} h) — matches myTDM Calculator's own formula.",
                ke, "/h"
            ),
            ExplanationStep(
                "Elimination half-life",
                "t1/2 = ln(2) / Ke.",
                halfLife, "h"
            ),
            ExplanationStep(
                "Back-extrapolated true peak and trough",
                "True Cmax (at end of infusion) = ${"%.2f".format(trueCmax)} mg/L; true Cmin " +
                    "(at the moment of dosing) = ${"%.2f".format(trueCmin)} mg/L — correcting for " +
                    "the delay between each sample and its reference point.",
            ),
            ExplanationStep(
                "Volume of distribution (Vd) — patient-specific",
                "Vd = Dose / (true Cmax - true Cmin), using the back-extrapolated values above.",
                vd, "L"
            ),
            ExplanationStep(
                "Clearance",
                "CL = Ke x Vd.",
                clearance, "L/h"
            ),
            ExplanationStep(
                "Final result",
                "Elimination half-life of ${"%.2f".format(halfLife)} h, calculated directly from " +
                    "this patient's two measured concentrations."
            )
        )

        return CalculationResult.Success(TdmResult(input.workflow, input, parameters, explanation))
    }
}
