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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

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
            PlaceholderScreen(
                title = "Workflow Selection",
                description = "Vancomycin Pre / Post / Pre+Post selection lands here in Phase 3.1.",
                onNext = { navController.navigate(Routes.INPUT_FORM) }
            )
        }
        composable(Routes.INPUT_FORM) {
            PlaceholderScreen(
                title = "Patient & Workflow Input",
                description = "The dynamic patient and workflow input form lands here in Phase 3.2/3.3.",
                onNext = { navController.navigate(Routes.RESULTS) }
            )
        }
        composable(Routes.RESULTS) {
            PlaceholderScreen(
                title = "Results",
                description = "Intermediate and final calculation results land here in Phase 4.1.",
                onNext = { navController.navigate(Routes.EXPLANATION) }
            )
        }
        composable(Routes.EXPLANATION) {
            PlaceholderScreen(
                title = "Explanation",
                description = "The step-by-step calculation explanation lands here in Phase 4.2.",
                onNext = null
            )
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
