package com.example.mono.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import com.example.mono.feature.tasks.navigation.navigateToTasksGraph
import com.example.mono.navigation.TopLevelDestination
import com.example.mono.navigation.TopLevelDestination.CALENDAR
import com.example.mono.navigation.TopLevelDestination.NOTES
import com.example.mono.navigation.TopLevelDestination.TASKS

@Composable
fun rememberMonoAppState(
    navController: NavHostController = rememberNavController()
) = remember(navController) {
    MonoAppState(navController)
}

@Stable
class MonoAppState(
    val navController: NavHostController
) {
    val currentDestination: NavDestination?
        @Composable get() = navController.currentBackStackEntryAsState().value?.destination

    val currentTopLevelDestination: TopLevelDestination?
        @Composable get() = when (currentDestination?.route) {
            else -> null
        }

    val topLevelDestinations: List<TopLevelDestination> = TopLevelDestination.values().asList()

    fun navigateToTopLevelDestination(topLevelDestination: TopLevelDestination) {
        val topLevelNavOptions = navOptions {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
        when (topLevelDestination) {
            TASKS -> navController.navigateToTasksGraph(topLevelNavOptions)
            CALENDAR -> Unit
            NOTES -> Unit
        }
    }
}