package com.example.mono.feature.calendar.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.example.mono.feature.calendar.CalendarRoute

const val calendarRoute = "calendar_route"

fun NavController.navigateToCalendar(navOptions: NavOptions? = null) {
    this.navigate(calendarRoute, navOptions)
}

fun NavGraphBuilder.calendarScreen(
    openDrawer: () -> Unit
) {
    composable(route = calendarRoute) {
        CalendarRoute(
            openDrawer = openDrawer
        )
    }
}