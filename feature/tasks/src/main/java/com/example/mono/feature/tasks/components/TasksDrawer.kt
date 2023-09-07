package com.example.mono.feature.tasks.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MenuOpen
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.Bookmarks
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mono.core.designsystem.component.MonoModalSheet
import com.example.mono.core.designsystem.component.MonoNavigationDrawerItem
import com.example.mono.core.designsystem.theme.MonoTheme
import com.example.mono.core.model.TaskList
import com.example.mono.feature.tasks.navigation.BOOKMARK_ROUTE
import com.example.mono.feature.tasks.navigation.TASKS_ROUTE

@Composable
fun TasksModalDrawerContent(
    currentRoute: String,
    taskLists: List<TaskList>,
    navigateToTasks: () -> Unit,
    navigateToBookmarks: () -> Unit,
    navigateToAddEditTaskList: () -> Unit,
    navigateToTaskList: (taskListId: String) -> Unit,
    onDrawerClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    MonoModalSheet(modifier = modifier) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 8.dp)
        ) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Mono",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDrawerClicked) {
                        Icon(
                            imageVector = Icons.Default.MenuOpen,
                            contentDescription = null
                        )
                    }
                }
            }

            item {
                MonoNavigationDrawerItem(
                    label = { Text(text = "All Tasks") },
                    selected = currentRoute == TASKS_ROUTE,
                    onClick = { navigateToTasks(); onDrawerClicked() },
                    icon = {
                        Icon(
                            imageVector = Icons.Outlined.Book,
                            contentDescription = null
                        )
                    }
                )
            }

            item {
                MonoNavigationDrawerItem(
                    label = { Text(text = "Bookmarks") },
                    selected = currentRoute == BOOKMARK_ROUTE,
                    onClick = { navigateToBookmarks(); onDrawerClicked() },
                    icon = {
                        Icon(
                            imageVector = Icons.Outlined.Bookmarks,
                            contentDescription = null
                        )
                    }
                )
            }

            item {
                MonoNavigationDrawerItem(
                    label = { Text(text = "Reminder") },
                    selected = false,
                    onClick = {},
                    icon = {
                        Icon(
                            imageVector = Icons.Outlined.Notifications,
                            contentDescription = null
                        )
                    }
                )
            }

            if (taskLists.isNotEmpty()) {
                item {
                    Divider(modifier = Modifier.padding(16.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Lists",
                            style = MaterialTheme.typography.labelLarge
                        )
                        TextButton(onClick = navigateToAddEditTaskList) {
                            Text(text = "Edit")
                        }
                    }
                }
            }

            items(taskLists) { taskList ->
                MonoNavigationDrawerItem(
                    label = { Text(text = taskList.name) },
                    selected = currentRoute.contains(taskList.id),
                    onClick = { navigateToTaskList(taskList.id); onDrawerClicked() },
                    icon = { Icon(imageVector = Icons.Outlined.Folder, contentDescription = null) }
                )
            }

            item {
                MonoNavigationDrawerItem(
                    label = { Text(text = "Create new list") },
                    selected = false,
                    onClick = { navigateToAddEditTaskList(); onDrawerClicked() },
                    icon = { Icon(imageVector = Icons.Default.Add, contentDescription = null) }
                )
            }

            if (taskLists.isNotEmpty()) {
                item {
                    Divider(modifier = Modifier.padding(16.dp))
                }
            }

            item {
                MonoNavigationDrawerItem(
                    label = { Text(text = "Setting") },
                    selected = false,
                    onClick = {},
                    icon = {
                        Icon(
                            imageVector = Icons.Outlined.Settings,
                            contentDescription = null
                        )
                    }
                )
            }
        }
    }
}

@Preview(name = "Tasks Drawer has task lists preview.")
@Composable
private fun TasksModalDrawerContentHasTaskListPreview() {
    MonoTheme {
        TasksModalDrawerContent(
            currentRoute = TASKS_ROUTE,
            taskLists = listOf(
                TaskList("1", "preview1"),
                TaskList("2", "preview2")
            ),
            navigateToTasks = {},
            navigateToBookmarks = {},
            navigateToAddEditTaskList = {},
            navigateToTaskList = {},
            onDrawerClicked = {}
        )
    }
}

@Preview(name = "Tasks Drawer has task lists preview.")
@Composable
private fun TasksModalDrawerContentPreview() {
    MonoTheme {
        TasksModalDrawerContent(
            currentRoute = TASKS_ROUTE,
            taskLists = listOf(),
            navigateToTasks = {},
            navigateToBookmarks = {},
            navigateToAddEditTaskList = {},
            navigateToTaskList = {},
            onDrawerClicked = {}
        )
    }
}