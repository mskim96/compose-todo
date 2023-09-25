package com.example.mono.feature.settings.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.mono.core.designsystem.anim.slideIntoUp
import com.example.mono.core.designsystem.anim.slideOutOfDown
import com.example.mono.feature.settings.SettingsRoute

const val settingsRoute = "settings_route"

fun NavController.navigateToSettings() {
    this.navigate(settingsRoute) {
        launchSingleTop = true
    }
}

fun NavGraphBuilder.settingsScreen(
    onBackClick: () -> Unit
) {
    composable(
        route = settingsRoute,
        enterTransition = { slideIntoUp() },
        exitTransition = { slideOutOfDown() }
    ) {
        SettingsRoute(
            onBackClick = onBackClick
        )
    }
}