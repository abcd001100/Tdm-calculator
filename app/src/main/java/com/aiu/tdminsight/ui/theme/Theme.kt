package com.aiu.tdminsight.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val LightColors = lightColorScheme(
    primary = TealPrimaryLight,
    secondary = SlateNeutral,
    tertiary = AmberWarning
)

private val DarkColors = darkColorScheme(
    primary = TealPrimaryDark,
    secondary = SlateNeutral,
    tertiary = AmberWarning
)

/**
 * App-wide Material 3 theme. Falls back to Android's dynamic color
 * (Material You) on supported devices, otherwise uses the fixed clinical
 * teal palette above.
 */
@Composable
fun TdmInsightTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColors
        else -> LightColors
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
