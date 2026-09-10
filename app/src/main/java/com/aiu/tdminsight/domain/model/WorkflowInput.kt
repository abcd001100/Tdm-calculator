package com.aiu.tdminsight.domain.model

/**
 * Fully validated input for one TDM calculation run. Each workflow only
 * carries the fields the case study describes it as needing (Case Study
 * §3, §7) — this is what keeps the dynamic form honest: a screen for
 * [Pre] simply has no way to construct a [PrePost].
 *
 * Instances of this type are only ever constructed after validation
 * succeeds (see domain/validation/WorkflowInputValidator.kt) — nothing
 * here parses or validates raw user input itself.
 */
sealed class WorkflowInput {
    abstract val workflow: VancomycinWorkflow
    abstract val patient: PatientInfo
    abstract val dose: DoseInfo

    /**
     * Pre-dose (trough) workflow: one concentration drawn before the
     * dose, plus how long before the dose it was drawn.
     */
    data class Pre(
        override val patient: PatientInfo,
        override val dose: DoseInfo,
        val preDoseConcentrationMgL: Double,
        val preDoseSampleTimeBeforeDoseHours: Double
    ) : WorkflowInput() {
        override val workflow = VancomycinWorkflow.PRE
    }

    /**
     * Post-dose (peak) workflow: one concentration drawn after the
     * infusion ends, plus how long after infusion end it was drawn.
     */
    data class Post(
        override val patient: PatientInfo,
        override val dose: DoseInfo,
        val postDoseConcentrationMgL: Double,
        val postDoseSampleTimeAfterInfusionHours: Double
    ) : WorkflowInput() {
        override val workflow = VancomycinWorkflow.POST
    }

    /**
     * Combined workflow: both a pre-dose and a post-dose concentration,
     * each with its own sampling time.
     */
    data class PrePost(
        override val patient: PatientInfo,
        override val dose: DoseInfo,
        val preDoseConcentrationMgL: Double,
        val preDoseSampleTimeBeforeDoseHours: Double,
        val postDoseConcentrationMgL: Double,
        val postDoseSampleTimeAfterInfusionHours: Double
    ) : WorkflowInput() {
        override val workflow = VancomycinWorkflow.PRE_POST
    }
}
