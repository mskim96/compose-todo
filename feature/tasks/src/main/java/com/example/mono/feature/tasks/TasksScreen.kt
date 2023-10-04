package com.example.mono.feature.tasks

import android.os.Build
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.mono.core.designsystem.component.MonoDropdownMenu
import com.example.mono.core.designsystem.component.MonoFloatingButton
import com.example.mono.core.designsystem.component.MonoIconDropdownMenu
import com.example.mono.core.designsystem.component.MonoTopAppBar
import com.example.mono.core.designsystem.component.TasksEmptyContent
import com.example.mono.core.designsystem.icon.MonoIcons
import com.example.mono.core.model.Task
import com.example.mono.core.model.TaskCreationParams
import com.example.mono.core.model.TaskSortingType
import com.example.mono.core.ui.CreateTaskDialog
import com.example.mono.core.ui.tasks
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState

@Composable
internal fun TasksRoute(
    onTaskClick: (Task) -> Unit,
    openDrawer: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TasksViewModel = hiltViewModel()
) {
    val tasksUiState by viewModel.tasksUiState.collectAsStateWithLifecycle()

    TasksScreen(
        tasksUiState = tasksUiState,
        onTaskClick = onTaskClick,
        onCreateTask = viewModel::createNewTask,
        openDrawer = openDrawer,
        onSortNoneTasks = { viewModel.setSortedType(TaskSortingType.NONE) },
        onSortDateTasks = { viewModel.setSortedType(TaskSortingType.DATE) },
        onOrderChange = viewModel::updateOrdering,
        onCheckedChange = viewModel::updateComplete,
        onBookmarkChange = viewModel::updateBookmarked,
        onCompletedDelete = viewModel::clearCompletedTasks,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun TasksScreen(
    tasksUiState: TasksUiState,
    onTaskClick: (Task) -> Unit,
    onCreateTask: (TaskCreationParams) -> Unit,
    onSortNoneTasks: () -> Unit,
    onSortDateTasks: () -> Unit,
    onOrderChange: (Boolean) -> Unit,
    onCheckedChange: (Task, Boolean) -> Unit,
    onBookmarkChange: (Task, Boolean) -> Unit,
    onCompletedDelete: () -> Unit,
    openDrawer: () -> Unit,
    modifier: Modifier = Modifier
) {
    val topAppBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(topAppBarState)

    var showCreateTaskDialog by remember { mutableStateOf(false) }
    var showDeleteCompletedTaskDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier,
        topBar = {
            TasksTopAppBar(
                hasCompletedTask = tasksUiState.completedTasks.isNotEmpty(),
                openDrawer = openDrawer,
                onCompletedDelete = { showDeleteCompletedTaskDialog = true },
                scrollBehavior = scrollBehavior
            )
        },
        floatingActionButton = {
            AnimatedVisibility(
                visible = !showCreateTaskDialog,
                enter = scaleIn(tween(delayMillis = 300)),
                exit = scaleOut()
            ) {
                if (!showCreateTaskDialog) {
                    MonoFloatingButton(
                        icon = MonoIcons.Add,
                        onClick = { showCreateTaskDialog = true }
                    )
                }
            }
        }
    ) { padding ->
        if (!tasksUiState.isLoading && tasksUiState.empty) {
            TasksEmptyContent(
                noTaskIcon = MonoIcons.Tasks,
                noTasksLabel = R.string.no_tasks
            )
        } else {
            when (tasksUiState.selectedSortType) {
                TaskSortingType.NONE -> {
                    TasksContent(
                        activeTasks = tasksUiState.activeTasks,
                        completedTasks = tasksUiState.completedTasks,
                        isAscending = tasksUiState.isAscending,
                        onTaskClick = onTaskClick,
                        selectedSortType = tasksUiState.selectedSortType,
                        onSortNoneTasks = onSortNoneTasks,
                        onSortDateTasks = onSortDateTasks,
                        onOrderChanged = { onOrderChange(!tasksUiState.isAscending) },
                        onCheckedChange = onCheckedChange,
                        onBookmarkChange = onBookmarkChange,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                            .nestedScroll(scrollBehavior.nestedScrollConnection)
                    )
                }

                TaskSortingType.DATE -> {
                    TasksWithDateContent(
                        activeTasksWithDate = tasksUiState.activeTasksByDate,
                        completedTasks = tasksUiState.completedTasks,
                        isAscending = tasksUiState.isAscending,
                        selectedSortType = tasksUiState.selectedSortType,
                        onSortNoneTasks = onSortNoneTasks,
                        onSortDateTasks = onSortDateTasks,
                        onOrderChanged = { onOrderChange(!tasksUiState.isAscending) },
                        onTaskClick = onTaskClick,
                        onCheckedChange = onCheckedChange,
                        onToggleBookmark = onBookmarkChange,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                            .nestedScroll(scrollBehavior.nestedScrollConnection)
                    )
                }
            }
        }

        if (showCreateTaskDialog) {
            CreateTaskDialog(
                onDismiss = { showCreateTaskDialog = false },
                onCreateTask = onCreateTask,
            )
        }
        if (showDeleteCompletedTaskDialog) {
            DeleteCompletedTasksDialog(
                onDismiss = { showDeleteCompletedTaskDialog = false },
                onDeleteCompleted = onCompletedDelete
            )
        }
    }
    NotificationPermissionEffect()
}

@Composable
internal fun TasksContent(
    activeTasks: List<Task>,
    completedTasks: List<Task>,
    isAscending: Boolean,
    onTaskClick: (Task) -> Unit,
    selectedSortType: TaskSortingType,
    onSortNoneTasks: () -> Unit,
    onSortDateTasks: () -> Unit,
    onOrderChanged: () -> Unit,
    onCheckedChange: (Task, Boolean) -> Unit,
    onBookmarkChange: (Task, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    var expandCompletedTasks by rememberSaveable { mutableStateOf(true) }
    val rotateExpandIcon by animateFloatAsState(
        targetValue = if (expandCompletedTasks) 0f else -180f,
        label = "rotate expand icon"
    )

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        state = rememberLazyListState()
    ) {
        item {
            CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.outline) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(IntrinsicSize.Min)
                        .padding(bottom = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End
                ) {
                    TaskSortTypeMoreMenu(
                        selectedSortType = selectedSortType,
                        onSortNoneTasks = onSortNoneTasks,
                        onSortDateTasks = onSortDateTasks
                    )
                    Spacer(modifier = Modifier.size(16.dp))
                    Divider(
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(1.dp)
                            .padding(vertical = 8.dp)
                    )
                    Spacer(modifier = Modifier.size(16.dp))
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clickable(onClick = onOrderChanged)
                    ) {
                        Icon(
                            imageVector = if (!isAscending) MonoIcons.ArrowDown else MonoIcons.ArrowUp,
                            contentDescription = null,
                            modifier = Modifier
                                .size(20.dp)
                                .align(Alignment.Center)
                        )
                    }
                    Spacer(modifier = Modifier.size(16.dp))
                }
            }
        }

        tasks(
            items = activeTasks,
            onCheckedChange = onCheckedChange,
            onTaskClick = onTaskClick,
            toggleBookmark = onBookmarkChange
        )

        if (completedTasks.isNotEmpty()) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 48.dp)
                        .clickable { expandCompletedTasks = !expandCompletedTasks },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Completed", modifier = Modifier.padding(start = 20.dp))
                    Icon(
                        imageVector = MonoIcons.ExpandMore,
                        contentDescription = null,
                        modifier = Modifier
                            .padding(end = 20.dp)
                            .rotate(rotateExpandIcon)
                    )
                }
                Divider(modifier = Modifier.padding(bottom = 8.dp))
            }
        }

        if (expandCompletedTasks) {
            tasks(
                items = completedTasks,
                onCheckedChange = onCheckedChange,
                onTaskClick = onTaskClick,
                toggleBookmark = onBookmarkChange
            )
        }
    }
}

@Composable
internal fun TasksWithDateContent(
    activeTasksWithDate: Map<String, List<Task>>,
    completedTasks: List<Task>,
    isAscending: Boolean,
    selectedSortType: TaskSortingType,
    onSortNoneTasks: () -> Unit,
    onSortDateTasks: () -> Unit,
    onOrderChanged: () -> Unit,
    onTaskClick: (Task) -> Unit,
    onCheckedChange: (Task, Boolean) -> Unit,
    onToggleBookmark: (Task, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    var expandCompletedTasks by rememberSaveable { mutableStateOf(true) }
    val rotateExpandIcon by animateFloatAsState(
        targetValue = if (expandCompletedTasks) 0f else -180f,
        label = "rotate expand icon"
    )

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        state = rememberLazyListState()
    ) {
        item {
            CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.outline) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(IntrinsicSize.Min)
                        .padding(bottom = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End
                ) {
                    TaskSortTypeMoreMenu(
                        selectedSortType = selectedSortType,
                        onSortNoneTasks = onSortNoneTasks,
                        onSortDateTasks = onSortDateTasks
                    )
                    Spacer(modifier = Modifier.size(16.dp))
                    Divider(
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(1.dp)
                            .padding(vertical = 8.dp)
                    )
                    Spacer(modifier = Modifier.size(16.dp))
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clickable(onClick = onOrderChanged)
                    ) {
                        Icon(
                            imageVector = if (!isAscending) MonoIcons.ArrowDown else MonoIcons.ArrowUp,
                            contentDescription = null,
                            modifier = Modifier
                                .size(20.dp)
                                .align(Alignment.Center)
                        )
                    }
                    Spacer(modifier = Modifier.size(16.dp))
                }
            }
        }

        activeTasksWithDate.forEach { (date, tasks) ->
            item {
                if (tasks.isNotEmpty()) {
                    Text(
                        text = date,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            tasks(
                items = tasks,
                onCheckedChange = onCheckedChange,
                onTaskClick = onTaskClick,
                toggleBookmark = onToggleBookmark
            )
        }

        if (completedTasks.isNotEmpty()) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 48.dp)
                        .clickable { expandCompletedTasks = !expandCompletedTasks },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Completed", modifier = Modifier.padding(start = 20.dp))
                    Icon(
                        imageVector = MonoIcons.ExpandMore,
                        contentDescription = null,
                        modifier = Modifier
                            .padding(end = 20.dp)
                            .rotate(rotateExpandIcon)
                    )
                }
                Divider(modifier = Modifier.padding(bottom = 8.dp))
            }
        }

        if (expandCompletedTasks) {
            tasks(
                items = completedTasks,
                onCheckedChange = onCheckedChange,
                onTaskClick = onTaskClick,
                toggleBookmark = onToggleBookmark
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TasksTopAppBar(
    hasCompletedTask: Boolean,
    openDrawer: () -> Unit,
    onCompletedDelete: () -> Unit,
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior? = null
) {
    MonoTopAppBar(
        titleRes = R.string.tasks,
        modifier = modifier,
        navigationIcon = {
            IconButton(onClick = openDrawer) {
                Icon(
                    imageVector = MonoIcons.Menu,
                    contentDescription = "Menu"
                )
            }
        },
        actions = {
            TasksMoreMenu(
                hasCompletedTask = hasCompletedTask,
                onCompletedDelete = onCompletedDelete
            )
        },
        scrollBehavior = scrollBehavior
    )
}

@Composable
private fun TasksMoreMenu(
    hasCompletedTask: Boolean,
    onCompletedDelete: () -> Unit
) {
    MonoIconDropdownMenu(
        iconContent = {
            Icon(imageVector = MonoIcons.MoreVert, contentDescription = "More")
        }
    ) { closeMenu ->
        DropdownMenuItem(
            text = { Text("Delete all completed task") },
            onClick = { onCompletedDelete(); closeMenu() },
            enabled = hasCompletedTask
        )
    }
}

@Composable
fun TaskSortTypeMoreMenu(
    selectedSortType: TaskSortingType,
    onSortNoneTasks: () -> Unit,
    onSortDateTasks: () -> Unit
) {
    MonoDropdownMenu(
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
    ) { closeMenu ->
        DropdownMenuItem(
            text = {
                Text(text = TaskSortingType.NONE.sortedName)
            },
            onClick = { onSortNoneTasks(); closeMenu() },
            leadingIcon = {
                if (selectedSortType == TaskSortingType.NONE) {
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
                if (selectedSortType == TaskSortingType.DATE) {
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
fun DeleteCompletedTasksDialog(
    onDismiss: () -> Unit,
    onDeleteCompleted: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = { onDeleteCompleted(); onDismiss() }) {
                Text(text = "Delete")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "Cancel")
            }
        },
        title = {
            Text(text = "Delete all completed tasks?")
        },
        text = {
            Text(text = "Completed tasks will be permanently deleted from this list unless the repeat.")
        }
    )
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun NotificationPermissionEffect() {
    // Permission requests should only be made from an Activity Context, which is not present
    // in previews.
    if (LocalInspectionMode.current) return
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return
    val notificationsPermissionState = rememberPermissionState(
        android.Manifest.permission.POST_NOTIFICATIONS,
    )
    LaunchedEffect(notificationsPermissionState) {
        val status = notificationsPermissionState.status
        if (status is PermissionStatus.Denied && !status.shouldShowRationale) {
            notificationsPermissionState.launchPermissionRequest()
        }
    }
}