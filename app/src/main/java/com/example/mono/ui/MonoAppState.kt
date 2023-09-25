package com.example.mono.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import com.example.mono.core.data.repository.TaskListRepository
import com.example.mono.core.model.TaskList
import com.example.mono.feature.bookmarks.navigation.navigateToBookmarks
import com.example.mono.feature.calendar.navigation.navigateToCalendar
import com.example.mono.feature.reminders.navigation.navigateToReminders
import com.example.mono.feature.settings.navigation.navigateToSettings
import com.example.mono.feature.tasklist.navigation.navigateToEditTaskLists
import com.example.mono.feature.tasklist.navigation.navigateToTaskList
import com.example.mono.feature.tasklist.navigation.taskListArgs
import com.example.mono.feature.tasks.navigation.navigateToTasks
import com.example.mono.feature.tasks.navigation.tasksRoute
import com.example.mono.navigation.TopLevelDestination
import com.example.mono.navigation.TopLevelDestination.BOOKMARKS
import com.example.mono.navigation.TopLevelDestination.CALENDAR
import com.example.mono.navigation.TopLevelDestination.CREATE_LIST
import com.example.mono.navigation.TopLevelDestination.REMINDERS
import com.example.mono.navigation.TopLevelDestination.SETTINGS
import com.example.mono.navigation.TopLevelDestination.TASKS
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

@Composable
fun rememberMonoAppState(
    taskListRepository: TaskListRepository,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    navController: NavHostController = rememberNavController()
) = remember(
    navController,
    coroutineScope,
    taskListRepository
) {
    MonoAppState(
        navController,
        coroutineScope,
        taskListRepository
    )
}

@Stable
class MonoAppState(
    val navController: NavHostController,
    val coroutineScope: CoroutineScope,
    taskListRepository: TaskListRepository
) {
    private val currentBackStackEntry
        @Composable get() = navController.currentBackStackEntryAsState().value

    val currentDestination: NavDestination?
        @Composable get() = currentBackStackEntry?.destination

    /**
     * Map of top level destinations to be used in Drawer.
     */
    val topLevelDestinations: List<TopLevelDestination> = TopLevelDestination.values().asList()

    /**
     * List of items to display in the Drawer if the user has created task lists.
     */
    val taskListDestinations: StateFlow<List<TaskList>> = taskListRepository.getTaskLists()
        .stateIn(
            coroutineScope,
            SharingStarted.WhileSubscribed(5_000L),
            initialValue = emptyList(),
        )

    /**
     * When there is a selected task list, the ID used to indicate selection in the drawer.
     */
    val selectedTaskListId: String?
        @Composable get() = currentBackStackEntry?.arguments?.getString(taskListArgs)

    /**
     * UI logic for navigating to a top level destination in the app. Top level destinations have
     * only one copy of the destination of the back stack, and save and restore state whenever you
     * navigate to and from it.
     *
     * @param topLevelDestination: The destination the app needs to navigate to.
     */
    fun navigateToTopLevelDestination(topLevelDestination: TopLevelDestination) {
        val topLevelNavOptions = navOptions {
            // Pop up to the start destination of the graph to
            // avoid building up a large stack of destinations
            // on the back stack as users select items
            popUpTo(navController.graph.findStartDestination().id) {
//                saveState = true
            }
            // Avoid multiple copies of the same destination when
            // reselecting the same item
            launchSingleTop = true
            // Restore state when reselecting a previously selected item
//            restoreState = true
        }

        when (topLevelDestination) {
            TASKS -> navController.navigateToTasks(topLevelNavOptions)
            BOOKMARKS -> navController.navigateToBookmarks(topLevelNavOptions)
            REMINDERS -> navController.navigateToReminders(topLevelNavOptions)
            CALENDAR -> navController.navigateToCalendar(topLevelNavOptions)
            CREATE_LIST -> navController.navigateToEditTaskLists()
            SETTINGS -> navController.navigateToSettings()
        }
    }

    fun navigateToEditTaskList(taskListsModifyType: String) {
        navController.navigateToEditTaskLists(taskListsModifyType)
    }

    fun navigateToTaskList(taskListId: String, popUpDestination: String = tasksRoute) {
        navController.navigateToTaskList(taskListId, popUpDestination)
    }
}