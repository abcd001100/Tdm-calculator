package com.aiu.tdminsight.ui.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.aiu.tdminsight.domain.model.VancomycinWorkflow
import com.aiu.tdminsight.ui.input.PatientFormScreen
import com.aiu.tdminsight.ui.input.WorkflowSelectionScreen
import com.aiu.tdminsight.ui.results.ExplanationScreen
import com.aiu.tdminsight.ui.results.ResultsScreen
import com.aiu.tdminsight.ui.results.sampleTdmResult

/**
 * Top-level navigation graph for the app. Every destination here is a
 * placeholder ([PlaceholderScreen]) — real screens replace them one at a
 * time in later roadmap phases (see docs/Implementation_Roadmap.md).
 * Routes are additive: later phases should add to this graph rather than
 * rewrite it, since Phase 3 and Phase 4 both touch this file.
 */
@Composable
fun TdmNavGraph(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = Routes.WORKFLOW_SELECTION) {
        composable(Routes.WORKFLOW_SELECTION) {
            WorkflowSelectionScreen(
                onWorkflowSelected = { workflow -> navController.navigate(Routes.inputForm(workflow)) }
            )
        }
        composable(
            route = Routes.INPUT_FORM,
            arguments = listOf(navArgument(Routes.INPUT_FORM_ARG_WORKFLOW) { type = NavType.StringType })
        ) { backStackEntry ->
            val workflowName = backStackEntry.arguments?.getString(Routes.INPUT_FORM_ARG_WORKFLOW)
            val workflow = workflowName?.let { runCatching { VancomycinWorkflow.valueOf(it) }.getOrNull() }
            if (workflow == null) {
                PlaceholderScreen(
                    title = "Patient & Workflow Input",
                    description = "No workflow was selected — go back and choose one.",
                    onNext = null
                )
            } else {
                PatientFormScreen(
                    workflow = workflow,
                    onContinue = { navController.navigate(Routes.RESULTS) }
                )
            }
        }
        composable(Routes.RESULTS) {
            // TODO(Phase 5): replace this fixed sample with the actual
            // validated WorkflowInput + real engine output, once the
            // calculation engine is unblocked and screens are wired
            // together with shared state.
            ResultsScreen(
                result = sampleTdmResult(VancomycinWorkflow.PRE_POST),
                onOpenExplanation = { navController.navigate(Routes.EXPLANATION) }
            )
        }
        composable(Routes.EXPLANATION) {
            ExplanationScreen(result = sampleTdmResult(VancomycinWorkflow.PRE_POST))
        }
    }
}

@Composable
private fun PlaceholderScreen(title: String, description: String, onNext: (() -> Unit)?) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(text = title, style = MaterialTheme.typography.headlineSmall)
        Text(text = description, style = MaterialTheme.typography.bodyMedium)
        if (onNext != null) {
            Button(onClick = onNext) {
                Text("Continue")
            }
        }
    }
}
