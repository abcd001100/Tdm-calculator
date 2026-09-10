package com.aiu.tdminsight.domain.calculation

import com.aiu.tdminsight.domain.model.BiologicalSex
import com.aiu.tdminsight.domain.model.PatientInfo
import kotlin.math.ln

/**
 * Population-level pharmacokinetic estimates used when only one
 * concentration is available (Vancomycin Pre-only, Post-only) and a
 * patient-specific elimination rate can't be derived directly from two
 * measured points.
 *
 * Sources (see docs/Calculation_Method_Proposal.md for the full
 * write-up and citations):
 *  - Cockcroft-Gault creatinine clearance: the standard, near-universal
 *    clinical CrCl estimate.
 *  - Matzke population Ke: Ke = 0.00083 x CrCl + 0.0044 (Ke in /h, CrCl
 *    in mL/min, uncorrected for body surface area) — a widely cited
 *    population-predicted elimination rate constant for vancomycin.
 *  - Population Vd: 0.7 L/kg, a commonly cited teaching default for
 *    vancomycin. This is the least certain of the values used here —
 *    flagged for lecturer confirmation.
 *
 * These are population averages, not measurements of this specific
 * patient — every place they're used is labelled as such in the
 * resulting explanation.
 */

/** Cockcroft-Gault creatinine clearance, in mL/min (uncorrected for BSA). */
fun cockcroftGaultCrClMlPerMin(patient: PatientInfo): Double {
    val base = ((140 - patient.ageYears) * patient.weightKg) / (72 * patient.serumCreatinineMgDl)
    return if (patient.sex == BiologicalSex.FEMALE) base * 0.85 else base
}

/** Matzke population-predicted elimination rate constant, per hour. */
fun populationKePerHour(creatinineClearanceMlPerMin: Double): Double =
    0.00083 * creatinineClearanceMlPerMin + 0.0044

/** Standard population volume of distribution estimate, in litres. */
fun populationVdLiters(weightKg: Double): Double = 0.7 * weightKg

/** t½ = ln(2) / Ke — this part is a mathematical identity, not a clinical assumption. */
fun halfLifeHours(kePerHour: Double): Double = ln(2.0) / kePerHour
