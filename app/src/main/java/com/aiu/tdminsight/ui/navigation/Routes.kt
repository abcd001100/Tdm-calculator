package com.aiu.tdminsight.ui.navigation

/**
 * Route identifiers for the main TDM workflow. Each stage below is a
 * placeholder screen until its owning roadmap phase replaces it:
 *
 *  - [WORKFLOW_SELECTION] — Phase 3.1 (choose Vancomycin Pre / Post / Pre+Post)
 *  - [INPUT_FORM]         — Phase 3.2 / 3.3 (dynamic patient + workflow inputs)
 *  - [RESULTS]            — Phase 4.1 (intermediate + final results)
 *  - [EXPLANATION]        — Phase 4.2 (step-by-step calculation explanation)
 */
object Routes {
    const val WORKFLOW_SELECTION = "workflow_selection"
    const val INPUT_FORM = "input_form"
    const val RESULTS = "results"
    const val EXPLANATION = "explanation"
}
