package com.aiu.tdminsight.ui.input

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.aiu.tdminsight.domain.model.VancomycinWorkflow

/**
 * First screen of the TDM flow: the user picks which sampling data they
 * have for this case, which determines which of the three Vancomycin
 * workflows runs next (Case Study §3 — "the selected workflow should
 * determine which fields are shown").
 */
@Composable
fun WorkflowSelectionScreen(onWorkflowSelected: (VancomycinWorkflow) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = "Select Vancomycin Workflow", style = MaterialTheme.typography.headlineSmall)
        Text(
            text = "Choose which sampling data is available for this case.",
            style = MaterialTheme.typography.bodyMedium
        )

        WorkflowOption(
            title = "Pre-dose (Trough) Only",
            description = "A concentration was drawn before this dose.",
            onClick = { onWorkflowSelected(VancomycinWorkflow.PRE) }
        )
        WorkflowOption(
            title = "Post-dose (Peak) Only",
            description = "A concentration was drawn after the infusion ended.",
            onClick = { onWorkflowSelected(VancomycinWorkflow.POST) }
        )
        WorkflowOption(
            title = "Pre + Post (Both)",
            description = "Both a pre-dose and a post-dose concentration are available.",
            onClick = { onWorkflowSelected(VancomycinWorkflow.PRE_POST) }
        )
    }
}

@Composable
private fun WorkflowOption(title: String, description: String, onClick: () -> Unit) {
    Card(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(text = title, style = MaterialTheme.typography.titleMedium)
            Text(text = description, style = MaterialTheme.typography.bodySmall)
        }
    }
}
