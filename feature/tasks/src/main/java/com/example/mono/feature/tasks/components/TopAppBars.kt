package com.example.mono.feature.tasks.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmarks
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.outlined.Bookmarks
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.mono.core.designsystem.component.MonoTopAppBar
import com.example.mono.core.model.TaskSortingType
import com.example.mono.feature.tasks.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasksTopAppBar(
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    openDrawer: () -> Unit,
    navigateToSearch: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior? = null
) {
    MonoTopAppBar(
        title = title,
        modifier = modifier,
        navigationIcon = {
            IconButton(onClick = openDrawer) {
                Icon(imageVector = Icons.Default.Menu, contentDescription = null)
            }
        },
        actions = {
            IconButton(onClick = navigateToSearch) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = stringResource(id = R.string.search)
                )
            }
            IconButton(onClick = { }) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = null
                )
            }
        },
        scrollBehavior = scrollBehavior
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun TaskDetailTopAppBar(
    onBackClick: () -> Unit,
    isBookmarked: Boolean,
    onTaskBookmarkChanged: () -> Unit,
    onDeleteTask: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bookmarkedIcon = if (isBookmarked) {
        Icons.Default.Bookmarks
    } else {
        Icons.Outlined.Bookmarks
    }

    MonoTopAppBar(
        title = { Text(text = "") },
        modifier = modifier,
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
            }
        },
        actions = {
            IconButton(onClick = onTaskBookmarkChanged) {
                Icon(
                    imageVector = bookmarkedIcon,
                    contentDescription = null
                )
            }
            TaskDetailMoreMenu(
                onDeleteTask = onDeleteTask
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditTaskGroupTopAppBar(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior? = null
) {
    MonoTopAppBar(
        titleRes = R.string.task_group_add_edit,
        modifier = modifier,
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
            }
        },
        scrollBehavior = scrollBehavior
    )
}

@Composable
private fun TaskDetailMoreMenu(
    onDeleteTask: () -> Unit
) {
    TopAppBarDropdownMenu(
        iconContent = {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = stringResource(id = R.string.menu_delete_task)
            )
        }
    ) { closeMenu ->
        DropdownMenuItem(
            text = {
                Text(text = stringResource(id = R.string.delete_task))
            },
            onClick = { onDeleteTask(); closeMenu() },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = null
                )
            },
            colors = MenuDefaults.itemColors(
                textColor = MaterialTheme.colorScheme.error,
                leadingIconColor = MaterialTheme.colorScheme.error
            )
        )
    }
}

@Composable
fun TaskSortTypeMoreMenu(
    selectedSortType: TaskSortingType,
    onSortNoneTasks: () -> Unit,
    onSortDateTasks: () -> Unit
) {
    TasksDropdownMenu(
        iconContent = {
            Icon(
                imageVector = Icons.Default.Sort,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = selectedSortType.sortedName,
                modifier = Modifier.padding(horizontal = 8.dp),
                style = MaterialTheme.typography.labelLarge
            )
        }
    ) {closeMenu ->
        DropdownMenuItem(
            text = {
                Text(text = TaskSortingType.NONE.sortedName)
            },
            onClick = { onSortNoneTasks(); closeMenu() },
            leadingIcon = {
                if(selectedSortType == TaskSortingType.NONE){
                    Icon(
                        imageVector = Icons.Outlined.Check,
                        contentDescription = null
                    )
                } else {
                    Spacer(modifier = Modifier.size(24.dp))
                }
            }
        )
        DropdownMenuItem(
            text = {
                Text(text = TaskSortingType.DATE.sortedName)
            },
            onClick = { onSortDateTasks(); closeMenu() },
            leadingIcon = {
                if(selectedSortType == TaskSortingType.DATE){
                    Icon(
                        imageVector = Icons.Outlined.Check,
                        contentDescription = null
                    )
                } else {
                    Spacer(modifier = Modifier.size(24.dp))
                }
            }
        )
    }
}

@Composable
private fun TasksDropdownMenu(
    iconContent: @Composable () -> Unit,
    content: @Composable ColumnScope.(() -> Unit) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.wrapContentSize(Alignment.TopEnd)) {
        Row(
            modifier = Modifier.clickable { expanded = !expanded },
            verticalAlignment = Alignment.CenterVertically
        ) {
            iconContent()
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.wrapContentSize(Alignment.TopEnd)
        ) {
            content { expanded = !expanded }
        }
    }
}

@Composable
private fun TopAppBarDropdownMenu(
    iconContent: @Composable () -> Unit,
    content: @Composable ColumnScope.(() -> Unit) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.wrapContentSize(Alignment.TopEnd)) {
        IconButton(onClick = { expanded = !expanded }) {
            iconContent()
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.wrapContentSize(Alignment.TopEnd)
        ) {
            content { expanded = !expanded }
        }
    }
}