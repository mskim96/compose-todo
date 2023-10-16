package com.example.mono.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import com.example.mono.feature.bookmarks.navigation.bookmarksScreen
import com.example.mono.feature.calendar.navigation.calendarScreen
import com.example.mono.feature.detail.navgation.navigateToTaskDetail
import com.example.mono.feature.detail.navgation.taskDetail
import com.example.mono.feature.reminders.navigation.remindersScreen
import com.example.mono.feature.settings.navigation.settingsScreen
import com.example.mono.feature.tasklist.navigation.editTaskListsScreen
import com.example.mono.feature.tasklist.navigation.taskListScreen
import com.example.mono.feature.tasks.navigation.navigateToTasks
import com.example.mono.feature.tasks.navigation.tasksRoute
import com.example.mono.feature.tasks.navigation.tasksScreen
import com.example.mono.ui.MonoAppState

@Composable
fun MonoNavHost(
    appState: MonoAppState,
    modifier: Modifier = Modifier,
    openDrawer: () -> Unit = {},
    startDestination: String = tasksRoute
) {
    val navController = appState.navController

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        tasksScreen(
            onTaskClick = { task -> navController.navigateToTaskDetail(task.id) },
            openDrawer = openDrawer
        )
        bookmarksScreen(
            onTaskClick = { task -> navController.navigateToTaskDetail(task.id) },
            openDrawer = openDrawer
        )
        remindersScreen(
            openDrawer = openDrawer
        )
        calendarScreen(
            onTaskClick = { task -> navController.navigateToTaskDetail(task.id) },
            openDrawer = openDrawer
        )
        taskListScreen(
            onTaskClick = { task -> navController.navigateToTaskDetail(task.id) },
            openDrawer = openDrawer
        )
        editTaskListsScreen(
            onBackClick = navController::navigateToTasks
        )
        taskDetail(
            onBackClick = navController::popBackStack
        )
        settingsScreen(
            onBackClick = navController::popBackStack
        )
    }
}