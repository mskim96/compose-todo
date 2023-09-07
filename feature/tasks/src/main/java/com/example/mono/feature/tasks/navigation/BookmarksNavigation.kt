package com.example.mono.feature.tasks.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.mono.core.model.Task
import com.example.mono.feature.tasks.bookmarks.BookmarksRoute

const val BOOKMARK_ROUTE = "bookmark_route"

fun NavController.navigateToBookmarkTasks() {
    this.navigate(BOOKMARK_ROUTE) {
        popUpTo(TASKS_ROUTE)
    }
}

fun NavGraphBuilder.bookmarkTasks(
    currentRoute: String,
    navigateToTasks: () -> Unit,
    navigateToAddEditTaskList: () -> Unit,
    navigateToTaskList: (taskListId: String) -> Unit,
    onTaskClick: (Task) -> Unit
) {
    composable(
        route = BOOKMARK_ROUTE
    ) {
        BookmarksRoute(
            currentRoute = currentRoute,
            navigateToTasks = navigateToTasks,
            navigateToAddEditTaskList = navigateToAddEditTaskList,
            navigateToTaskList = navigateToTaskList,
            onTaskClick = onTaskClick
        )
    }
}