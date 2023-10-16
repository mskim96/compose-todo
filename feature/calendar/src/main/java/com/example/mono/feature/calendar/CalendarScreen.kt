package com.example.mono.feature.calendar

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Today
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.mono.core.designsystem.component.MonoFloatingButton
import com.example.mono.core.designsystem.component.MonoTopAppBar
import com.example.mono.core.designsystem.component.TasksEmptyContent
import com.example.mono.core.designsystem.icon.MonoIcons
import com.example.mono.core.model.task.Task
import com.example.mono.core.model.task.TaskCreationParams
import com.example.mono.core.ui.CreateTaskDialog
import com.example.mono.core.ui.tasks
import com.example.mono.feature.calendar.component.Calendar
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
internal fun CalendarRoute(
    onTaskClick: (Task) -> Unit,
    openDrawer: () -> Unit,
    viewModel: CalendarViewModel = hiltViewModel()
) {
    val calendarUiState by viewModel.calendarUiState.collectAsStateWithLifecycle()

    CalendarScreen(
        state = calendarUiState,
        onDateSelected = viewModel::updateSelectedDate,
        onCreateTask = viewModel::createNewTask,
        openDrawer = openDrawer,
        onTaskClick = onTaskClick,
        onCheckedChange = viewModel::updateComplete,
        onBookmarkChange = viewModel::updateBookmarked,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun CalendarScreen(
    state: CalendarUiState,
    onDateSelected: (LocalDate) -> Unit,
    openDrawer: () -> Unit,
    onCreateTask: (TaskCreationParams) -> Unit,
    onTaskClick: (Task) -> Unit,
    onCheckedChange: (Task, Boolean) -> Unit,
    onBookmarkChange: (Task, Boolean) -> Unit,
    calendarState: CalendarState = rememberCalendarState(),
) {
    val coroutineScope = rememberCoroutineScope()
    var expandCalendar by remember { mutableStateOf(true) }
    var showCreateTaskDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CalendarTopAppBar(
                monthTitle = if (calendarState.firstVisibleMonth.yearMonth.year == LocalDate.now().year) {
                    val format = DateTimeFormatter.ofPattern("MMMM")
                    calendarState.firstVisibleMonth.yearMonth.format(format)
                } else {
                    val format = DateTimeFormatter.ofPattern("MMMM yyyy")
                    calendarState.firstVisibleMonth.yearMonth.format(format)
                },
                isCalendarExpanded = expandCalendar,
                openDrawer = openDrawer,
                expandCalendar = { expandCalendar = !expandCalendar },
                navigateToToday = {
                    coroutineScope.launch {
                        calendarState.animateScrollToMonth(LocalDate.now())
                    }
                }
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
        Column(modifier = Modifier.padding(padding)) {
            AnimatedVisibility(
                visible = expandCalendar,
            ) {
                Calendar(
                    calendarUiState = state,
                    onDateSelected = onDateSelected,
                    modifier = Modifier.background(
                        MaterialTheme.colorScheme.primaryContainer.copy(
                            alpha = 0.4f
                        )
                    ),
                    calendarState = calendarState
                )
            }
            if (expandCalendar) {
                Divider(Modifier.padding(bottom = 12.dp))
            }
            if (!state.isLoading && state.tasks.isEmpty()) {
                TasksEmptyContent(
                    noTaskIcon = MonoIcons.Tasks,
                    noTasksLabel = R.string.no_tasks
                )
            } else {
                CalendarContent(
                    tasks = state.tasks,
                    onTaskClick = onTaskClick,
                    onCheckedChange = onCheckedChange,
                    onBookmarkChange = onBookmarkChange
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
}

@Composable
fun CalendarContent(
    tasks: List<Task>,
    onTaskClick: (Task) -> Unit,
    onCheckedChange: (Task, Boolean) -> Unit,
    onBookmarkChange: (Task, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        state = rememberLazyListState()
    ) {
        tasks(
            items = tasks,
            onCheckedChange = onCheckedChange,
            onTaskClick = onTaskClick,
            toggleBookmark = onBookmarkChange
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CalendarTopAppBar(
    monthTitle: String,
    isCalendarExpanded: Boolean,
    expandCalendar: () -> Unit,
    openDrawer: () -> Unit,
    navigateToToday: () -> Unit,
    modifier: Modifier = Modifier
) {
    val rotateExpandIcon by animateFloatAsState(
        targetValue = if (isCalendarExpanded) 0f else 180f,
        label = "Expand calendar icon"
    )
    val expandTopAppBarColor by animateColorAsState(
        targetValue = if (isCalendarExpanded) MaterialTheme.colorScheme.primaryContainer.copy(
            alpha = 0.4f
        ) else MaterialTheme.colorScheme.surface,
        animationSpec = tween(durationMillis = 400, delayMillis = 400),
        label = "Expand top app bar color"
    )
    MonoTopAppBar(
        title = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .clickable {
                        expandCalendar()
                    },
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(text = monthTitle)
                Icon(
                    imageVector = MonoIcons.ExpandLess,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(top = 2.dp)
                        .rotate(rotateExpandIcon)
                )
            }
        },
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
            IconButton(onClick = {}) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search"
                )
            }
            IconButton(onClick = navigateToToday) {
                Icon(
                    imageVector = Icons.Outlined.Today,
                    contentDescription = "Navigate to today"
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = expandTopAppBarColor
        )
    )
}