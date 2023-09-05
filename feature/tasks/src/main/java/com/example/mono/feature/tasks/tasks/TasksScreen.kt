package com.example.mono.feature.tasks.tasks

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.mono.core.designsystem.component.MonoFloatingButton
import com.example.mono.core.designsystem.component.MonoModalNavigationDrawer
import com.example.mono.core.model.Task
import com.example.mono.core.ui.taskList
import com.example.mono.feature.tasks.components.TaskModalDrawerContent
import com.example.mono.feature.tasks.components.TasksTopAppBar
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime

@Composable
internal fun TasksRoute(
    navigateToTaskGroup: (groupId: String) -> Unit,
    navigateToAddEditTaskGroup: () -> Unit,
    onTaskClick: (Task) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TasksViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val taskGroups by viewModel.taskGroups.collectAsStateWithLifecycle(emptyList())
    val taskGroupId by viewModel.taskGroupId.collectAsStateWithLifecycle()
    val taskGroupName = viewModel.builtInTaskGroups.firstOrNull {
        it.groupId == taskGroupId
    }?.groupName ?: taskGroups.firstOrNull {
        it.id == taskGroupId
    }?.name ?: ""

    val snackbarState = remember { SnackbarHostState() }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    val scope = rememberCoroutineScope()

    BackHandler(enabled = drawerState.isOpen) {
        scope.launch { drawerState.close() }
    }

    MonoModalNavigationDrawer(
        drawerContent = {
            TaskModalDrawerContent(
                selectedGroupId = taskGroupId,
                builtInTaskGroups = viewModel.builtInTaskGroups,
                taskGroups = taskGroups,
                navigateToTaskGroup = navigateToTaskGroup,
                navigateToAddEditTaskGroup = navigateToAddEditTaskGroup,
                onDrawerClicked = { scope.launch { drawerState.close() } }
            )
        },
        drawerState = drawerState
    ) {
        TasksScreen(
            taskGroupId = taskGroupId,
            topAppBarTitle = taskGroupName,
            drawerState = drawerState,
            uiState = uiState,
            snackbarState = snackbarState,
            onCreateTask = viewModel::createNewTask,
            onTaskClick = onTaskClick,
            onCheckedChange = viewModel::completeTask,
            onToggleBookmark = viewModel::updateBookmarked,
            modifier = modifier
        )
    }
}

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalAnimationApi::class
)
@Composable
internal fun TasksScreen(
    taskGroupId: String,
    topAppBarTitle: String,
    drawerState: DrawerState,
    uiState: TasksUiState,
    snackbarState: SnackbarHostState,
    onCreateTask: (title: String, description: String, isBookmarked: Boolean, date: LocalDate?, time: LocalTime?, groupId: String) -> Unit,
    onTaskClick: (Task) -> Unit,
    onCheckedChange: (Task, Boolean) -> Unit,
    onToggleBookmark: (Task, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    var showDialog by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TasksTopAppBar(
                title = { Text(text = topAppBarTitle) },
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
        },
        snackbarHost = { SnackbarHost(snackbarState) }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            when (uiState) {
                TasksUiState.Loading -> Unit
                TasksUiState.Empty -> Unit
                is TasksUiState.Tasks -> TasksContent(
                    activeTasks = uiState.activeTasks,
                    completedTasks = uiState.completedTasks,
                    onTaskClick = onTaskClick,
                    onCheckedChange = onCheckedChange,
                    onToggleBookmark = onToggleBookmark,
                    nestedScrollBehavior = scrollBehavior.nestedScrollConnection
                )
            }
        }
        if (showDialog) {
            CreateTaskDialog(
                taskGroupId = taskGroupId,
                onDismiss = { showDialog = false },
                onCreateTask = onCreateTask
            )
        }
    }
}

@Composable
internal fun TasksContent(
    activeTasks: List<Task>,
    completedTasks: List<Task>,
    onTaskClick: (Task) -> Unit,
    onCheckedChange: (Task, Boolean) -> Unit,
    onToggleBookmark: (Task, Boolean) -> Unit,
    modifier: Modifier = Modifier,
    nestedScrollBehavior: NestedScrollConnection
) {
    var expandCompleted by rememberSaveable { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .nestedScroll(nestedScrollBehavior)
    ) {
        taskList(
            items = activeTasks,
            onCheckedChange = onCheckedChange,
            onTaskClick = onTaskClick,
            toggleBookmark = onToggleBookmark
        )
        if (completedTasks.isNotEmpty()) {
            item {
                Divider()
                Row(
                    modifier = Modifier
                        .clickable { expandCompleted = !expandCompleted }
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "Completed(${completedTasks.size})",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    AnimatedContent(targetState = expandCompleted, label = "") {
                        if (it) {
                            Icon(
                                imageVector = Icons.Default.ExpandLess,
                                contentDescription = null
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.ExpandMore,
                                contentDescription = null
                            )
                        }
                    }
                }
            }
            if (expandCompleted) {
                taskList(
                    items = completedTasks,
                    onCheckedChange = onCheckedChange,
                    onTaskClick = onTaskClick,
                    toggleBookmark = onToggleBookmark
                )
            }
        }
    }
}