package com.aiu.tdminsight.ui.results

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.aiu.tdminsight.domain.model.BiologicalSex
import com.aiu.tdminsight.domain.model.DoseInfo
import com.aiu.tdminsight.domain.model.ExplanationStep
import com.aiu.tdminsight.domain.model.PatientInfo
import com.aiu.tdminsight.domain.model.PharmacokineticParameters
import com.aiu.tdminsight.domain.model.TdmResult
import com.aiu.tdminsight.domain.model.VancomycinWorkflow
import com.aiu.tdminsight.domain.model.WorkflowInput
import com.aiu.tdminsight.ui.theme.TdmInsightTheme
import org.junit.Rule
import org.junit.Test

class ExplanationScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val input = WorkflowInput.Pre(
        patient = PatientInfo(
            weightKg = 70.0,
            ageYears = 45,
            sex = BiologicalSex.MALE,
            serumCreatinineMgDl = 1.0
        ),
        dose = DoseInfo(
            doseMg = 1000.0,
            infusionDurationMinutes = 60.0,
            dosingIntervalHours = 12.0
        ),
        preDoseConcentrationMgL = 8.0,
        preDoseSampleTimeBeforeDoseHours = 0.5
    )

    private fun resultWithSteps(steps: List<ExplanationStep>) = TdmResult(
        workflow = VancomycinWorkflow.PRE,
        input = input,
        parameters = PharmacokineticParameters(),
        explanation = steps
    )

    @Test
    fun explanationScreen_showsStepsInOrder_withValueAndUnit() {
        val steps = listOf(
            ExplanationStep(
                label = "Input Values",
                detail = "Patient weight, dose, and sample concentrations as entered."
            ),
            ExplanationStep(
                label = "Elimination rate constant (Ke)",
                detail = "Sample value — the real formula is pending lecturer approval.",
                value = 0.15,
                unit = "/h"
            )
        )

        composeTestRule.setContent {
            TdmInsightTheme {
                ExplanationScreen(result = resultWithSteps(steps))
            }
        }

        composeTestRule.onNodeWithText("1. Input Values").assertExists()
        composeTestRule.onNodeWithText("2. Elimination rate constant (Ke)").assertExists()
        composeTestRule.onNodeWithText("0.1500 /h").assertExists()
    }

    @Test
    fun explanationScreen_showsDescriptiveStep_withoutValueLine() {
        val steps = listOf(
            ExplanationStep(
                label = "Model Used",
                detail = "One-compartment first-order elimination model."
            )
        )

        composeTestRule.setContent {
            TdmInsightTheme {
                ExplanationScreen(result = resultWithSteps(steps))
            }
        }

        composeTestRule.onNodeWithText("1. Model Used").assertExists()
        composeTestRule.onNodeWithText("One-compartment first-order elimination model.").assertExists()
    }

    @Test
    fun explanationScreen_showsEmptyMessage_whenNoStepsAvailable() {
        composeTestRule.setContent {
            TdmInsightTheme {
                ExplanationScreen(result = resultWithSteps(emptyList()))
            }
        }

        composeTestRule.onNodeWithText("No explanation steps are available for this result yet.").assertExists()
    }

    @Test
    fun explanationScreen_showsSampleDataWarning_whenFlagged() {
        composeTestRule.setContent {
            TdmInsightTheme {
                ExplanationScreen(result = resultWithSteps(emptyList()), isSampleData = true)
            }
        }

        composeTestRule.onNodeWithText("Sample data — no calculation has been run yet.").assertExists()
    }

    @Test
    fun explanationScreen_hidesSampleDataWarning_whenNotSampleData() {
        composeTestRule.setContent {
            TdmInsightTheme {
                ExplanationScreen(result = resultWithSteps(emptyList()), isSampleData = false)
            }
        }

        composeTestRule.onNodeWithText("Sample data — no calculation has been run yet.").assertDoesNotExist()
    }
}
