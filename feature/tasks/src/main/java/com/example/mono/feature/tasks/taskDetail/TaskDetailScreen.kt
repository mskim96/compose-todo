package com.example.mono.feature.tasks.taskDetail

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.mono.core.common.datetime.toFormattedDate
import com.example.mono.core.common.datetime.toFormattedTime
import com.example.mono.core.designsystem.component.MonoInputChip
import com.example.mono.core.designsystem.component.MonoTextField
import com.example.mono.core.designsystem.theme.MonoTheme
import com.example.mono.feature.tasks.R
import com.example.mono.feature.tasks.components.IconRow
import com.example.mono.feature.tasks.components.MonoDateTimePicker
import com.example.mono.feature.tasks.components.TaskDetailTopAppBar
import java.time.LocalDate
import java.time.LocalTime

@Composable
internal fun TaskDetailRoute(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TaskDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    TaskDetailScreen(
        uiState = uiState,
        onUpdateTask = viewModel::updateTask,
        onDeleteTask = viewModel::deleteTask,
        updateTitle = viewModel::updateTitle,
        updateDescription = viewModel::updateDescription,
        updateDateTime = viewModel::updateDateTime,
        updateCompleted = viewModel::updateCompleted,
        toggleBookmark = viewModel::toggleBookmark,
        modifier = modifier
    )
    LaunchedEffect(key1 = uiState) {
        if(uiState.isTaskSaved || uiState.isTaskDeleted){
            onBackClick()
        }
    }
}

@Composable
internal fun TaskDetailScreen(
    uiState: TaskDetailUiState,
    onUpdateTask: () -> Unit,
    onDeleteTask: () -> Unit,
    updateTitle: (title: String) -> Unit,
    updateDescription: (description: String) -> Unit,
    updateDateTime: (date: LocalDate?, time: LocalTime?) -> Unit,
    updateCompleted: (Boolean) -> Unit,
    toggleBookmark: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler {
        onUpdateTask()
    }
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TaskDetailTopAppBar(
                onBackClick = onUpdateTask,
                isBookmarked = uiState.isBookmarked,
                toggleBookmark = { toggleBookmark(!uiState.isBookmarked) },
                onDeleteTask = onDeleteTask,
            )
        },
        bottomBar = {
            val markComplete = if (uiState.isCompleted) "Mark Active" else "Mark Complete"
            BottomAppBar(
                actions = {},
                floatingActionButton = {
                    TextButton(onClick = { updateCompleted(!uiState.isCompleted) }) {
                        Text(text = markComplete, style = MaterialTheme.typography.titleSmall)
                    }
                }
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
            MonoTextField(
                value = uiState.title,
                onValueChange = updateTitle,
                textStyle = MaterialTheme.typography.headlineSmall,
                placeholder = { Text(text = stringResource(id = R.string.placeholder_title)) }
            )
        }
        IconRow(icon = Icons.Default.Sort) {
            MonoTextField(
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
            ) { date ->
                date?.let {
                    MonoInputChip(
                        label = { Text(text = formattedTime?.let { time -> "$it $time" } ?: it) },
                        onClick = { showDateDialog = true },
                        onTrailingClick = { updateDateTime(null, null) }
                    )
                } ?: Text(text = stringResource(id = R.string.placeholder_date_time))
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

@Preview(name = "Task detail screen")
@Composable
private fun TaskDetailScreenPreview() {
    MonoTheme {
        TaskDetailScreen(
            uiState = TaskDetailUiState(),
            onUpdateTask = { },
            onDeleteTask = { },
            updateTitle = { },
            updateDescription = { },
            updateDateTime = { _, _ -> },
            updateCompleted = { },
            toggleBookmark = { }
        )
    }
}