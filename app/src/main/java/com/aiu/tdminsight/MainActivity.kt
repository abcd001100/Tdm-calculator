package com.aiu.tdminsight

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.aiu.tdminsight.ui.theme.TdmInsightTheme

/**
 * Application entry point. This is a themed shell only — navigation and
 * real screens land in later roadmap phases (see
 * docs/Implementation_Roadmap.md, Phase 1.2 onward).
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TdmInsightTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Surface(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
                        AppPlaceholder()
                    }
                }
            }
        }
    }
}

@Composable
private fun AppPlaceholder() {
    Text(text = "TDM Insight")
}

@Preview(showBackground = true)
@Composable
private fun AppPlaceholderPreview() {
    TdmInsightTheme {
        AppPlaceholder()
    }
}
