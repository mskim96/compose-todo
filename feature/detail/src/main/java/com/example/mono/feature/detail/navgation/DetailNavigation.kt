package com.example.mono.feature.detail.navgation

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.mono.feature.detail.TaskDetailRoute

/**
 * Task id argument for task detail.
 */
internal const val taskIdArgs = "taskId"

/**
 * Task id type safe arguments wrapper.
 */
internal class TaskIdArgs(val taskId: String) {
    constructor(savedStateHandle: SavedStateHandle) :
            this(checkNotNull(savedStateHandle[taskIdArgs]) as String)
}

/**
 * Task detail route.
 */
const val taskDetailRoute = "task_detail_route"

/**
 * Navigate to task detail screen.
 */
fun NavController.navigateToTaskDetail(taskId: String) {
    this.navigate("$taskDetailRoute/$taskId")
}

/**
 * The navigation graph for task detail screen.
 *
 * @param onBackClick event when back click.
 */
fun NavGraphBuilder.taskDetail(
    onBackClick: () -> Unit,
) {
    composable(
        route = "$taskDetailRoute/{$taskIdArgs}",
        arguments = listOf(
            navArgument(taskIdArgs) { NavType.StringType }
        )
    ) {
        TaskDetailRoute(
            onBackClick = onBackClick
        )
    }
}