package com.aiu.tdminsight.ui.results

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.aiu.tdminsight.domain.model.BiologicalSex
import com.aiu.tdminsight.domain.model.DoseInfo
import com.aiu.tdminsight.domain.model.PatientInfo
import com.aiu.tdminsight.domain.model.PharmacokineticParameters
import com.aiu.tdminsight.domain.model.TdmResult
import com.aiu.tdminsight.domain.model.VancomycinWorkflow
import com.aiu.tdminsight.domain.model.WorkflowInput
import com.aiu.tdminsight.ui.theme.TdmInsightTheme
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class ResultsScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val patient = PatientInfo(
        weightKg = 70.0,
        ageYears = 45,
        sex = BiologicalSex.MALE,
        serumCreatinineMgDl = 1.0
    )

    private val dose = DoseInfo(
        doseMg = 1000.0,
        infusionDurationMinutes = 60.0,
        dosingIntervalHours = 12.0
    )

    private fun prePostResult(parameters: PharmacokineticParameters) = TdmResult(
        workflow = VancomycinWorkflow.PRE_POST,
        input = WorkflowInput.PrePost(
            patient = patient,
            dose = dose,
            preDoseConcentrationMgL = 8.0,
            preDoseSampleTimeBeforeDoseHours = 0.5,
            postDoseConcentrationMgL = 28.0,
            postDoseSampleTimeAfterInfusionHours = 1.0
        ),
        parameters = parameters
    )

    @Test
    fun resultsScreen_showsInputValuesAndParameters() {
        val result = prePostResult(
            PharmacokineticParameters(
                eliminationRateConstantPerHour = 0.15,
                eliminationHalfLifeHours = 4.6,
                volumeOfDistributionL = 50.0,
                clearanceLPerHour = 7.5
            )
        )

        composeTestRule.setContent {
            TdmInsightTheme {
                ResultsScreen(result = result, onOpenExplanation = {})
            }
        }

        composeTestRule.onNodeWithText("Input Values").assertExists()
        composeTestRule.onNodeWithText("Weight").assertExists()
        composeTestRule.onNodeWithText("70.0 kg").assertExists()
        composeTestRule.onNodeWithText("Pharmacokinetic Parameters").assertExists()
        composeTestRule.onNodeWithText("Elimination rate constant (Ke)").assertExists()
        composeTestRule.onNodeWithText("0.1500 /h").assertExists()
    }

    @Test
    fun resultsScreen_showsSampleDataWarning_whenFlagged() {
        val result = prePostResult(PharmacokineticParameters())

        composeTestRule.setContent {
            TdmInsightTheme {
                ResultsScreen(result = result, isSampleData = true, onOpenExplanation = {})
            }
        }

        composeTestRule.onNodeWithText("Sample data — no calculation has been run yet.").assertExists()
    }

    @Test
    fun resultsScreen_hidesSampleDataWarning_whenNotSampleData() {
        val result = prePostResult(PharmacokineticParameters())

        composeTestRule.setContent {
            TdmInsightTheme {
                ResultsScreen(result = result, isSampleData = false, onOpenExplanation = {})
            }
        }

        composeTestRule.onNodeWithText("Sample data — no calculation has been run yet.").assertDoesNotExist()
    }

    @Test
    fun resultsScreen_tappingViewExplanation_invokesCallback() {
        var opened = false
        val result = prePostResult(PharmacokineticParameters())

        composeTestRule.setContent {
            TdmInsightTheme {
                ResultsScreen(result = result, onOpenExplanation = { opened = true })
            }
        }

        composeTestRule.onNodeWithText("View Explanation").performClick()

        assertTrue(opened)
    }

    @Test
    fun resultsScreen_showsCreatinineClearance_whenPresent() {
        val result = prePostResult(
            PharmacokineticParameters(creatinineClearanceMlPerMin = 95.0)
        )

        composeTestRule.setContent {
            TdmInsightTheme {
                ResultsScreen(result = result, onOpenExplanation = {})
            }
        }

        composeTestRule.onNodeWithText("Creatinine clearance").assertExists()
    }

    @Test
    fun resultsScreen_hidesCreatinineClearance_whenAbsent() {
        val result = prePostResult(PharmacokineticParameters())

        composeTestRule.setContent {
            TdmInsightTheme {
                ResultsScreen(result = result, onOpenExplanation = {})
            }
        }

        composeTestRule.onNodeWithText("Creatinine clearance").assertDoesNotExist()
    }

    @Test
    fun resultsScreen_showsOnlyPreFields_forPreWorkflow() {
        val preResult = TdmResult(
            workflow = VancomycinWorkflow.PRE,
            input = WorkflowInput.Pre(
                patient = patient,
                dose = dose,
                preDoseConcentrationMgL = 8.0,
                preDoseSampleTimeBeforeDoseHours = 0.5
            ),
            parameters = PharmacokineticParameters()
        )

        composeTestRule.setContent {
            TdmInsightTheme {
                ResultsScreen(result = preResult, onOpenExplanation = {})
            }
        }

        composeTestRule.onNodeWithText("Pre-dose concentration").assertExists()
        composeTestRule.onNodeWithText("Post-dose concentration").assertDoesNotExist()
    }
}
