package com.example.mono.feature.tasks.navigation

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.mono.feature.tasks.taskDetail.TaskDetailRoute

/**
 * Task id argument for task detail.
 */
internal const val TASK_ID_ARGS = "taskId"

/**
 * Task id type safe arguments wrapper.
 */
internal class TaskIdArgs(val taskId: String) {
    constructor(savedStateHandle: SavedStateHandle) :
            this(checkNotNull(savedStateHandle[TASK_ID_ARGS]) as String)
}

/**
 * Task detail route.
 */
const val TASK_DETAIL_ROUTE = "task_detail_route"

/**
 * Navigate to [TaskDetailRoute].
 */
fun NavController.navigateToTaskDetail(taskId: String) {
    this.navigate("$TASK_DETAIL_ROUTE/$taskId")
}


/**
 * nested graph of [tasksGraph].
 *
 * @param onBackClick event when back click.
 */
fun NavGraphBuilder.taskDetail(
    onBackClick: () -> Unit,
) {
    composable(
        route = "$TASK_DETAIL_ROUTE/{$TASK_ID_ARGS}",
        arguments = listOf(
            navArgument(TASK_ID_ARGS) { NavType.StringType }
        )
    ) {
        TaskDetailRoute(onBackClick = onBackClick)
    }
}