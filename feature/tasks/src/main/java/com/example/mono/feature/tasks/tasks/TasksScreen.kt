package com.example.mono.feature.tasks.tasks

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.BottomAppBar
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.mono.core.designsystem.component.MonoFloatingButton
import com.example.mono.core.designsystem.component.MonoModalNavigationDrawer
import com.example.mono.core.model.Task
import com.example.mono.core.ui.TaskItem
import com.example.mono.core.ui.tasks
import com.example.mono.feature.tasks.components.CreateTaskDialog
import com.example.mono.feature.tasks.components.IconRow
import com.example.mono.feature.tasks.components.TasksModalDrawerContent
import com.example.mono.feature.tasks.components.TasksTopAppBar
import kotlinx.coroutines.launch

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalAnimationApi::class
)
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
        Scaffold(
            modifier = modifier,
            topBar = {
                TasksTopAppBar(
                    title = { Text(text = "All tasks") },
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
            TasksScreen(
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun TasksScreen(
    uiState: TasksUiState,
    onTaskClick: (Task) -> Unit,
    onCheckedChange: (Task, Boolean) -> Unit,
    onToggleBookmark: (Task, Boolean) -> Unit,
    modifier: Modifier = Modifier,
    nestedScrollBehavior: NestedScrollConnection
) {
    var isExpand by remember { mutableStateOf(true) }
    val (activeTasks, completedTasks) = uiState.tasks.partition { !it.isCompleted }

    LazyColumn(
        modifier = modifier.nestedScroll(nestedScrollBehavior),
        state = rememberLazyListState()
    ) {
        tasks(
            items = activeTasks,
            onCheckedChange = onCheckedChange,
            onTaskClick = onTaskClick,
            toggleBookmark = onToggleBookmark
        )

        item {
            if (completedTasks.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .clickable { isExpand = !isExpand },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Completed tasks")
                    Icon(
                        imageVector = if (isExpand) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = null
                    )
                }
            }
        }

        items(
            completedTasks,
            key = { it.id }
        ) { task ->
            AnimatedVisibility(
                isExpand,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                TaskItem(
                    task = task,
                    onCheckedChange = { onCheckedChange(task, it) },
                    onTaskClick = onTaskClick,
                    toggleBookmark = { onToggleBookmark(task, it) }
                )
            }
        }
    }
}