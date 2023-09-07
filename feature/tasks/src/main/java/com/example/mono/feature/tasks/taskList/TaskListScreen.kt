package com.example.mono.feature.tasks.taskList

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.mono.core.designsystem.component.MonoFloatingButton
import com.example.mono.core.designsystem.component.MonoModalNavigationDrawer
import com.example.mono.core.model.Task
import com.example.mono.core.model.TaskList
import com.example.mono.core.ui.tasks
import com.example.mono.feature.tasks.components.CreateTaskDialog
import com.example.mono.feature.tasks.components.TasksModalDrawerContent
import com.example.mono.feature.tasks.components.TasksTopAppBar
import com.example.mono.feature.tasks.tasks.TasksUiState
import kotlinx.coroutines.launch

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalAnimationApi::class
)
@Composable
fun TaskListRoute(
    navigateToTasks: () -> Unit,
    navigateToAddEditTaskList: () -> Unit,
    navigateToTaskList: (taskListId: String) -> Unit,
    onTaskClick: (Task) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TaskListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val selectedTaskList by viewModel.selectedTaskList.collectAsStateWithLifecycle(TaskList("", ""))

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    var showDialog by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    BackHandler(enabled = drawerState.isOpen) {
        scope.launch { drawerState.close() }
    }

    MonoModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            TasksModalDrawerContent(
                currentRoute = selectedTaskList.id,
                taskLists = uiState.taskLists,
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
                    title = { Text(text = selectedTaskList.name) },
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
            TaskListScreen(
                uiState = uiState,
                onTaskClick = onTaskClick,
                onCheckedChange = viewModel::completeTask,
                onToggleBookmark = viewModel::updateBookmarked,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                nestedScrollBehavior = scrollBehavior.nestedScrollConnection
            )
        }
    }

    if (showDialog) {
        CreateTaskDialog(
            onDismiss = { showDialog = false },
            onCreateTask = viewModel::createNewTask
        )
    }
}

@Composable
fun TaskListScreen(
    uiState: TasksUiState,
    onTaskClick: (Task) -> Unit,
    onCheckedChange: (Task, Boolean) -> Unit,
    onToggleBookmark: (Task, Boolean) -> Unit,
    modifier: Modifier = Modifier,
    nestedScrollBehavior: NestedScrollConnection
) {
    LazyColumn(
        modifier = modifier.nestedScroll(nestedScrollBehavior)
    ) {
        tasks(
            items = uiState.tasks,
            onCheckedChange = onCheckedChange,
            onTaskClick = onTaskClick,
            toggleBookmark = onToggleBookmark
        )
    }
}