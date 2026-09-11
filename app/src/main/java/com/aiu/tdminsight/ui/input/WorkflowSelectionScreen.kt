package com.aiu.tdminsight.ui.input

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
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
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "TDM INSIGHT",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary
        )
        Text(text = "Select Vancomycin Workflow", style = MaterialTheme.typography.headlineSmall)
        Text(
            text = "Choose which sampling data is available for this case.",
            style = MaterialTheme.typography.bodyMedium
        )

        WorkflowOption(
            title = "Pre-dose (Trough) Only",
            description = "A concentration was drawn before this dose.",
            accentColor = MaterialTheme.colorScheme.primary,
            onClick = { onWorkflowSelected(VancomycinWorkflow.PRE) }
        )
        WorkflowOption(
            title = "Post-dose (Peak) Only",
            description = "A concentration was drawn after the infusion ended.",
            accentColor = MaterialTheme.colorScheme.tertiary,
            onClick = { onWorkflowSelected(VancomycinWorkflow.POST) }
        )
        WorkflowOption(
            title = "Pre + Post (Both)",
            description = "Both a pre-dose and a post-dose concentration are available.",
            accentColor = MaterialTheme.colorScheme.secondary,
            onClick = { onWorkflowSelected(VancomycinWorkflow.PRE_POST) }
        )
    }
}

@Composable
private fun WorkflowOption(
    title: String,
    description: String,
    accentColor: Color,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight()
                    .background(accentColor)
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(text = title, style = MaterialTheme.typography.titleMedium)
                Text(text = description, style = MaterialTheme.typography.bodySmall)
            }
            Text(
                text = "→",
                style = MaterialTheme.typography.titleLarge,
                color = accentColor,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(end = 16.dp)
            )
        }
    }
}
