package com.example.mono.feature.tasks.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.mono.feature.tasks.editGroup.AddEditTaskGroupRoute

const val ADD_EDIT_TASK_GROUP = "add_edit_task_group_route"

fun NavController.navigateToAddEditTaskGroup() {
    this.navigate(ADD_EDIT_TASK_GROUP)
}

fun NavGraphBuilder.addEditTaskGroup(
    onBackClick: () -> Unit
) {
    composable(
        route = ADD_EDIT_TASK_GROUP
    ) {
        AddEditTaskGroupRoute(
            onBackClick = onBackClick
        )
    }
}