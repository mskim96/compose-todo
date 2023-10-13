package com.example.mono.feature.tasks.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.example.mono.core.model.task.Task
import com.example.mono.feature.tasks.TasksRoute

/**
 * Tasks graph and route.
 */
const val tasksRoute = "tasks_route"

/**
 * The entry point to the Task feature module.
 */
fun NavController.navigateToTasks(navOptions: NavOptions? = null) {
    this.navigate(tasksRoute, navOptions)
}

fun NavController.navigateToTasks() {
    this.navigate(tasksRoute) {
        popUpTo(tasksRoute)
        launchSingleTop = true
    }
}

/**
 * The navigation graph for tasks screen.
 *
 * @param onTaskClick event when a task is clicked.
 */
fun NavGraphBuilder.tasksScreen(
    onTaskClick: (Task) -> Unit,
    openDrawer: () -> Unit
) {
    composable(route = tasksRoute) {
        TasksRoute(
            onTaskClick = onTaskClick,
            openDrawer = openDrawer
        )
    }
}