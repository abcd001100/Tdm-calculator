package com.aiu.tdminsight.ui.results

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.aiu.tdminsight.domain.model.ExplanationStep
import com.aiu.tdminsight.domain.model.TdmResult

/**
 * Step-by-step walkthrough of how a result was reached (Case Study §9:
 * Input Values → Intermediate Values → Pharmacokinetic Parameters →
 * Final Result), opened from the Results screen. Pass [isSampleData] =
 * true only when [result] is fixture data rather than a real
 * calculation.
 */
@Composable
fun ExplanationScreen(result: TdmResult, isSampleData: Boolean = false) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = "Calculation Explanation", style = MaterialTheme.typography.headlineSmall)
        if (isSampleData) {
            Text(
                text = "Sample data — no calculation has been run yet.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error
            )
        }

        if (result.explanation.isEmpty()) {
            Text(
                text = "No explanation steps are available for this result yet.",
                style = MaterialTheme.typography.bodyMedium
            )
        } else {
            result.explanation.forEachIndexed { index, step ->
                ExplanationStepCard(stepNumber = index + 1, step = step)
            }
        }
    }
}

@Composable
private fun ExplanationStepCard(stepNumber: Int, step: ExplanationStep) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "$stepNumber. ${step.label}",
                style = MaterialTheme.typography.titleMedium
            )
            Text(text = step.detail, style = MaterialTheme.typography.bodyMedium)
            if (step.value != null) {
                Text(
                    text = "${step.value} ${step.unit.orEmpty()}".trim(),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}
