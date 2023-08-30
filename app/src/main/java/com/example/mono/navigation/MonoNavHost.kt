package com.example.mono.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import com.example.mono.feature.tasks.navigation.TASKS_GRAPH_ROUTE_PATTERN
import com.example.mono.feature.tasks.navigation.navigateToTaskDetail
import com.example.mono.feature.tasks.navigation.taskDetail
import com.example.mono.feature.tasks.navigation.tasksGraph
import com.example.mono.ui.MonoAppState

@Composable
fun MonoNavHost(
    appState: MonoAppState,
    modifier: Modifier = Modifier,
    startDestination: String = TASKS_GRAPH_ROUTE_PATTERN
) {
    val navController = appState.navController
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        tasksGraph(
            onTaskClick = { task -> navController.navigateToTaskDetail(task.id) },
            nestedGraphs = {
                taskDetail(
                    onBackClick = navController::popBackStack
                )
            }
        )
    }
}