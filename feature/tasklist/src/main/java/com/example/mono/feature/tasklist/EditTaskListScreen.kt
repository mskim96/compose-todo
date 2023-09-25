package com.example.mono.feature.tasklist

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.mono.core.designsystem.component.MonoTextField
import com.example.mono.core.designsystem.component.MonoTopAppBar
import com.example.mono.core.designsystem.icon.MonoIcons
import com.example.mono.core.designsystem.theme.MonoTheme
import com.example.mono.core.model.TaskList
import com.example.mono.feature.tasklist.navigation.createTaskListType

@OptIn(ExperimentalComposeUiApi::class)
@Composable
internal fun EditTaskListRoute(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: EditTaskListViewModel = hiltViewModel()
) {
    val keyboardManager = LocalSoftwareKeyboardController.current
    val taskListUiState by viewModel.taskListUiState.collectAsStateWithLifecycle()

    val backClick = {
        keyboardManager?.hide()
        onBackClick()
    }

    EditTaskListScreen(
        taskListUiState = taskListUiState,
        modifyType = viewModel.modifyType,
        onCreateTaskList = viewModel::createTaskList,
        onUpdateTaskList = viewModel::updateTaskList,
        onDeleteTaskList = viewModel::deleteTaskList,
        onBackClick = backClick,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun EditTaskListScreen(
    taskListUiState: EditTaskListUiState,
    modifyType: String,
    onCreateTaskList: (String) -> Unit,
    onUpdateTaskList: (TaskList) -> Unit,
    onDeleteTaskList: (TaskList) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val topAppBarState = rememberTopAppBarState()
    val scrollBehavior =
        TopAppBarDefaults.enterAlwaysScrollBehavior(topAppBarState)

    Scaffold(
        modifier = modifier,
        topBar = {
            EditTaskListTopAppBar(
                onBackClick = onBackClick,
                scrollBehavior = scrollBehavior
            )
        }
    ) { padding ->
        EditTaskListContent(
            taskListUiState = taskListUiState,
            modifyType = modifyType,
            onCreateTaskList = onCreateTaskList,
            onUpdateTaskList = onUpdateTaskList,
            onDeleteTaskList = onDeleteTaskList,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .nestedScroll(scrollBehavior.nestedScrollConnection)
        )
    }
}

@Composable
internal fun EditTaskListContent(
    taskListUiState: EditTaskListUiState,
    modifyType: String,
    onCreateTaskList: (String) -> Unit,
    onUpdateTaskList: (TaskList) -> Unit,
    onDeleteTaskList: (TaskList) -> Unit,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current
    var showDeleteTaskListDialog by remember { mutableStateOf(false) }
    var taskListToDelete by remember { mutableStateOf<TaskList?>(null) }

    if (showDeleteTaskListDialog) {
        DeleteTaskListDialog(
            onDismiss = {
                showDeleteTaskListDialog = false
                taskListToDelete = null
            },
            onConfirmTaskListDelete = {
                taskListToDelete?.let { taskList -> onDeleteTaskList(taskList) }
                showDeleteTaskListDialog = false
            }
        )
    }
    LazyColumn(
        modifier = modifier,
        state = rememberLazyListState()
    ) {
        createTaskList(
            modifyType = modifyType,
            onCreateTaskList = onCreateTaskList,
            onClearFocus = focusManager::clearFocus,
            modifier = Modifier
        )
        taskLists(
            taskListUiState = taskListUiState,
            onUpdateTaskList = onUpdateTaskList,
            onDeleteTaskList = {
                showDeleteTaskListDialog = true
                taskListToDelete = it
            },
            onClearFocus = focusManager::clearFocus,
            modifier = Modifier
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditTaskListTopAppBar(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior
) {
    MonoTopAppBar(
        titleRes = R.string.edit_task_lists,
        modifier = modifier,
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = MonoIcons.ArrowBack,
                    contentDescription = "Back"
                )
            }
        },
        scrollBehavior = scrollBehavior
    )
}

internal fun LazyListScope.createTaskList(
    modifyType: String,
    onCreateTaskList: (String) -> Unit,
    onClearFocus: () -> Unit,
    modifier: Modifier = Modifier
) = item {
    val focusRequester = remember { FocusRequester() }

    var taskListIsFocused by remember { mutableStateOf(false) }
    var taskListName by remember { mutableStateOf("") }

    val createTaskListIfNotEmpty = {
        if (taskListName.isNotEmpty()) {
            onCreateTaskList(taskListName)
            onClearFocus()
        } else {
            onClearFocus()
        }
    }

    Column(modifier = modifier) {
        if (taskListIsFocused) {
            Divider()
        }
        MonoTextField(
            value = taskListName,
            onValueChange = { taskListName = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp)
                .focusRequester(focusRequester)
                .onFocusChanged {
                    taskListIsFocused = it.isFocused
                    if (!it.isFocused) taskListName = ""
                },
            placeholder = { Text(text = stringResource(R.string.create_new_task_list)) },
            leadingIcon = {
                FocusedIconButton(
                    isFocused = taskListIsFocused,
                    onFocusedIconClick = onClearFocus,
                    onUnFocusedIconClick = focusRequester::requestFocus,
                    focusedIcon = MonoIcons.Close,
                    unFocusedIcon = MonoIcons.Add
                )
            },
            trailingIcon = {
                if (taskListIsFocused) {
                    IconButton(onClick = createTaskListIfNotEmpty) {
                        Icon(imageVector = MonoIcons.Check, contentDescription = null)
                    }
                }
            },
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { createTaskListIfNotEmpty() }
            )
        )
        if (taskListIsFocused) {
            Divider()
        }
    }
    LaunchedEffect(Unit) {
        if (modifyType == createTaskListType) {
            focusRequester.requestFocus()
        }
    }
}

internal fun LazyListScope.taskLists(
    taskListUiState: EditTaskListUiState,
    onUpdateTaskList: (TaskList) -> Unit,
    onDeleteTaskList: (TaskList) -> Unit,
    onClearFocus: () -> Unit,
    modifier: Modifier = Modifier
) {
    when (taskListUiState) {
        EditTaskListUiState.Loading -> Unit
        is EditTaskListUiState.Success -> {
            items(
                items = taskListUiState.taskLists,
                key = { it.id }
            ) { taskList ->
                EditTaskListItem(
                    taskList = taskList,
                    onUpdateTaskList = onUpdateTaskList,
                    onDeleteTaskList = onDeleteTaskList,
                    onClearFocus = onClearFocus,
                    modifier = modifier
                )
            }
        }
    }
}

@Composable
private fun EditTaskListItem(
    taskList: TaskList,
    onUpdateTaskList: (TaskList) -> Unit,
    onDeleteTaskList: (TaskList) -> Unit,
    onClearFocus: () -> Unit,
    modifier: Modifier = Modifier
) {
    val focusRequester = remember { FocusRequester() }

    var taskListIsFocused by remember { mutableStateOf(false) }
    var taskListName by remember { mutableStateOf(taskList.name) }

    val updateTaskListIfChanged = {
        if (taskList.name != taskListName) {
            onUpdateTaskList(taskList.copy(name = taskListName))
        }
    }

    Column(modifier = modifier) {
        if (taskListIsFocused) {
            Divider()
        }
        MonoTextField(
            value = taskListName,
            onValueChange = { taskListName = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp)
                .focusRequester(focusRequester)
                .onFocusChanged { taskListIsFocused = it.isFocused },
            leadingIcon = {
                FocusedIconButton(
                    isFocused = taskListIsFocused,
                    onFocusedIconClick = { onDeleteTaskList(taskList) },
                    onUnFocusedIconClick = focusRequester::requestFocus,
                    focusedIcon = MonoIcons.Close,
                    unFocusedIcon = MonoIcons.TaskList
                )
            },
            trailingIcon = {
                FocusedIconButton(
                    isFocused = taskListIsFocused,
                    onFocusedIconClick = {
                        onUpdateTaskList(taskList.copy(name = taskListName))
                        onClearFocus()
                    },
                    onUnFocusedIconClick = focusRequester::requestFocus,
                    focusedIcon = MonoIcons.Check,
                    unFocusedIcon = MonoIcons.Edit
                )
            },
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    if (taskListName != taskList.name) {
                        onUpdateTaskList(taskList.copy(name = taskListName))
                        onClearFocus()
                    } else {
                        onClearFocus()
                    }
                }
            )
        )
        if (taskListIsFocused) {
            Divider()
        }

        LaunchedEffect(key1 = taskListIsFocused) {
            if (!taskListIsFocused) {
                updateTaskListIfChanged()
            }
        }
    }
}

/**
 * IconButton that dynamically adjusts icons and actions based on focus state.
 *
 * @param isFocused state of focus.
 * @param onFocusedIconClick called when the user clicked if state is focused.
 * @param onUnFocusedIconClick called when the user clicked if state is unfocused.
 * @param focusedIcon icon content if state is focused.
 * @param unFocusedIcon icon content if state is unfocused.
 * @param modifier Modifier to be applied to the icon button.
 */
@Composable
private fun FocusedIconButton(
    isFocused: Boolean,
    onFocusedIconClick: (() -> Unit),
    onUnFocusedIconClick: (() -> Unit),
    focusedIcon: ImageVector,
    unFocusedIcon: ImageVector,
    modifier: Modifier = Modifier
) {
    val icon = if (isFocused) focusedIcon else unFocusedIcon
    val onIconClick = {
        if (isFocused) onFocusedIconClick() else onUnFocusedIconClick()
    }
    IconButton(
        onClick = onIconClick,
        modifier = modifier
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null
        )
    }
}

/**
 * Dialog that Request Confirmation from the User Before Deleting a Task List.
 *
 * @param onDismiss Called when the user tries to dismiss.
 * @param onConfirmTaskListDelete Called when the user confirm delete task list.
 */
@Composable
private fun DeleteTaskListDialog(
    onDismiss: () -> Unit,
    onConfirmTaskListDelete: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = { onConfirmTaskListDelete(); onDismiss() }) {
                Text(text = stringResource(R.string.delete_task_list))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(R.string.cancel_delete_task_list))
            }
        },
        icon = {
            Icon(
                imageVector = MonoIcons.DeleteTaskList,
                contentDescription = null
            )
        },
        title = { Text(text = stringResource(R.string.delete_task_list_dialog_title)) },
        text = {
            Text(
                text = stringResource(R.string.delete_task_list_dialog_subtitle),
                style = MaterialTheme.typography.bodyLarge
            )
        }
    )
}

@Preview(name = "Edit task list screen")
@Composable
private fun EditTaskListScreenPreview() {
    MonoTheme {
        EditTaskListScreen(
            taskListUiState = EditTaskListUiState.Success(
                taskLists = listOf(TaskList("1", "Preview"))
            ),
            modifyType = "",
            onCreateTaskList = {},
            onUpdateTaskList = {},
            onDeleteTaskList = {},
            onBackClick = {}
        )
    }
}

@Preview(name = "Delete task list dialog")
@Composable
private fun DeleteTaskListDialogPreview() {
    MonoTheme {
        DeleteTaskListDialog(
            onDismiss = {},
            onConfirmTaskListDelete = {}
        )
    }
}