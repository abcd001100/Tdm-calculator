package com.aiu.tdminsight.domain.model

/**
 * Pharmacokinetic parameters a workflow may produce (Case Study §6).
 * Every field is nullable because which parameters apply — and how
 * they're computed — depends on the workflow and on the
 * lecturer-approved calculation method, neither of which is decided at
 * the model layer.
 */
data class PharmacokineticParameters(
    val eliminationRateConstantPerHour: Double? = null,
    val eliminationHalfLifeHours: Double? = null,
    val volumeOfDistributionL: Double? = null,
    val clearanceLPerHour: Double? = null
)

/**
 * One step of the explanation shown alongside the final result (Case
 * Study §9: Input Values → Intermediate Values → Pharmacokinetic
 * Parameters → Final Result). [value]/[unit] are optional since some
 * steps are purely descriptive (e.g. "using the one-compartment
 * first-order elimination model").
 */
data class ExplanationStep(
    val label: String,
    val detail: String,
    val value: Double? = null,
    val unit: String? = null
)

/**
 * The result of one TDM calculation run. This is the contract the
 * Results screen (Phase 4.1) and Explanation screen (Phase 4.2) are
 * built against — including with mock data, before the calculation
 * engine itself is unblocked (see TdmCalculationEngine.kt).
 */
data class TdmResult(
    val workflow: VancomycinWorkflow,
    val input: WorkflowInput,
    val parameters: PharmacokineticParameters,
    val explanation: List<ExplanationStep> = emptyList()
)
