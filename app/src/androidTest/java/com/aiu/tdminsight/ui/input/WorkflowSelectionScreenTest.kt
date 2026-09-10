package com.aiu.tdminsight.ui.input

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.aiu.tdminsight.domain.model.VancomycinWorkflow
import com.aiu.tdminsight.ui.theme.TdmInsightTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class WorkflowSelectionScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun setScreen(onSelected: (VancomycinWorkflow) -> Unit) {
        composeTestRule.setContent {
            TdmInsightTheme {
                WorkflowSelectionScreen(onWorkflowSelected = onSelected)
            }
        }
    }

    @Test
    fun tappingPreDoseOption_reportsPreWorkflow() {
        var selected: VancomycinWorkflow? = null
        setScreen { selected = it }

        composeTestRule.onNodeWithText("Pre-dose (Trough) Only").performClick()

        assertEquals(VancomycinWorkflow.PRE, selected)
    }

    @Test
    fun tappingPostDoseOption_reportsPostWorkflow() {
        var selected: VancomycinWorkflow? = null
        setScreen { selected = it }

        composeTestRule.onNodeWithText("Post-dose (Peak) Only").performClick()

        assertEquals(VancomycinWorkflow.POST, selected)
    }

    @Test
    fun tappingPrePostOption_reportsPrePostWorkflow() {
        var selected: VancomycinWorkflow? = null
        setScreen { selected = it }

        composeTestRule.onNodeWithText("Pre + Post (Both)").performClick()

        assertEquals(VancomycinWorkflow.PRE_POST, selected)
    }
}
