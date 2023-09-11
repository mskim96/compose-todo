package com.example.mono.feature.tasks.tasks

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.mono.core.designsystem.component.MonoFloatingButton
import com.example.mono.core.designsystem.component.MonoModalNavigationDrawer
import com.example.mono.core.designsystem.theme.MonoTheme
import com.example.mono.core.model.Task
import com.example.mono.core.ui.tasks
import com.example.mono.feature.tasks.R
import com.example.mono.feature.tasks.components.CreateTaskDialog
import com.example.mono.feature.tasks.components.TasksEmptyContent
import com.example.mono.feature.tasks.components.TasksModalDrawerContent
import com.example.mono.feature.tasks.components.TasksTopAppBar
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime

@Composable
internal fun TasksRoute(
    currentRoute: String,
    navigateToBookmarkTasks: () -> Unit,
    navigateToAddEditTaskList: () -> Unit,
    navigateToTaskList: (taskListId: String) -> Unit,
    onTaskClick: (Task) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TasksViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    BackHandler(enabled = drawerState.isOpen) {
        scope.launch { drawerState.close() }
    }

    MonoModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            TasksModalDrawerContent(
                currentRoute = currentRoute,
                taskLists = uiState.taskLists,
                navigateToTasks = {},
                navigateToBookmarks = navigateToBookmarkTasks,
                navigateToAddEditTaskList = navigateToAddEditTaskList,
                navigateToTaskList = navigateToTaskList,
                onDrawerClicked = { scope.launch { drawerState.close() } }
            )
        }
    ) {
        TasksScreen(
            uiState = uiState,
            openDrawer = { scope.launch { drawerState.open() } },
            onTaskClick = onTaskClick,
            onCreateTask = viewModel::createNewTask,
            onCheckedChange = viewModel::completeTask,
            onToggleBookmark = viewModel::updateBookmarked,
            modifier = modifier
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun TasksScreen(
    uiState: TasksUiState,
    openDrawer: () -> Unit,
    onTaskClick: (Task) -> Unit,
    onCreateTask: (title: String, description: String, isBookmarked: Boolean, date: LocalDate?, time: LocalTime?) -> Unit,
    onCheckedChange: (Task, Boolean) -> Unit,
    onToggleBookmark: (Task, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        CreateTaskDialog(
            onDismiss = { showDialog = false },
            onCreateTask = onCreateTask
        )
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TasksTopAppBar(
                title = { Text(text = "All tasks") },
                openDrawer = openDrawer,
                navigateToSearch = {},
                scrollBehavior = scrollBehavior
            )
        },
        floatingActionButton = {
            AnimatedVisibility(
                visible = !showDialog,
                enter = scaleIn(tween(delayMillis = 300)),
                exit = scaleOut()
            ) {
                MonoFloatingButton(
                    icon = Icons.Default.Add,
                    onClick = { showDialog = true }
                )
            }
        }
    ) { padding ->
        if (!uiState.isLoading && uiState.tasks.isEmpty()) {
            TasksEmptyContent(
                noTaskIcon = Icons.Outlined.Book,
                noTasksLabel = R.string.no_tasks
            )
        } else {
            TasksContent(
                tasks = uiState.tasks,
                onTaskClick = onTaskClick,
                onCheckedChange = onCheckedChange,
                onToggleBookmark = onToggleBookmark,
                nestedScrollBehavior = scrollBehavior.nestedScrollConnection,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            )
        }
    }
}

@Composable
internal fun TasksContent(
    tasks: List<Task>,
    onTaskClick: (Task) -> Unit,
    onCheckedChange: (Task, Boolean) -> Unit,
    onToggleBookmark: (Task, Boolean) -> Unit,
    modifier: Modifier = Modifier,
    nestedScrollBehavior: NestedScrollConnection
) {
    val (completedTasks, activeTasks) = tasks.partition(Task::isCompleted)
    var expandCompletedTasks by rememberSaveable { mutableStateOf(true) }
    val rotateExpandIcon by animateFloatAsState(
        targetValue = if (expandCompletedTasks) 0f else -180f,
        label = "rotate expand icon"
    )

    LazyColumn(
        modifier = modifier
            .padding(top = 12.dp)
            .nestedScroll(nestedScrollBehavior),
        state = rememberLazyListState()
    ) {
        tasks(
            items = activeTasks,
            onCheckedChange = onCheckedChange,
            onTaskClick = onTaskClick,
            toggleBookmark = onToggleBookmark
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
                        imageVector = Icons.Default.ExpandMore,
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

@Preview("Tasks Screen")
@Composable
private fun TasksScreenPreview() {
    MonoTheme {
        TasksScreen(
            uiState = TasksUiState(
                tasks = listOf(
                    Task(
                        "1",
                        "1",
                        "Task title preview",
                        "description"
                    )
                )
            ),
            openDrawer = {},
            onCreateTask = { _, _, _, _, _ -> },
            onTaskClick = {},
            onCheckedChange = { _, _ -> },
            onToggleBookmark = { _, _ -> }
        )
    }
}