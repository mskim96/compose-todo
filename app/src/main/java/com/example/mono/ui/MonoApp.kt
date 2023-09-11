package com.example.mono.ui

import androidx.compose.runtime.Composable
import com.example.mono.navigation.MonoNavHost

@Composable
fun MonoApp(
    appState: MonoAppState = rememberMonoAppState()
) {
    MonoNavHost(
        appState = appState
    )
}