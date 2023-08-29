package com.example.mono.feature.tasks.taskDetail

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.mono.core.common.datetime.toFormattedDate
import com.example.mono.core.common.datetime.toFormattedTime
import com.example.mono.core.designsystem.component.MonoFloatingButton
import com.example.mono.core.designsystem.component.MonoInputChip
import com.example.mono.core.designsystem.component.MonoOutlinedTextField
import com.example.mono.feature.tasks.R
import com.example.mono.feature.tasks.components.IconRow
import com.example.mono.feature.tasks.components.MonoDateTimePicker
import com.example.mono.feature.tasks.components.TaskDetailTopAppBar
import java.time.LocalDate
import java.time.LocalTime

@Composable
internal fun TaskDetailRoute(
    onBackClick: () -> Unit,
    onUpdateTask: () -> Unit,
    onDeleteTask: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TaskDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    TaskDetailScreen(
        uiState = uiState,
        onBackClick = onBackClick,
        updateTask = viewModel::updateTask,
        onDeleteTask = viewModel::deleteTask,
        updateTitle = viewModel::updateTitle,
        updateDescription = viewModel::updateDescription,
        updateDateTime = viewModel::updateDateTime,
        modifier = modifier
    )
    LaunchedEffect(key1 = uiState.isTaskSaved) {
        if (uiState.isTaskSaved) {
            onUpdateTask()
        }
    }
    LaunchedEffect(key1 = uiState.isTaskDeleted) {
        if (uiState.isTaskDeleted) {
            onDeleteTask()
        }
    }
}

@Composable
internal fun TaskDetailScreen(
    uiState: TaskDetailUiState,
    onBackClick: () -> Unit,
    onDeleteTask: () -> Unit,
    updateTask: () -> Unit,
    updateTitle: (title: String) -> Unit,
    updateDescription: (description: String) -> Unit,
    updateDateTime: (date: LocalDate?, time: LocalTime?) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TaskDetailTopAppBar(
                onBackClick = onBackClick,
                isBookmarked = uiState.isBookmarked,
                updateBookmarked = { },
                onDeleteTask = onDeleteTask,
            )
        },
        floatingActionButton = {
            MonoFloatingButton(
                icon = Icons.Default.EditNote,
                onClick = updateTask,
                modifier = Modifier.imePadding()
            )
        }
    ) { padding ->
        TaskDetailContent(
            uiState = uiState,
            updateTitle = updateTitle,
            updateDescription = updateDescription,
            updateDateTime = updateDateTime,
            modifier = Modifier.padding(padding)
        )
    }
}

@Composable
internal fun TaskDetailContent(
    uiState: TaskDetailUiState,
    updateTitle: (title: String) -> Unit,
    updateDescription: (description: String) -> Unit,
    updateDateTime: (date: LocalDate?, time: LocalTime?) -> Unit,
    modifier: Modifier = Modifier
) {
    var showDateDialog by rememberSaveable { mutableStateOf(false) }

    val formattedDate = uiState.date?.let(LocalDate::toFormattedDate)
    val formattedTime = uiState.time?.let(LocalTime::toFormattedTime)

    Column(modifier = modifier.padding(horizontal = 16.dp)) {
        IconRow(icon = null) {
            MonoOutlinedTextField(
                value = uiState.title,
                onValueChange = updateTitle,
                textStyle = MaterialTheme.typography.headlineSmall,
                placeholder = { Text(text = stringResource(id = R.string.placeholder_title)) }
            )
        }
        IconRow(icon = Icons.Default.Sort) {
            MonoOutlinedTextField(
                value = uiState.description,
                onValueChange = updateDescription,
                textStyle = MaterialTheme.typography.bodyLarge,
                placeholder = { Text(text = stringResource(id = R.string.placeholder_description)) }
            )
        }
        IconRow(
            icon = Icons.Default.AccessTime,
            onClickRow = { showDateDialog = true }
        ) {
            Spacer(modifier = Modifier.width(16.dp))
            AnimatedContent(
                targetState = formattedDate,
                label = "create_date"
            ) {
                if (it != null) {
                    MonoInputChip(
                        label = {
                            Text(
                                text = if (formattedTime != null) {
                                    "$it $formattedTime"
                                } else {
                                    "$it"
                                }
                            )
                        },
                        onClick = { showDateDialog = true },
                        onTrailingClick = { updateDateTime(null, null) }
                    )
                } else {
                    Text(text = stringResource(id = R.string.placeholder_date_time))
                }
            }
        }
    }

    if (showDateDialog) {
        MonoDateTimePicker(
            onDismiss = { showDateDialog = false },
            onConfirm = updateDateTime,
            previousDate = uiState.date,
            previousTime = uiState.time
        )
    }
}