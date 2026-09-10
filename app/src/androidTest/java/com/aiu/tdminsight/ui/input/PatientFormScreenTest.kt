package com.aiu.tdminsight.ui.input

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.aiu.tdminsight.domain.model.VancomycinWorkflow
import com.aiu.tdminsight.ui.theme.TdmInsightTheme
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class PatientFormScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun tappingContinueWithBlankForm_showsValidationErrors() {
        composeTestRule.setContent {
            TdmInsightTheme {
                PatientFormScreen(workflow = VancomycinWorkflow.PRE, onContinue = {})
            }
        }

        composeTestRule.onNodeWithText("Continue").performClick()

        // Understandable, field-specific messages — not a raw exception.
        composeTestRule.onNodeWithText("Weight (kg) is required.").assertExists()
    }

    @Test
    fun onlyPreDoseFields_areShown_forPreWorkflow() {
        composeTestRule.setContent {
            TdmInsightTheme {
                PatientFormScreen(workflow = VancomycinWorkflow.PRE, onContinue = {})
            }
        }

        composeTestRule.onNodeWithText("Pre-dose concentration (mg/L)").assertExists()
        composeTestRule.onNodeWithText("Post-dose concentration (mg/L)").assertDoesNotExist()
    }

    @Test
    fun bothConcentrationFields_areShown_forPrePostWorkflow() {
        composeTestRule.setContent {
            TdmInsightTheme {
                PatientFormScreen(workflow = VancomycinWorkflow.PRE_POST, onContinue = {})
            }
        }

        composeTestRule.onNodeWithText("Pre-dose concentration (mg/L)").assertExists()
        composeTestRule.onNodeWithText("Post-dose concentration (mg/L)").assertExists()
    }

    @Test
    fun validPreWorkflowInput_callsOnContinue() {
        var continued = false

        composeTestRule.setContent {
            TdmInsightTheme {
                PatientFormScreen(workflow = VancomycinWorkflow.PRE, onContinue = { continued = true })
            }
        }

        composeTestRule.onNodeWithText("Weight (kg)").performTextInput("70")
        composeTestRule.onNodeWithText("Age (years)").performTextInput("45")
        composeTestRule.onNodeWithText("Male").performClick()
        composeTestRule.onNodeWithText("Serum creatinine (mg/dL)").performTextInput("1.0")
        composeTestRule.onNodeWithText("Dose (mg)").performTextInput("1000")
        composeTestRule.onNodeWithText("Infusion duration (minutes)").performTextInput("60")
        composeTestRule.onNodeWithText("Dosing interval (hours)").performTextInput("12")
        composeTestRule.onNodeWithText("Pre-dose concentration (mg/L)").performTextInput("8.0")
        composeTestRule.onNodeWithText("Sample time before dose (hours)").performTextInput("0.5")

        composeTestRule.onNodeWithText("Continue").performClick()

        assertTrue(continued)
    }

    @Test
    fun invalidTiming_doesNotCallOnContinue() {
        var continued = false

        composeTestRule.setContent {
            TdmInsightTheme {
                PatientFormScreen(workflow = VancomycinWorkflow.PRE, onContinue = { continued = true })
            }
        }

        composeTestRule.onNodeWithText("Weight (kg)").performTextInput("70")
        composeTestRule.onNodeWithText("Age (years)").performTextInput("45")
        composeTestRule.onNodeWithText("Male").performClick()
        composeTestRule.onNodeWithText("Serum creatinine (mg/dL)").performTextInput("1.0")
        composeTestRule.onNodeWithText("Dose (mg)").performTextInput("1000")
        composeTestRule.onNodeWithText("Infusion duration (minutes)").performTextInput("60")
        composeTestRule.onNodeWithText("Dosing interval (hours)").performTextInput("12")
        composeTestRule.onNodeWithText("Pre-dose concentration (mg/L)").performTextInput("8.0")
        // Sample time older than the 12h dosing interval — should be rejected.
        composeTestRule.onNodeWithText("Sample time before dose (hours)").performTextInput("13")

        composeTestRule.onNodeWithText("Continue").performClick()

        assertFalse(continued)
    }
}
