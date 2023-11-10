package com.example.mono.feature.detail.navgation

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.example.mono.feature.detail.TaskDetailRoute

/**
 * Task id argument for task detail.
 */
internal const val taskIdArgs = "taskId"

/**
 * Task detail route.
 */
internal const val taskDetailRoute = "task_detail_route"
private const val DEEP_LINK_URI_PATTERN = "mono://$taskDetailRoute/{$taskIdArgs}"

/**
 * Task id type safe arguments wrapper.
 */
internal class TaskIdArgs(val taskId: String) {
    constructor(savedStateHandle: SavedStateHandle) :
            this(checkNotNull(savedStateHandle[taskIdArgs]) as String)
}

/**
 * Navigate to task detail screen.
 */
fun NavController.navigateToTaskDetail(taskId: String, attachmentResult: String? = null, popupDestination: String = "") {
    attachmentResult?.let {
        this.navigate("$taskDetailRoute/$taskId?attachment=$it") {
            popUpTo(popupDestination) {
                inclusive = true
            }
        }
    } ?: this.navigate("$taskDetailRoute/$taskId") {

    }
}

/**
 * The navigation graph for task detail screen.
 *
 * @param onBackClick event when back click.
 */
fun NavGraphBuilder.taskDetail(
    onBackClick: () -> Unit,
    navigateToAttachment: (String, String) -> Unit,
) {
    composable(
        route = "$taskDetailRoute/{$taskIdArgs}?attachment={attachment}",
        arguments = listOf(
            navArgument(taskIdArgs) { type = NavType.StringType },
            navArgument("attachment") { nullable = true }
        ),
        deepLinks = listOf(
            navDeepLink { uriPattern = DEEP_LINK_URI_PATTERN }
        )
    ) {
        TaskDetailRoute(
            onBackClick = onBackClick,
            navigateToAttachment = navigateToAttachment,
        )
    }
}