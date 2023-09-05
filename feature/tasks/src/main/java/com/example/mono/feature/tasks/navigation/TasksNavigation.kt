package com.example.mono.feature.tasks.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.example.mono.core.model.Task
import com.example.mono.feature.tasks.tasks.TasksRoute

/**
 * Task group filter argument.
 */
const val TASKS_GROUP_ID_ARGS = "taskGroupId"

/**
 * Tasks graph and route.
 */
const val TASKS_GRAPH_ROUTE_PATTERN = "tasks_graph"
private const val TASKS_DESTINATION = "tasks_route"
private const val TASKS_ROUTE = "tasks_route/{$TASKS_GROUP_ID_ARGS}"

/**
 * The entry point to the Task feature module's graph.
 */
fun NavController.navigateToTasksGraph(navOptions: NavOptions? = null) {
    this.navigate(TASKS_GRAPH_ROUTE_PATTERN, navOptions)
}

fun NavController.navigateToTasks(groupId: String) {
    this.navigate("$TASKS_DESTINATION/$groupId") {
        popUpTo(TASKS_GRAPH_ROUTE_PATTERN)
    }
}

/**
 * Task feature module's graph.
 *
 * @param navigateToTaskGroup
 * @param onTaskClick event when a task is clicked.
 * @param nestedGraphs The nested graphs of Task.
 */
fun NavGraphBuilder.tasksGraph(
    navigateToTaskGroup: (groupId: String) -> Unit,
    navigateToAddEditTaskGroup: () -> Unit,
    onTaskClick: (Task) -> Unit,
    nestedGraphs: NavGraphBuilder.() -> Unit
) {
    navigation(
        route = TASKS_GRAPH_ROUTE_PATTERN,
        startDestination = TASKS_ROUTE
    ) {
        composable(
            route = TASKS_ROUTE,
            arguments = listOf(
                navArgument(TASKS_GROUP_ID_ARGS) { type = NavType.StringType }
            )
        ) {
            TasksRoute(
                navigateToTaskGroup = navigateToTaskGroup,
                navigateToAddEditTaskGroup = navigateToAddEditTaskGroup,
                onTaskClick = onTaskClick
            )
        }
        nestedGraphs()
    }
}