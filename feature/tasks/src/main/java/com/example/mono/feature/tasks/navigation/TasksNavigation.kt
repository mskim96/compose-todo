package com.example.mono.feature.tasks.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.mono.core.model.Task
import com.example.mono.feature.tasks.tasks.TasksRoute

/**
 * Tasks graph and route.
 */
const val TASKS_GRAPH_ROUTE_PATTERN = "tasks_graph"
private const val TASKS_ROUTE = "tasks_route"

/**
 * The entry point to the Task feature module's graph.
 */
fun NavController.navigateToTasksGraph(navOptions: NavOptions? = null) {
    this.navigate(TASKS_GRAPH_ROUTE_PATTERN, navOptions)
}

/**
 * Task feature module's graph.
 *
 * @param onTaskClick event when a task is clicked.
 * @param nestedGraphs The nested graphs of Task.
 */
fun NavGraphBuilder.tasksGraph(
    onTaskClick: (Task) -> Unit,
    nestedGraphs: NavGraphBuilder.() -> Unit
) {
    navigation(
        route = TASKS_GRAPH_ROUTE_PATTERN,
        startDestination = TASKS_ROUTE
    ) {
        composable(
            route = TASKS_ROUTE
        ) {
            TasksRoute(onTaskClick = onTaskClick)
        }
        nestedGraphs()
    }
}