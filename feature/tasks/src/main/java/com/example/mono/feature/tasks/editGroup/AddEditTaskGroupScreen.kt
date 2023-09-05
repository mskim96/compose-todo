package com.example.mono.feature.tasks.editGroup

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.FolderDelete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.mono.core.designsystem.component.MonoTextField
import com.example.mono.core.model.TaskGroup
import com.example.mono.feature.tasks.R
import com.example.mono.feature.tasks.components.AddEditTaskGroupTopAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditTaskGroupRoute(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AddEditTaskGroupViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(
        topBar = {
            AddEditTaskGroupTopAppBar(
                onBackClick = onBackClick,
                scrollBehavior = scrollBehavior
            )
        }
    ) { padding ->
        AddEditTaskGroupScreen(
            uiState = uiState,
            onCreateNewTaskGroup = viewModel::createTaskGroup,
            onUpdateTaskGroup = viewModel::updateTaskGroup,
            onDeleteTaskGroup = viewModel::deleteTaskGroup,
            modifier = modifier
                .fillMaxSize()
                .padding(padding),
            scrollConnection = scrollBehavior.nestedScrollConnection
        )
    }
}

@Composable
fun AddEditTaskGroupScreen(
    uiState: AddEditTaskGroupUiState,
    onCreateNewTaskGroup: (name: String) -> Unit,
    onUpdateTaskGroup: (TaskGroup) -> Unit,
    onDeleteTaskGroup: (TaskGroup) -> Unit,
    modifier: Modifier = Modifier,
    scrollConnection: NestedScrollConnection
) {
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    var newGroupIsFocused by remember { mutableStateOf(false) }
    var newGroupName by remember { mutableStateOf("") }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .nestedScroll(scrollConnection)
    ) {
        item {
            MonoTextField(
                value = newGroupName,
                onValueChange = { newGroupName = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { focusRequester.requestFocus() }
                    .focusRequester(focusRequester)
                    .onFocusChanged {
                        newGroupIsFocused = it.isFocused
                        if (!it.isFocused) newGroupName = ""
                    },
                placeholder = {
                    Text(text = stringResource(id = R.string.placeholder_task_group))
                },
                leadingIcon = {
                    IconButton(
                        onClick = { if (newGroupIsFocused) focusManager.clearFocus() }
                    ) {
                        Crossfade(
                            targetState = newGroupIsFocused,
                            label = "create_task_group_new"
                        ) {
                            if (it) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = null
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = null
                                )
                            }
                        }
                    }
                },
                trailingIcon = {
                    AnimatedVisibility(
                        visible = newGroupIsFocused,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        IconButton(onClick = {
                            onCreateNewTaskGroup(newGroupName)
                            focusManager.clearFocus()
                        }) {
                            Icon(imageVector = Icons.Default.Check, contentDescription = null)
                        }
                    }
                }
            )
        }
        items(uiState.items) {
            AddEditTaskGroupItem(
                taskGroup = it,
                onUpdateTaskGroup = onUpdateTaskGroup,
                onDeleteTaskGroup = onDeleteTaskGroup
            )
        }
    }
}

@Composable
fun AddEditTaskGroupItem(
    taskGroup: TaskGroup,
    onUpdateTaskGroup: (TaskGroup) -> Unit,
    onDeleteTaskGroup: (TaskGroup) -> Unit,
    modifier: Modifier = Modifier
) {
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    var taskName by rememberSaveable {
        mutableStateOf(taskGroup.name)
    }
    var isFocused by remember { mutableStateOf(false) }

    MonoTextField(
        value = taskName,
        onValueChange = { taskName = it },
        modifier = modifier
            .fillMaxWidth()
            .clickable { focusRequester.requestFocus() }
            .focusRequester(focusRequester)
            .onFocusChanged {
                isFocused = it.isFocused
                if (taskName != taskGroup.name) onUpdateTaskGroup(taskGroup.copy(name = taskName))
            },
        leadingIcon = {
            IconButton(onClick = { if (isFocused) onDeleteTaskGroup(taskGroup) }) {
                Crossfade(targetState = isFocused, label = "delete_task_group") {
                    if (it) {
                        Icon(imageVector = Icons.Outlined.FolderDelete, contentDescription = null)
                    } else {
                        Icon(imageVector = Icons.Outlined.Folder, contentDescription = null)
                    }
                }
            }
        },
        trailingIcon = {
            IconButton(onClick = {
                if (isFocused) {
                    onUpdateTaskGroup(taskGroup.copy(name = taskName))
                    focusManager.clearFocus()
                } else {
                    focusRequester.requestFocus()
                }
            }) {
                Crossfade(targetState = isFocused, label = "edit_task_group") {
                    if (it) {
                        Icon(imageVector = Icons.Default.Check, contentDescription = null)
                    } else {
                        Icon(imageVector = Icons.Default.Edit, contentDescription = null)
                    }
                }
            }
        }
    )
}