package com.aiu.tdminsight

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.aiu.tdminsight.ui.navigation.TdmNavGraph
import com.aiu.tdminsight.ui.theme.TdmInsightTheme

/**
 * Application entry point. Screens themselves live in the navigation
 * graph (see ui/navigation/NavGraph.kt) — this class only sets up the
 * theme and the surrounding Scaffold.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TdmInsightTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Surface(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
                        TdmNavGraph()
                    }
                }
            }
        }
    }
}
