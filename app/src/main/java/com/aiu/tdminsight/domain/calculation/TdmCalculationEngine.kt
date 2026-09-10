package com.aiu.tdminsight.domain.calculation

import com.aiu.tdminsight.domain.model.TdmResult
import com.aiu.tdminsight.domain.model.WorkflowInput

/**
 * Contract for turning validated input into a [TdmResult]. The UI layer
 * (Phase 3/4) should depend on this interface, not on a concrete
 * implementation, so the Results/Explanation screens can be built and
 * tested against a fake engine before the real one exists.
 */
interface TdmCalculationEngine {
    fun calculate(input: WorkflowInput): TdmResult
}

/**
 * BLOCKED — see docs/Case_Study_Analysis.md §8 and
 * docs/Implementation_Roadmap.md Phase 2.3.
 *
 * The Vancomycin Pre / Post / Pre+Post equations, units, assumptions,
 * and reference values are not present in the assessment or case study
 * documents. Per CLAUDE.md's clinical-calculation rules, no formula may
 * be invented or guessed — implementation of each method below stops
 * here until the lecturer supplies the approved equations.
 *
 * Do not replace these [NotImplementedError]s with a guessed formula.
 * When the equations are available, implement one workflow at a time,
 * each as its own commit with its own unit tests (see the roadmap's
 * Phase 2.3 follow-on tasks).
 */
class VancomycinCalculationEngine : TdmCalculationEngine {

    override fun calculate(input: WorkflowInput): TdmResult {
        return when (input) {
            is WorkflowInput.Pre -> calculatePre(input)
            is WorkflowInput.Post -> calculatePost(input)
            is WorkflowInput.PrePost -> calculatePrePost(input)
        }
    }

    private fun calculatePre(input: WorkflowInput.Pre): TdmResult {
        throw NotImplementedError(
            "Vancomycin Pre calculation is blocked: lecturer-approved equations, " +
                "units, and reference values are required before this can be " +
                "implemented. See docs/Case_Study_Analysis.md §8."
        )
    }

    private fun calculatePost(input: WorkflowInput.Post): TdmResult {
        throw NotImplementedError(
            "Vancomycin Post calculation is blocked: lecturer-approved equations, " +
                "units, and reference values are required before this can be " +
                "implemented. See docs/Case_Study_Analysis.md §8."
        )
    }

    private fun calculatePrePost(input: WorkflowInput.PrePost): TdmResult {
        throw NotImplementedError(
            "Vancomycin Pre+Post calculation is blocked: lecturer-approved " +
                "equations, units, and reference values are required before this " +
                "can be implemented. See docs/Case_Study_Analysis.md §8."
        )
    }
}
