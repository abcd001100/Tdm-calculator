package com.aiu.tdminsight.domain.model

/**
 * Biological sex, kept as its own enum (rather than a free-text field)
 * since some renal-function formulas a future lecturer-approved
 * calculation may use require it as a discrete input.
 */
enum class BiologicalSex {
    MALE,
    FEMALE
}

/**
 * Demographic and lab data common to every Vancomycin workflow. This is
 * a plain data container — it does not compute anything (e.g. no
 * creatinine clearance formula here). Any derived clinical value must
 * wait for a lecturer-approved equation, per CLAUDE.md's clinical rules.
 */
data class PatientInfo(
    val weightKg: Double,
    val ageYears: Int,
    val sex: BiologicalSex,
    val serumCreatinineMgDl: Double
)
