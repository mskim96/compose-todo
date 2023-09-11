package com.example.mono.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import com.example.mono.feature.tasks.navigation.TASKS_GRAPH_ROUTE_PATTERN
import com.example.mono.feature.tasks.navigation.addEditTaskList
import com.example.mono.feature.tasks.navigation.bookmarkTasks
import com.example.mono.feature.tasks.navigation.navigateToAddEditTaskList
import com.example.mono.feature.tasks.navigation.navigateToBookmarkTasks
import com.example.mono.feature.tasks.navigation.navigateToTaskDetail
import com.example.mono.feature.tasks.navigation.navigateToTaskList
import com.example.mono.feature.tasks.navigation.navigateToTasks
import com.example.mono.feature.tasks.navigation.taskDetail
import com.example.mono.feature.tasks.navigation.taskList
import com.example.mono.feature.tasks.navigation.tasksGraph
import com.example.mono.ui.MonoAppState

@Composable
fun MonoNavHost(
    appState: MonoAppState,
    modifier: Modifier = Modifier,
    startDestination: String = TASKS_GRAPH_ROUTE_PATTERN
) {
    val navController = appState.navController
    val currentRoute = appState.currentDestination?.route ?: startDestination

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        tasksGraph(
            currentRoute = currentRoute,
            navigateToBookmarkTasks = navController::navigateToBookmarkTasks,
            navigateToAddEditTaskList = navController::navigateToAddEditTaskList,
            navigateToTaskList = { taskListId -> navController.navigateToTaskList(taskListId) },
            onTaskClick = { task -> navController.navigateToTaskDetail(task.id) },
            nestedGraphs = {
                bookmarkTasks(
                    currentRoute = currentRoute,
                    navigateToTasks = navController::navigateToTasks,
                    navigateToAddEditTaskList = navController::navigateToAddEditTaskList,
                    navigateToTaskList = { taskListId -> navController.navigateToTaskList(taskListId) },
                    onTaskClick = { task -> navController.navigateToTaskDetail(task.id) }
                )
                taskList(
                    navigateToTasks = navController::navigateToTasks,
                    navigateToBookmarkTasks = navController::navigateToBookmarkTasks,
                    navigateToAddEditTaskList = navController::navigateToAddEditTaskList,
                    navigateToTaskList = { taskListId -> navController.navigateToTaskList(taskListId) },
                    onTaskClick = { task -> navController.navigateToTaskDetail(task.id) }
                )
                addEditTaskList(
                    onBackClick = navController::navigateToTasks
                )
                taskDetail(
                    onBackClick = navController::popBackStack
                )
            }
        )
    }
}