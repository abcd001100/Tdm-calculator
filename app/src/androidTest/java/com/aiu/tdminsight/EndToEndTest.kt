package com.aiu.tdminsight

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.aiu.tdminsight.ui.navigation.TdmNavGraph
import com.aiu.tdminsight.ui.theme.TdmInsightTheme
import org.junit.Rule
import org.junit.Test

/**
 * Phase 5.2 — full regression pass. Drives the real [TdmNavGraph] (not
 * an isolated screen) through all three Vancomycin workflows, so this
 * is the one place validation -> real [com.aiu.tdminsight.domain.calculation.VancomycinCalculationEngine]
 * output -> Results -> Explanation is checked as one continuous path,
 * per docs/Implementation_Roadmap.md Phase 5.2.
 *
 * Explanation step labels are asserted (not raw computed numbers,
 * which are float-precision-fragile) to confirm each workflow actually
 * used its own method: population estimates for Pre/Post-only vs. the
 * patient-specific two-point method for Pre+Post — see
 * docs/Calculation_Method_Proposal.md.
 */
class EndToEndTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun setUpApp() {
        composeTestRule.setContent {
            TdmInsightTheme {
                TdmNavGraph()
            }
        }
    }

    private fun fillCommonPatientAndDose(
        weightKg: String = "70",
        ageYears: String = "45",
        serumCreatinineMgDl: String = "1.0",
        doseMg: String = "1000",
        infusionDurationMinutes: String = "60",
        dosingIntervalHours: String = "12"
    ) {
        composeTestRule.onNodeWithText("Weight (kg)").performTextInput(weightKg)
        composeTestRule.onNodeWithText("Age (years)").performTextInput(ageYears)
        composeTestRule.onNodeWithText("Male").performClick()
        composeTestRule.onNodeWithText("Serum creatinine (mg/dL)").performTextInput(serumCreatinineMgDl)
        composeTestRule.onNodeWithText("Dose (mg)").performTextInput(doseMg)
        composeTestRule.onNodeWithText("Infusion duration (minutes)").performTextInput(infusionDurationMinutes)
        composeTestRule.onNodeWithText("Dosing interval (hours)").performTextInput(dosingIntervalHours)
    }

    @Test
    fun preWorkflow_validInput_reachesResultsAndExplanationWithRealCalculation() {
        setUpApp()

        composeTestRule.onNodeWithText("Pre-dose (Trough) Only").performClick()
        fillCommonPatientAndDose()
        composeTestRule.onNodeWithText("Pre-dose concentration (mg/L)").performTextInput("8.0")
        composeTestRule.onNodeWithText("Sample time before dose (hours)").performTextInput("0.5")
        composeTestRule.onNodeWithText("Continue").performClick()

        // Landed on Results with a real (not fixture) calculation.
        composeTestRule.onNodeWithText("Results").assertExists()
        composeTestRule.onNodeWithText("Sample data — no calculation has been run yet.").assertDoesNotExist()
        composeTestRule.onNodeWithText("Pharmacokinetic Parameters").assertExists()
        composeTestRule.onNodeWithText("Creatinine clearance").assertExists()

        composeTestRule.onNodeWithText("View Explanation").performClick()

        // Pre-only uses the population Ke/Vd method, not the two-point one.
        composeTestRule.onNodeWithText("1. Input values").assertExists()
        composeTestRule.onNodeWithText("2. Creatinine clearance (Cockcroft-Gault)").assertExists()
        composeTestRule.onNodeWithText("3. Elimination rate constant (Ke) — population estimate").assertExists()
        composeTestRule.onNodeWithText("7. Final result: estimated peak concentration (Cmax)").assertExists()
    }

    @Test
    fun postWorkflow_validInput_reachesResultsAndExplanationWithRealCalculation() {
        setUpApp()

        composeTestRule.onNodeWithText("Post-dose (Peak) Only").performClick()
        fillCommonPatientAndDose()
        composeTestRule.onNodeWithText("Post-dose concentration (mg/L)").performTextInput("28.0")
        composeTestRule.onNodeWithText("Sample time after infusion ends (hours)").performTextInput("1.0")
        composeTestRule.onNodeWithText("Continue").performClick()

        composeTestRule.onNodeWithText("Results").assertExists()
        composeTestRule.onNodeWithText("Sample data — no calculation has been run yet.").assertDoesNotExist()

        composeTestRule.onNodeWithText("View Explanation").performClick()

        composeTestRule.onNodeWithText("3. Elimination rate constant (Ke) — population estimate").assertExists()
        composeTestRule.onNodeWithText("7. Final result: estimated trough concentration (Cmin)").assertExists()
    }

    @Test
    fun prePostWorkflow_validInput_reachesResultsAndExplanationWithRealCalculation() {
        setUpApp()

        composeTestRule.onNodeWithText("Pre + Post (Both)").performClick()
        fillCommonPatientAndDose()
        composeTestRule.onNodeWithText("Pre-dose concentration (mg/L)").performTextInput("8.0")
        composeTestRule.onNodeWithText("Sample time before dose (hours)").performTextInput("0.5")
        composeTestRule.onNodeWithText("Post-dose concentration (mg/L)").performTextInput("28.0")
        composeTestRule.onNodeWithText("Sample time after infusion ends (hours)").performTextInput("1.0")
        composeTestRule.onNodeWithText("Continue").performClick()

        composeTestRule.onNodeWithText("Results").assertExists()
        composeTestRule.onNodeWithText("Sample data — no calculation has been run yet.").assertDoesNotExist()
        // Pre+Post never computes creatinine clearance (only Pre/Post-only do).
        composeTestRule.onNodeWithText("Creatinine clearance").assertDoesNotExist()

        composeTestRule.onNodeWithText("View Explanation").performClick()

        // Pre+Post uses the patient-specific two-point method, not a population estimate.
        composeTestRule.onNodeWithText("1. Input values").assertExists()
        composeTestRule.onNodeWithText("2. Elimination rate constant (Ke) — patient-specific").assertExists()
        composeTestRule.onNodeWithText("4. Back-extrapolated true peak and trough").assertExists()
        composeTestRule.onNodeWithText("7. Final result").assertExists()
    }

    @Test
    fun blankForm_doesNotReachResults_showsValidationError() {
        setUpApp()

        composeTestRule.onNodeWithText("Pre-dose (Trough) Only").performClick()
        composeTestRule.onNodeWithText("Continue").performClick()

        composeTestRule.onNodeWithText("Weight (kg) is required.").assertExists()
        composeTestRule.onNodeWithText("Results").assertDoesNotExist()
    }

    @Test
    fun prePostWorkflow_engineRejectsNonIncreasingConcentrations_showsErrorWithoutNavigating() {
        setUpApp()

        composeTestRule.onNodeWithText("Pre + Post (Both)").performClick()
        fillCommonPatientAndDose()
        // Post concentration not higher than pre — passes field/timing
        // validation, but the engine must refuse the logarithm rather
        // than crash or silently produce a negative Ke (Case Study §8).
        composeTestRule.onNodeWithText("Pre-dose concentration (mg/L)").performTextInput("20.0")
        composeTestRule.onNodeWithText("Sample time before dose (hours)").performTextInput("0.5")
        composeTestRule.onNodeWithText("Post-dose concentration (mg/L)").performTextInput("15.0")
        composeTestRule.onNodeWithText("Sample time after infusion ends (hours)").performTextInput("1.0")
        composeTestRule.onNodeWithText("Continue").performClick()

        composeTestRule.onNodeWithText(
            "Post-dose concentration must be higher than the pre-dose concentration to " +
                "calculate an elimination rate — check the sample order and timing."
        ).assertExists()
        composeTestRule.onNodeWithText("Results").assertDoesNotExist()
    }

    @Test
    fun preWorkflow_engineRejectsIntervalShorterThanInfusion_showsErrorWithoutNavigating() {
        setUpApp()

        composeTestRule.onNodeWithText("Pre-dose (Trough) Only").performClick()
        // Dosing interval (1h) shorter than the infusion duration
        // (90 min) — passes field/timing validation (which only checks
        // sample time against the interval), but the engine must refuse
        // rather than divide by a non-positive remaining time.
        fillCommonPatientAndDose(dosingIntervalHours = "1", infusionDurationMinutes = "90")
        composeTestRule.onNodeWithText("Pre-dose concentration (mg/L)").performTextInput("8.0")
        composeTestRule.onNodeWithText("Sample time before dose (hours)").performTextInput("0.5")
        composeTestRule.onNodeWithText("Continue").performClick()

        composeTestRule.onNodeWithText("Dosing interval must be longer than the infusion duration.")
            .assertExists()
        composeTestRule.onNodeWithText("Results").assertDoesNotExist()
    }
}
