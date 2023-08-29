package com.example.mono.feature.tasks.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.mono.feature.tasks.taskDetail.TaskDetailRoute

/**
 * Task detail route.
 */
const val TASK_DETAIL_ROUTE = "task_detail_route"

/**
 * Task id argument for task detail.
 */
internal const val TASK_ID = "taskId"

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
 * @param onUpdateTask event when task is updated.
 */
fun NavGraphBuilder.taskDetail(
    onBackClick: () -> Unit,
    onUpdateTask: () -> Unit,
    onDeleteTask: () -> Unit
) {
    composable(
        route = "$TASK_DETAIL_ROUTE/{$TASK_ID}",
        arguments = listOf(
            navArgument(TASK_ID) { NavType.StringType }
        )
    ) {
        TaskDetailRoute(
            onBackClick = onBackClick,
            onUpdateTask = onUpdateTask,
            onDeleteTask = onDeleteTask
        )
    }
}