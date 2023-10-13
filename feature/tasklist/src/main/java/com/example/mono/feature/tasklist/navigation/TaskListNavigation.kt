package com.example.mono.feature.tasklist.navigation

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.mono.core.designsystem.anim.slideIntoUp
import com.example.mono.core.designsystem.anim.slideOutOfDown
import com.example.mono.core.model.task.Task
import com.example.mono.feature.tasklist.EditTaskListRoute
import com.example.mono.feature.tasklist.TaskListRoute

/**
 * Args for related task lists.
 */
const val taskListArgs = "taskListId"
const val editTaskListsModifyType = "task_lists_modify_type"

/**
 * Edit task lists modify type.
 */
const val createTaskListType = "create_task_list"
const val editTaskListType = "edit_task_list"

/**
 * Route for related task lists.
 */
const val editTaskListsRoute = "edit_task_lists_route"
const val taskListRoute = "task_list_route"

/**
 * Task list id type safe arguments wrapper.
 */
internal class TaskListIdArgs(val taskListId: String) {
    constructor(savedStateHandle: SavedStateHandle) :
            this(checkNotNull(savedStateHandle[taskListArgs]) as String)
}

/**
 * Edit task list modify type's type safe arguments wrapper.
 */
internal class EditTaskListsModifyTypeArgs(val modifyType: String) {
    constructor(savedStateHandle: SavedStateHandle) :
            this(checkNotNull(savedStateHandle[editTaskListsModifyType]) as String)
}

/**
 * Navigate to [EditTaskListRoute].
 *
 * @param modifyType edit task list's modify type.
 */
fun NavController.navigateToEditTaskLists(modifyType: String = createTaskListType) {
    this.navigate("$editTaskListsRoute/$modifyType")
}

/**
 * Navigate to [TaskListRoute].
 *
 * @param taskListId task list id.
 * @param popUpDestination the task list screen is not considered a TopLevelDestination, but it is treated as one.
 * Therefore, the previous destination is specified as the pop-up destination to perform the pop-up action.
 */
fun NavController.navigateToTaskList(taskListId: String, popUpDestination: String) {
    this.navigate("$taskListRoute/$taskListId") {
        popUpTo(popUpDestination)
        launchSingleTop = true
    }
}

/**
 * The navigation graph for the Edit task lists screen.
 */
fun NavGraphBuilder.editTaskListsScreen(
    onBackClick: () -> Unit,
) {
    composable(
        route = "$editTaskListsRoute/{$editTaskListsModifyType}",
        arguments = listOf(
            navArgument(editTaskListsModifyType) { NavType.StringType }
        ),
        enterTransition = { slideIntoUp() },
        exitTransition = { slideOutOfDown() }
    ) {
        EditTaskListRoute(
            onBackClick = onBackClick
        )
    }
}

/**
 * The navigation graph for task list screen.
 */
fun NavGraphBuilder.taskListScreen(
    onTaskClick: (Task) -> Unit,
    openDrawer: () -> Unit
) {
    composable(
        route = "$taskListRoute/{$taskListArgs}",
        arguments = listOf(
            navArgument(taskListArgs) { NavType.StringType }
        )
    ) {
        TaskListRoute(
            onTaskClick = onTaskClick,
            openDrawer = openDrawer
        )
    }
}