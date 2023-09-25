package com.example.mono.feature.reminders.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.example.mono.feature.reminders.RemindersRoute

const val remindersRoute = "reminders_route"

fun NavController.navigateToReminders(navOptions: NavOptions? = null) {
    this.navigate(remindersRoute, navOptions)
}

fun NavGraphBuilder.remindersScreen(
    openDrawer: () -> Unit
) {
    composable(route = remindersRoute) {
        RemindersRoute(
            openDrawer = openDrawer
        )
    }
}