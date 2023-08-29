package com.example.mono.feature.tasks.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.mono.feature.tasks.createTask.CreateTaskRoute

const val CREATE_TASKS_ROUTE = "create_task_route"

fun NavController.navigateToCreateTask() {
    this.navigate(CREATE_TASKS_ROUTE)
}

fun NavGraphBuilder.createTask(
    onBackClick: () -> Unit,
    onTaskUpdate: () -> Unit
) {
    composable(
        route = CREATE_TASKS_ROUTE
    ) {
        CreateTaskRoute(
            onBackClick = onBackClick,
            onTaskUpdate = onTaskUpdate
        )
    }
}