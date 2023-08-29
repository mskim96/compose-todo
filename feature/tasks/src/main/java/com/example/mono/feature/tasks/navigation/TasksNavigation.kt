package com.example.mono.feature.tasks.navigation

import android.app.Activity
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.example.mono.core.model.Task
import com.example.mono.feature.tasks.TasksRoute

/**
 * Tasks graph and route.
 */
const val TASKS_GRAPH_ROUTE_PATTERN = "tasks_graph"
private const val TASKS_ROUTE = "tasks_route"

/**
 * Tasks argument for displayed message.
 */
const val USER_MESSAGE_ARG = "userMessage"

/**
 * The entry point to the Task feature module's graph.
 */
fun NavController.navigateToTasksGraph(navOptions: NavOptions? = null) {
    this.navigate(TASKS_GRAPH_ROUTE_PATTERN, navOptions)
}

/**
 * Navigate to [TasksRoute].
 */
fun NavController.navigateToTasks(userMessage: Int = 0) {
    this.navigate(
        TASKS_ROUTE.let {
            if (userMessage != 0) "$it?$USER_MESSAGE_ARG=$userMessage" else it
        }
    ) {
        // TODO: When adding a modal drawer or bottom navigation, delegate this logic to the App State.
        popUpTo(TASKS_GRAPH_ROUTE_PATTERN)
    }
}

/**
 * Task feature module's graph.
 *
 * @param onTaskClick event when a task is clicked.
 * @param onCreateTask event when a create task.
 * @param nestedGraphs The nested graphs of Task.
 */
fun NavGraphBuilder.tasksGraph(
    onTaskClick: (Task) -> Unit,
    onCreateTask: () -> Unit,
    nestedGraphs: NavGraphBuilder.() -> Unit
) {
    navigation(
        route = TASKS_GRAPH_ROUTE_PATTERN,
        startDestination = TASKS_ROUTE
    ) {
        composable(
            route = "$TASKS_ROUTE?$USER_MESSAGE_ARG={$USER_MESSAGE_ARG}",
            arguments = listOf(
                navArgument(name = USER_MESSAGE_ARG) { NavType.IntType; defaultValue = 0 }
            )
        ) { entry ->
            TasksRoute(
                userMessage = entry.arguments?.getInt(USER_MESSAGE_ARG)!!,
                onUserMessageDisplayed = { entry.arguments?.putInt(USER_MESSAGE_ARG, 0) },
                onTaskClick = onTaskClick,
                onCreateTask = onCreateTask
            )
        }
        nestedGraphs()
    }
}

const val DELETE_RESULT_OK = Activity.RESULT_FIRST_USER + 1