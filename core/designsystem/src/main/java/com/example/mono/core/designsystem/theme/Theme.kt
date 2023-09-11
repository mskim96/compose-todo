package com.example.mono.core.designsystem.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val monoDarkColorScheme = darkColorScheme(
    background = Tan20,
    onBackground = Tan89,
    surface = Tan10,
    onSurface = Tan89,
    surfaceVariant = Orange60,
    onSurfaceVariant = Tan89,
    primary = Orange95,
    onPrimary = White,
    primaryContainer = Orange95,
    onPrimaryContainer = Orange40,
    secondaryContainer = Tan30,
    onSecondaryContainer = Tan89,
    outline = Tan70
)

private val monoLightColorScheme = lightColorScheme(
    background = White,
    onBackground = Tan29,
    surface = White,
    onSurface = Tan50,
    surfaceVariant = Tan95,
    onSurfaceVariant = Tan50,
    primary = Orange70,
    onPrimary = White,
    primaryContainer = Orange99,
    onPrimaryContainer = Tan50,
    secondaryContainer = Tan90,
    onSecondaryContainer = Tan50,
    outline = Tan65
)

@Composable
fun MonoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> monoDarkColorScheme
        else -> monoLightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.apply {
                // Set the color of the system status bar and
                // navigation bar (system bar).
                statusBarColor = Color.Transparent.toArgb()
                navigationBarColor = Color.Transparent.toArgb()
            }

            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = !darkTheme
                isAppearanceLightNavigationBars = !darkTheme
            }

        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = MonoTypography,
        content = content
    )
}