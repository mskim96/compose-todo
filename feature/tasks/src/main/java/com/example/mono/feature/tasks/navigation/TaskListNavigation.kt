package com.example.mono.feature.tasks.navigation

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.mono.core.model.Task
import com.example.mono.feature.tasks.taskList.TaskListRoute

internal const val TASK_LIST_ARGS = "taskListId"

internal class TaskListIdArgs(val taskListId: String) {
    constructor(savedStateHandle: SavedStateHandle) :
            this(checkNotNull(savedStateHandle[TASK_LIST_ARGS]) as String)
}

const val TASK_LIST_ROUTE = "task_list_route"

fun NavController.navigateToTaskList(taskListId: String) {
    this.navigate("$TASK_LIST_ROUTE/$taskListId") {
        popUpTo(TASKS_ROUTE)
    }
}

fun NavGraphBuilder.taskList(
    navigateToTasks: () -> Unit,
    navigateToBookmarkTasks: () -> Unit,
    navigateToAddEditTaskList: () -> Unit,
    navigateToTaskList: (taskListId: String) -> Unit,
    onTaskClick: (Task) -> Unit
) {
    composable(
        route = "$TASK_LIST_ROUTE/{$TASK_LIST_ARGS}",
        arguments = listOf(
            navArgument(TASK_LIST_ARGS) { NavType.StringType }
        )
    ) {
        TaskListRoute(
            navigateToTasks = navigateToTasks,
            navigateToBookmarkTasks = navigateToBookmarkTasks,
            navigateToAddEditTaskList = navigateToAddEditTaskList,
            navigateToTaskList = navigateToTaskList,
            onTaskClick = onTaskClick
        )
    }
}