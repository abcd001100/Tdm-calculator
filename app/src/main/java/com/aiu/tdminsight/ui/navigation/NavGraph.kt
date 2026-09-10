package com.aiu.tdminsight.ui.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.aiu.tdminsight.domain.model.TdmResult
import com.aiu.tdminsight.domain.model.VancomycinWorkflow
import com.aiu.tdminsight.ui.input.PatientFormScreen
import com.aiu.tdminsight.ui.input.WorkflowSelectionScreen
import com.aiu.tdminsight.ui.results.ExplanationScreen
import com.aiu.tdminsight.ui.results.ResultsScreen
import com.aiu.tdminsight.ui.results.sampleTdmResult

/**
 * Top-level navigation graph for the app. Routes are additive: later
 * phases should add to this graph rather than rewrite it.
 *
 * [latestResult] is a small piece of nav-graph-scoped state carrying
 * the most recently calculated [TdmResult] from the input form to the
 * Results/Explanation screens — a full ViewModel felt like more
 * architecture than this hand-off needs right now.
 */
@Composable
fun TdmNavGraph(navController: NavHostController = rememberNavController()) {
    var latestResult by remember { mutableStateOf<TdmResult?>(null) }

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
                    onCalculated = { result ->
                        latestResult = result
                        navController.navigate(Routes.RESULTS)
                    }
                )
            }
        }
        composable(Routes.RESULTS) {
            val result = latestResult
            if (result == null) {
                // Reached without going through the form (e.g. process
                // death) — show fixture data rather than crash, clearly
                // marked as sample data.
                ResultsScreen(
                    result = sampleTdmResult(VancomycinWorkflow.PRE_POST),
                    isSampleData = true,
                    onOpenExplanation = { navController.navigate(Routes.EXPLANATION) }
                )
            } else {
                ResultsScreen(
                    result = result,
                    isSampleData = false,
                    onOpenExplanation = { navController.navigate(Routes.EXPLANATION) }
                )
            }
        }
        composable(Routes.EXPLANATION) {
            val result = latestResult
            if (result == null) {
                ExplanationScreen(result = sampleTdmResult(VancomycinWorkflow.PRE_POST), isSampleData = true)
            } else {
                ExplanationScreen(result = result, isSampleData = false)
            }
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
