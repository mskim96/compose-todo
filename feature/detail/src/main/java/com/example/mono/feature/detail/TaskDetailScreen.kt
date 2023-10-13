package com.example.mono.feature.detail

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.SubdirectoryArrowRight
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Label
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.mono.core.designsystem.component.MonoIconDropdownMenu
import com.example.mono.core.designsystem.component.MonoNoPaddingTextField
import com.example.mono.core.designsystem.component.MonoTextField
import com.example.mono.core.designsystem.component.MonoTopAppBar
import com.example.mono.core.designsystem.icon.MonoIcons
import com.example.mono.core.model.task.SubTask
import com.example.mono.core.model.task.TaskColorPalette
import com.example.mono.core.model.task.TaskList
import com.example.mono.core.ui.IconRowItem
import com.example.mono.core.ui.MonoDateTimePicker
import com.example.mono.core.ui.TaskDateTimeChip
import java.time.LocalDate
import java.time.LocalTime

@OptIn(ExperimentalComposeUiApi::class)
@Composable
internal fun TaskDetailRoute(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TaskDetailViewModel = hiltViewModel()
) {
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    val savedUpdateBackClick = {
        focusManager.clearFocus()
        keyboardController?.hide()
        viewModel.saveTask(onBackClick)
    }

    val taskDetailUiState by viewModel.taskUiState.collectAsStateWithLifecycle()
    val subTaskUiState by viewModel.subTaskUiState.collectAsStateWithLifecycle()
    val taskListUiState by viewModel.taskListUiState.collectAsStateWithLifecycle()

    TaskDetailScreen(
        taskDetailUiState = taskDetailUiState,
        subTaskUiState = subTaskUiState,
        taskListUiState = taskListUiState,
        onBackClick = savedUpdateBackClick,
        onDeleteTask = viewModel::deleteTask,
        onTitleChanged = viewModel::updateTitle,
        onDetailChanged = viewModel::updateDetail,
        onDateTimeSelected = viewModel::updateDateTime,
        onTaskColorChanged = viewModel::updateTaskColor,
        onTaskCompletedChanged = viewModel::updateTaskCompleted,
        onTaskBookmarkChanged = viewModel::updateTaskBookmark,
        onTaskListSelected = viewModel::updateTaskList,
        onCreateSubTask = viewModel::createSubTask,
        onSubTaskTitleChanged = viewModel::editSubTask,
        onSubTaskCompletedChanged = viewModel::updateSubTaskComplete,
        onDeleteSubTask = viewModel::deleteSubTask,
        modifier = modifier
    )

    BackHandler {
        savedUpdateBackClick()
    }

    LaunchedEffect(taskDetailUiState.isTaskSaved, taskDetailUiState.isTaskDeleted) {
        when {
            taskDetailUiState.isTaskSaved -> savedUpdateBackClick()
            taskDetailUiState.isTaskDeleted -> onBackClick()
        }
    }
}

@Composable
internal fun TaskDetailScreen(
    taskDetailUiState: TaskDetailUiState,
    subTaskUiState: SubTaskUiState,
    taskListUiState: TaskListUiState,
    onBackClick: () -> Unit,
    onDeleteTask: () -> Unit,
    onTitleChanged: (title: String) -> Unit,
    onDetailChanged: (detail: String) -> Unit,
    onDateTimeSelected: (date: LocalDate?, time: LocalTime?) -> Unit,
    onTaskColorChanged: (colorCode: Long) -> Unit,
    onTaskCompletedChanged: (Boolean) -> Unit,
    onTaskBookmarkChanged: (Boolean) -> Unit,
    onTaskListSelected: (TaskList?) -> Unit,
    onCreateSubTask: () -> Unit,
    onSubTaskCompletedChanged: (subTask: SubTask, isCompleted: Boolean) -> Unit,
    onSubTaskTitleChanged: (subTaskId: String, title: String) -> Unit,
    onDeleteSubTask: (subtaskId: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showDeleteTaskDialog by remember { mutableStateOf(false) }
    var showTaskListDialog by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TaskDetailTopAppBar(
                isBookmarked = taskDetailUiState.isTaskBookmarked,
                onBackClick = onBackClick,
                onBookmarkChange = { onTaskBookmarkChanged(!taskDetailUiState.isTaskBookmarked) },
                onDeleteTask = {
                    if (subTaskUiState is SubTaskUiState.Success) {
                        if (subTaskUiState.subTasks.isNotEmpty()) showDeleteTaskDialog =
                            true else onDeleteTask()
                    }
                }
            )
        },
        bottomBar = {
            AnimatedVisibility(
                visible = !showTaskListDialog,
                enter = slideInVertically { it },
                exit = slideOutVertically { it }
            ) {
                BottomAppBar(
                    windowInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Bottom)
                ) {
                    Spacer(modifier = Modifier.weight(1f))
                    TextButton(onClick = { onTaskCompletedChanged(!taskDetailUiState.isTaskCompleted) }) {
                        Text(
                            text = if (taskDetailUiState.isTaskCompleted) {
                                stringResource(id = R.string.task_mark_activate)
                            } else {
                                stringResource(id = R.string.task_mark_completed)
                            },
                            style = MaterialTheme.typography.titleSmall,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                }
            }
        }
    ) { padding ->
        TaskDetailContent(
            taskDetailUiState = taskDetailUiState,
            subTaskUiState = subTaskUiState,
            taskListUiState = taskListUiState,
            onTitleChanged = onTitleChanged,
            onDetailChanged = onDetailChanged,
            onDateTimeSelected = onDateTimeSelected,
            onTaskColorChanged = onTaskColorChanged,
            showTaskListDialog = { showTaskListDialog = true },
            onCreateSubTask = onCreateSubTask,
            onSubTaskCompletedChanged = onSubTaskCompletedChanged,
            onSubTaskTitleChanged = onSubTaskTitleChanged,
            onDeleteSubTask = onDeleteSubTask,
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
        )
    }

    if (showDeleteTaskDialog) {
        DeleteTaskDialog(
            onDismiss = { showDeleteTaskDialog = false },
            onDeleteTask = onDeleteTask
        )
    }
    if (showTaskListDialog) {
        SelectTaskListDialog(
            currentTaskList = taskDetailUiState.taskList,
            taskListUiState = taskListUiState,
            onDismiss = { showTaskListDialog = false },
            onConfirm = onTaskListSelected
        )
    }
}

@Composable
internal fun TaskDetailContent(
    taskDetailUiState: TaskDetailUiState,
    subTaskUiState: SubTaskUiState,
    taskListUiState: TaskListUiState,
    onTitleChanged: (title: String) -> Unit,
    onDetailChanged: (description: String) -> Unit,
    onDateTimeSelected: (date: LocalDate?, time: LocalTime?) -> Unit,
    onTaskColorChanged: (colorCode: Long) -> Unit,
    showTaskListDialog: () -> Unit,
    onCreateSubTask: () -> Unit,
    onSubTaskCompletedChanged: (subTask: SubTask, isCompleted: Boolean) -> Unit,
    onSubTaskTitleChanged: (subTaskId: String, title: String) -> Unit,
    onDeleteSubTask: (subTaskId: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showDateDialog by rememberSaveable { mutableStateOf(false) }

    if (showDateDialog) {
        MonoDateTimePicker(
            previousDate = taskDetailUiState.date,
            previousTime = taskDetailUiState.time,
            onDismiss = { showDateDialog = false },
            onConfirm = onDateTimeSelected
        )
    }

    LazyColumn(
        modifier = modifier,
        state = rememberLazyListState()
    ) {
        selectTaskListButton(
            selectedTaskList = taskDetailUiState.taskList,
            taskListUiState = taskListUiState,
            openTaskListDialog = showTaskListDialog
        )
        taskTitle(
            title = taskDetailUiState.title,
            onTitleChanged = onTitleChanged,
            modifier = Modifier.fillMaxWidth()
        )
        taskDetail(
            detail = taskDetailUiState.detail,
            onDetailChanged = onDetailChanged,
            modifier = Modifier.fillMaxWidth()
        )
        taskDateTime(
            date = taskDetailUiState.date,
            time = taskDetailUiState.time,
            openDateTimeDialog = { showDateDialog = true },
            onDeleteDateTimeSelected = { onDateTimeSelected(null, null) }
        )
        item {
            IconRowItem(
                iconContent = {
                    Icon(imageVector = Icons.Outlined.Label, contentDescription = null)
                },
                onClick = {},
                onClickEnabled = false,
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp)
            ) {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 40.dp)
                        .padding(start = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    items(TaskColorPalette.values()) {
                        val selected = it.color == taskDetailUiState.color
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(color = Color(it.color))
                                .clickable {
                                    onTaskColorChanged(it.color)
                                }
                        ) {
                            if (selected) {
                                Icon(
                                    imageVector = Icons.Outlined.Check,
                                    contentDescription = null,
                                    modifier = Modifier.align(Alignment.Center),
                                    tint = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }
        addEditSubTask(
            subTaskUiState = subTaskUiState,
            onCreateSubTask = onCreateSubTask,
            onSubTaskCompletedChanged = onSubTaskCompletedChanged,
            onSubTaskTitleChanged = onSubTaskTitleChanged,
            onDeleteSubTask = onDeleteSubTask
        )
    }
}

fun LazyListScope.selectTaskListButton(
    selectedTaskList: TaskList?,
    taskListUiState: TaskListUiState,
    openTaskListDialog: () -> Unit,
    modifier: Modifier = Modifier
) = item {
    TextButton(
        onClick = openTaskListDialog,
        modifier = modifier.padding(start = 4.dp),
        enabled = if (taskListUiState is TaskListUiState.Success) {
            taskListUiState.taskLists.isNotEmpty()
        } else {
            false
        }
    ) {
        Text(text = selectedTaskList?.name ?: stringResource(id = R.string.no_task_list))
        Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = null)
    }
}

fun LazyListScope.taskTitle(
    title: String,
    onTitleChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) = item {
    MonoTextField(
        value = title,
        onValueChange = onTitleChanged,
        modifier = modifier,
        textStyle = MaterialTheme.typography.headlineSmall,
        placeholder = { Text(text = stringResource(id = R.string.placeholder_title)) }
    )
}

fun LazyListScope.taskDetail(
    detail: String,
    onDetailChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) = item {
    IconRowItem(
        iconContent = { Icon(imageVector = MonoIcons.Sort, contentDescription = null) },
        onClick = {},
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        MonoTextField(
            value = detail,
            onValueChange = onDetailChanged,
            modifier = modifier,
            placeholder = { Text(text = stringResource(id = R.string.placeholder_description)) }
        )
    }
}

fun LazyListScope.taskDateTime(
    date: LocalDate?,
    time: LocalTime?,
    openDateTimeDialog: () -> Unit,
    onDeleteDateTimeSelected: () -> Unit,
    modifier: Modifier = Modifier
) = item {
    IconRowItem(
        iconContent = {
            Icon(
                imageVector = MonoIcons.Clock,
                contentDescription = null
            )
        },
        onClick = openDateTimeDialog,
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        AnimatedContent(targetState = date, label = "dateTime") { date ->
            if (date != null) {
                TaskDateTimeChip(
                    date = date,
                    time = time,
                    onClick = openDateTimeDialog,
                    modifier = Modifier.padding(start = 16.dp),
                    onTrailingIconClick = onDeleteDateTimeSelected
                )
            } else {
                Text(
                    text = stringResource(id = R.string.placeholder_date_time),
                    modifier = Modifier.padding(start = 16.dp)
                )
            }
        }
    }
}

fun LazyListScope.addEditSubTask(
    subTaskUiState: SubTaskUiState,
    onCreateSubTask: () -> Unit,
    onSubTaskCompletedChanged: (subTask: SubTask, isCompleted: Boolean) -> Unit,
    onSubTaskTitleChanged: (subTaskId: String, title: String) -> Unit,
    onDeleteSubTask: (subTaskId: String) -> Unit
) {
    when (subTaskUiState) {
        SubTaskUiState.Loading -> Unit
        is SubTaskUiState.Success -> {
            if (subTaskUiState.subTasks.isEmpty()) {
                addSubListButton(
                    iconEnabled = true,
                    onCreateSubTask = onCreateSubTask,
                    modifier = Modifier,
                    contentPadding = PaddingValues(start = 16.dp)
                )
            } else {
                subTasks(
                    subTasks = subTaskUiState.subTasks,
                    onSubTaskCompletedChanged = onSubTaskCompletedChanged,
                    onSubTaskTitleChanged = onSubTaskTitleChanged,
                    onDeleteSubTask = onDeleteSubTask
                )
                addSubListButton(
                    iconEnabled = false,
                    onCreateSubTask = onCreateSubTask,
                    modifier = Modifier
                )
            }
        }
    }
}

fun LazyListScope.addSubListButton(
    iconEnabled: Boolean,
    onCreateSubTask: () -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues()
) = item {
    val iconPadding = if (!iconEnabled) 20.dp else 0.dp
    IconRowItem(
        iconContent = {
            if (iconEnabled) {
                Icon(
                    imageVector = Icons.Default.SubdirectoryArrowRight,
                    contentDescription = null
                )
            }
        },
        onClick = onCreateSubTask,
        modifier = modifier.padding(horizontal = iconPadding),
        contentPadding = contentPadding
    ) {
        Text(
            text = stringResource(id = R.string.add_sub_tasks),
            modifier = Modifier.padding(start = 16.dp)
        )
    }
}

fun LazyListScope.subTasks(
    subTasks: List<SubTask>,
    onSubTaskCompletedChanged: (subTask: SubTask, isCompleted: Boolean) -> Unit,
    onSubTaskTitleChanged: (subTaskId: String, title: String) -> Unit,
    onDeleteSubTask: (subTaskId: String) -> Unit
) = items(
    items = subTasks,
    key = { subTask -> subTask.id }
) { subTask ->
    Row(
        modifier = Modifier
            .heightIn(min = 56.dp)
            .padding(start = 16.dp)
    ) {
        if (subTasks.first() == subTask) {
            Icon(
                imageVector = Icons.Default.SubdirectoryArrowRight,
                contentDescription = null,
                modifier = Modifier.padding(top = 16.dp)
            )
        } else {
            Spacer(modifier = Modifier.size(24.dp))
        }

        SubTaskItem(
            subTask = subTask,
            onCompletedChanged = { isCompleted ->
                onSubTaskCompletedChanged(
                    subTask,
                    isCompleted
                )
            },
            onUpdateSubTask = onSubTaskTitleChanged,
            onDeleteSubTask = { onDeleteSubTask(subTask.id) }
        )
    }
}

@Composable
fun SubTaskItem(
    subTask: SubTask,
    onCompletedChanged: (Boolean) -> Unit,
    onDeleteSubTask: () -> Unit,
    onUpdateSubTask: (subTaskId: String, title: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var isFocused by remember { mutableStateOf(false) }
    var title by remember { mutableStateOf(subTask.title) }

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = subTask.isCompleted,
            onCheckedChange = { onCompletedChanged(!subTask.isCompleted) },
            modifier = Modifier
        )
        MonoNoPaddingTextField(
            value = title,
            onValueChange = { title = it },
            modifier = Modifier
                .weight(1f)
                .onFocusChanged { isFocused = it.isFocused },
            placeholder = { Text(text = stringResource(id = R.string.placeholder_subtask_title)) }
        )
        if (isFocused) {
            IconButton(
                onClick = onDeleteSubTask,
                modifier = Modifier.padding(end = 8.dp)
            ) {
                Icon(imageVector = Icons.Default.Close, contentDescription = null)
            }
        } else {
            Spacer(
                modifier = Modifier
                    .size(48.dp)
                    .padding(end = 8.dp)
            )
        }
    }
    LaunchedEffect(isFocused) {
        if (!isFocused && subTask.title != title) {
            onUpdateSubTask(subTask.id, title)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectTaskListDialog(
    currentTaskList: TaskList?,
    taskListUiState: TaskListUiState,
    onDismiss: () -> Unit,
    onConfirm: (taskList: TaskList?) -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberStandardBottomSheetState(
            initialValue = SheetValue.Expanded,
            skipHiddenState = false
        ),
        dragHandle = null,
        windowInsets = WindowInsets.ime.only(WindowInsetsSides.Bottom)
    ) {
        when (taskListUiState) {
            TaskListUiState.Loading -> Unit
            is TaskListUiState.Success -> {
                LazyColumn(modifier = Modifier.padding(16.dp)) {
                    item {
                        Text(
                            text = stringResource(id = R.string.move_task_list),
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                    item {
                        IconRowItem(
                            iconContent = {
                                if (currentTaskList == null) Icon(
                                    imageVector = MonoIcons.Check,
                                    contentDescription = null
                                ) else {
                                    Spacer(modifier = Modifier.size(24.dp))
                                }
                            },
                            onClick = { onConfirm(null); onDismiss() },
                            contentPadding = PaddingValues(horizontal = 16.dp)
                        ) {
                            Text(
                                text = stringResource(id = R.string.no_task_list),
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }
                    items(
                        taskListUiState.taskLists,
                        key = { taskList -> taskList.id }
                    ) {
                        IconRowItem(
                            iconContent = {
                                if (currentTaskList == it) Icon(
                                    imageVector = MonoIcons.Check,
                                    contentDescription = null
                                ) else {
                                    Spacer(modifier = Modifier.size(24.dp))
                                }
                            },
                            onClick = { onConfirm(it); onDismiss() },
                            contentPadding = PaddingValues(horizontal = 16.dp)
                        ) {
                            Text(text = it.name, modifier = Modifier.padding(start = 8.dp))
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TaskDetailTopAppBar(
    isBookmarked: Boolean,
    onBackClick: () -> Unit,
    onBookmarkChange: () -> Unit,
    onDeleteTask: () -> Unit,
    modifier: Modifier = Modifier
) {
    MonoTopAppBar(
        title = {},
        modifier = modifier,
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = MonoIcons.ArrowBack,
                    contentDescription = "Back"
                )
            }
        },
        actions = {
            IconButton(onClick = onBookmarkChange) {
                Icon(
                    imageVector = if (isBookmarked) {
                        MonoIcons.Bookmark
                    } else {
                        MonoIcons.BookmarkOutlined
                    },
                    contentDescription = null,
                    tint = if (isBookmarked) {
                        MaterialTheme.colorScheme.primaryContainer
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    }
                )
            }
            TaskDetailMoreMenu(
                onDeleteTask = onDeleteTask
            )
        }
    )
}

@Composable
private fun TaskDetailMoreMenu(
    onDeleteTask: () -> Unit
) {
    MonoIconDropdownMenu(
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
private fun DeleteTaskDialog(
    onDismiss: () -> Unit,
    onDeleteTask: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = { onDeleteTask(); onDismiss() }) {
                Text(text = "Delete")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "Cancel")
            }
        },
        title = {
            Text(text = "This task has subtasks")
        },
        text = {
            Text(text = "Deleting this task will also delete subtasks")
        }
    )
}