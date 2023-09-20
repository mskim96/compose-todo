package com.example.mono.feature.tasks.bookmarks

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.outlined.Bookmarks
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.mono.core.designsystem.component.MonoFloatingButton
import com.example.mono.core.designsystem.component.MonoModalNavigationDrawer
import com.example.mono.core.model.Task
import com.example.mono.core.model.TaskSortingType
import com.example.mono.core.ui.tasks
import com.example.mono.feature.tasks.R
import com.example.mono.feature.tasks.components.CreateTaskDialog
import com.example.mono.feature.tasks.components.TaskSortTypeMoreMenu
import com.example.mono.feature.tasks.components.TasksEmptyContent
import com.example.mono.feature.tasks.components.TasksModalDrawerContent
import com.example.mono.feature.tasks.components.TasksTopAppBar
import com.example.mono.feature.tasks.taskDetail.TaskListUiState
import kotlinx.coroutines.launch

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalAnimationApi::class
)
@Composable
fun BookmarksRoute(
    currentRoute: String,
    navigateToTasks: () -> Unit,
    navigateToAddEditTaskList: () -> Unit,
    navigateToTaskList: (taskListId: String) -> Unit,
    onTaskClick: (Task) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: BookmarksViewModel = hiltViewModel()
) {
    val uiState by viewModel.tasksUiState.collectAsStateWithLifecycle()
    val taskListsUiState by viewModel.taskListsUiState.collectAsStateWithLifecycle()

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    var showDialog by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    MonoModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            TasksModalDrawerContent(
                currentRoute = currentRoute,
                taskLists = when (taskListsUiState) {
                    TaskListUiState.Loading -> emptyList()
                    is TaskListUiState.Success -> (taskListsUiState as TaskListUiState.Success).taskLists
                },
                navigateToTasks = navigateToTasks,
                navigateToBookmarks = {},
                navigateToAddEditTaskList = navigateToAddEditTaskList,
                navigateToTaskList = navigateToTaskList,
                onDrawerClicked = { scope.launch { drawerState.close() } }
            )
        }
    ) {
        Scaffold(
            modifier = modifier,
            topBar = {
                TasksTopAppBar(
                    title = { Text(text = "Bookmarks") },
                    openDrawer = { scope.launch { drawerState.open() } },
                    navigateToSearch = {},
                    scrollBehavior = scrollBehavior
                )
            },
            bottomBar = {
                AnimatedVisibility(
                    visible = !showDialog,
                    enter = slideInVertically(tween(delayMillis = 200)) { it },
                    exit = slideOutVertically()
                ) {
                    BottomAppBar(
                        actions = { },
                        floatingActionButton = {
                            MonoFloatingButton(
                                icon = Icons.Default.Add,
                                onClick = { showDialog = true },
                                modifier = Modifier.animateEnterExit(
                                    enter = scaleIn(tween(delayMillis = 300)),
                                    exit = scaleOut()
                                )
                            )
                        }
                    )
                }
            }
        ) { padding ->
            if (!uiState.isLoading && uiState.activeTasks.isEmpty() && uiState.completedTasks.isEmpty() && uiState.activeTasksByDate.isEmpty()) {
                TasksEmptyContent(
                    noTaskIcon = Icons.Outlined.Bookmarks,
                    noTasksLabel = R.string.no_tasks
                )
            } else {
                when (uiState.selectedSortType) {
                    TaskSortingType.NONE -> {
                        BookmarksScreen(
                            activeTasks = uiState.activeTasks,
                            completedTasks = uiState.completedTasks,
                            isAscending = uiState.isAscending,
                            selectedSortType = uiState.selectedSortType,
                            onSortNoneTasks = { viewModel.setSortedType(TaskSortingType.NONE) },
                            onSortDateTasks = { viewModel.setSortedType(TaskSortingType.DATE) },
                            onOrderChanged = { viewModel.updateOrdering(!uiState.isAscending) },
                            onTaskClick = onTaskClick,
                            onCheckedChange = viewModel::completeTask,
                            onToggleBookmark = viewModel::updateBookmarked,
                            modifier = Modifier
                                .nestedScroll(scrollBehavior.nestedScrollConnection)
                                .padding(padding)
                        )
                    }

                    TaskSortingType.DATE -> {
                        BookmarksWithDateContent(
                            activeTasksWithDate = uiState.activeTasksByDate,
                            completedTasks = uiState.completedTasks,
                            isAscending = uiState.isAscending,
                            selectedSortType = uiState.selectedSortType,
                            onSortNoneTasks = { viewModel.setSortedType(TaskSortingType.NONE) },
                            onSortDateTasks = { viewModel.setSortedType(TaskSortingType.DATE) },
                            onOrderChanged = { viewModel.updateOrdering(!uiState.isAscending) },
                            onTaskClick = onTaskClick,
                            onCheckedChange = viewModel::completeTask,
                            onToggleBookmark = viewModel::updateBookmarked,
                            modifier = Modifier
                                .nestedScroll(scrollBehavior.nestedScrollConnection)
                                .padding(padding)
                        )
                    }
                }
            }
        }

        if (showDialog) {
            CreateTaskDialog(
                onDismiss = { showDialog = false },
                onCreateTask = viewModel::createNewTask
            )
        }

        BackHandler(enabled = drawerState.isOpen) {
            scope.launch { drawerState.close() }
        }
    }
}

@Composable
fun BookmarksScreen(
    activeTasks: List<Task>,
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
                            .clickable { onOrderChanged() }
                    ) {
                        Icon(
                            imageVector = if (!isAscending) Icons.Default.ArrowDownward else Icons.Default.ArrowUpward,
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

@Composable
fun BookmarksWithDateContent(
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
                            .clickable { onOrderChanged() }
                    ) {
                        Icon(
                            imageVector = if (!isAscending) Icons.Default.ArrowDownward else Icons.Default.ArrowUpward,
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