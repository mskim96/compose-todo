package com.example.mono.feature.tasks.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.mono.feature.tasks.addEditTaskList.AddEditTaskListRoute

const val ADD_EDIT_TASK_LIST = "add_edit_task_list_route"

fun NavController.navigateToAddEditTaskList() {
    this.navigate(ADD_EDIT_TASK_LIST)
}

fun NavGraphBuilder.addEditTaskList(
    onBackClick: () -> Unit
) {
    composable(
        route = ADD_EDIT_TASK_LIST
    ) {
        AddEditTaskListRoute(
            onBackClick = onBackClick
        )
    }
}