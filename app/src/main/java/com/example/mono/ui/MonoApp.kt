package com.example.mono.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import com.example.mono.R
import com.example.mono.core.data.repository.TaskListRepository
import com.example.mono.core.designsystem.component.MonoModalNavigationDrawer
import com.example.mono.core.designsystem.component.MonoModalSheet
import com.example.mono.core.designsystem.component.MonoNavigationDrawerItem
import com.example.mono.core.designsystem.icon.MonoIcons
import com.example.mono.core.model.task.TaskList
import com.example.mono.feature.tasklist.navigation.createTaskListType
import com.example.mono.feature.tasklist.navigation.editTaskListType
import com.example.mono.navigation.MonoNavHost
import com.example.mono.navigation.TopLevelDestination
import kotlinx.coroutines.launch
import com.example.mono.feature.tasklist.R as taskListR

@Composable
fun MonoApp(
    taskListRepository: TaskListRepository,
    appState: MonoAppState = rememberMonoAppState(
        taskListRepository = taskListRepository
    )
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val taskListDestinations by appState.taskListDestinations.collectAsStateWithLifecycle()

    MonoModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            MonoAppDrawer(
                destinations = appState.topLevelDestinations,
                taskListDestinations = taskListDestinations,
                onNavigateToDestination = appState::navigateToTopLevelDestination,
                onNavigateToTaskList = appState::navigateToTaskList,
                onNavigateToEditTaskList = { appState.navigateToEditTaskList(editTaskListType) },
                onNavigateToCreateTaskList = { appState.navigateToEditTaskList(createTaskListType) },
                currentTaskListId = appState.selectedTaskListId,
                currentDestination = appState.currentDestination,
                closeDrawer = { appState.coroutineScope.launch { drawerState.close() } })
        },
    ) {
        MonoNavHost(
            appState = appState,
            openDrawer = { appState.coroutineScope.launch { drawerState.open() } }
        )
    }
}


@Composable
private fun MonoAppDrawer(
    destinations: List<TopLevelDestination>,
    taskListDestinations: List<TaskList>,
    onNavigateToDestination: (TopLevelDestination) -> Unit,
    onNavigateToTaskList: (taskListId: String) -> Unit,
    onNavigateToEditTaskList: () -> Unit,
    onNavigateToCreateTaskList: () -> Unit,
    currentTaskListId: String?,
    currentDestination: NavDestination?,
    closeDrawer: () -> Unit,
    modifier: Modifier = Modifier
) {
    MonoModalSheet(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp)
                .verticalScroll(rememberScrollState())
        ) {
            MonoDrawerHeader()
            if (taskListDestinations.isEmpty()) {
                MonoNavigationDefaultContent(
                    destinations = destinations,
                    currentDestination = currentDestination,
                    onNavigateToDestination = onNavigateToDestination,
                    closeDrawer = closeDrawer
                )
            } else {
                MonoTaskListDrawerContent(
                    destinations = destinations,
                    taskListDestinations = taskListDestinations,
                    onNavigateToDestination = onNavigateToDestination,
                    onNavigateToTaskList = onNavigateToTaskList,
                    onNavigateToEditTaskList = onNavigateToEditTaskList,
                    onNavigateToCreateTaskList = onNavigateToCreateTaskList,
                    currentDestination = currentDestination,
                    currentTaskListId = currentTaskListId,
                    closeDrawer = closeDrawer
                )
            }
        }
    }
}

@Composable
private fun MonoNavigationDefaultContent(
    destinations: List<TopLevelDestination>,
    currentDestination: NavDestination?,
    onNavigateToDestination: (TopLevelDestination) -> Unit,
    closeDrawer: () -> Unit,
    modifier: Modifier = Modifier
) {
    destinations.forEach { destination ->
        val selected =
            currentDestination.isTopLevelDestinationInHierarchy(destination)
        MonoNavigationDrawerItem(
            label = {
                Text(text = stringResource(destination.titleTextId))
            },
            selected = selected,
            onClick = { onNavigateToDestination(destination); closeDrawer() },
            modifier = modifier,
            icon = {
                Icon(
                    imageVector = destination.icon,
                    contentDescription = null
                )
            }
        )
    }
}

@Composable
private fun MonoTaskListDrawerContent(
    destinations: List<TopLevelDestination>,
    taskListDestinations: List<TaskList>,
    onNavigateToDestination: (TopLevelDestination) -> Unit,
    onNavigateToTaskList: (taskListId: String) -> Unit,
    onNavigateToEditTaskList: () -> Unit,
    onNavigateToCreateTaskList: () -> Unit,
    currentDestination: NavDestination?,
    currentTaskListId: String?,
    closeDrawer: () -> Unit,
    modifier: Modifier = Modifier
) {
    destinations.forEach { destination ->
        val selected =
            currentDestination.isTopLevelDestinationInHierarchy(destination)
        if (destination != TopLevelDestination.CREATE_LIST) {
            MonoNavigationDrawerItem(
                selected = selected,
                label = { Text(text = stringResource(destination.titleTextId)) },
                onClick = { onNavigateToDestination(destination); closeDrawer() },
                modifier = modifier,
                icon = {
                    Icon(
                        imageVector = destination.icon,
                        contentDescription = null
                    )
                }
            )
        } else {
            MonoTaskListDrawerItem(
                currentTaskListId = currentTaskListId,
                taskLists = taskListDestinations,
                onNavigateToTaskList = onNavigateToTaskList,
                onNavigateToEditTaskLists = { onNavigateToEditTaskList(); closeDrawer() },
                onNavigateToCreateTaskList = { onNavigateToCreateTaskList(); closeDrawer() },
                closeDrawer = closeDrawer,
                modifier = modifier
            )
        }
    }
}

@Composable
private fun MonoTaskListDrawerItem(
    currentTaskListId: String?,
    taskLists: List<TaskList>,
    onNavigateToTaskList: (String) -> Unit,
    onNavigateToEditTaskLists: () -> Unit,
    onNavigateToCreateTaskList: () -> Unit,
    closeDrawer: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Divider(modifier = Modifier.padding(vertical = 8.dp))
        TaskListItemHeader(
            onNavigateToEditTaskList = onNavigateToEditTaskLists,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        taskLists.forEach { taskList ->
            MonoNavigationDrawerItem(
                label = { Text(text = taskList.name) },
                selected = currentTaskListId == taskList.id,
                onClick = { onNavigateToTaskList(taskList.id); closeDrawer() },
                icon = {
                    Icon(
                        imageVector = MonoIcons.TaskList,
                        contentDescription = null
                    )
                }
            )
        }
        CreateTaskListButton(navigateToCreateTaskList = onNavigateToCreateTaskList)
        Divider(modifier = Modifier.padding(vertical = 8.dp))
    }
}

@Composable
private fun CreateTaskListButton(
    navigateToCreateTaskList: () -> Unit,
    modifier: Modifier = Modifier
) {
    MonoNavigationDrawerItem(
        label = { Text(text = stringResource(taskListR.string.create_new_task_list)) },
        selected = false,
        onClick = navigateToCreateTaskList,
        modifier = modifier,
        icon = { Icon(imageVector = MonoIcons.Add, contentDescription = null) }
    )
}

@Composable
private fun TaskListItemHeader(
    onNavigateToEditTaskList: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(taskListR.string.drawer_task_lists),
            style = MaterialTheme.typography.bodyMedium
        )
        TextButton(onClick = onNavigateToEditTaskList) {
            Text(text = stringResource(taskListR.string.drawer_task_lists_edit))
        }
    }
}

@Composable
private fun MonoDrawerHeader(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_launcher_foreground),
            contentDescription = null,
            modifier = Modifier.size(72.dp),
            tint = MaterialTheme.colorScheme.secondary
        )
    }
}

private fun NavDestination?.isTopLevelDestinationInHierarchy(destination: TopLevelDestination) =
    this?.hierarchy?.any {
        it.route?.contains(destination.name, true) ?: false
    } ?: false