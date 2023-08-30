package com.example.mono.feature.tasks.tasks

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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.mono.core.designsystem.component.MonoFloatingButton
import com.example.mono.core.model.Task
import com.example.mono.core.ui.taskList
import com.example.mono.feature.tasks.components.TasksTopAppBar
import java.time.LocalDate
import java.time.LocalTime

@Composable
internal fun TasksRoute(
    onTaskClick: (Task) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TasksViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val message by viewModel.userMessage.collectAsStateWithLifecycle()
    val snackbarState = remember { SnackbarHostState() }

    TasksScreen(
        uiState = uiState,
        snackbarState = snackbarState,
        onCreateTask = viewModel::createNewTask,
        onTaskClick = onTaskClick,
        onCheckedChange = viewModel::completeTask,
        onToggleBookmark = viewModel::updateBookmarked,
        modifier = modifier
    )

    message?.let {
        val snackbarText = stringResource(id = it)
        LaunchedEffect(key1 = it, snackbarText) {
            snackbarState.showSnackbar(snackbarText)
            viewModel.snackBarMessageShown()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
internal fun TasksScreen(
    uiState: TasksUiState,
    snackbarState: SnackbarHostState,
    onCreateTask: (title: String, description: String, isBookmarked: Boolean, date: LocalDate?, time: LocalTime?) -> Unit,
    onTaskClick: (Task) -> Unit,
    onCheckedChange: (Task, Boolean) -> Unit,
    onToggleBookmark: (Task, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TasksTopAppBar(
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
                TasksUiState.Empty -> EmptyTasksContent()
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
    Divider()
    LazyColumn(modifier = modifier.nestedScroll(nestedScrollBehavior)) {
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

@Composable
internal fun EmptyTasksContent(
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {

    }
}