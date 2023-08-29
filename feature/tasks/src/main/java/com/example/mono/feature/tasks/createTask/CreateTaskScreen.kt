package com.example.mono.feature.tasks.createTask

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.mono.core.common.datetime.toFormattedDate
import com.example.mono.core.common.datetime.toFormattedTime
import com.example.mono.core.designsystem.component.MonoInputChip
import com.example.mono.core.designsystem.component.MonoOutlinedTextField
import com.example.mono.core.designsystem.theme.MonoTheme
import com.example.mono.feature.tasks.R
import com.example.mono.feature.tasks.components.CreateTaskTopAppBar
import com.example.mono.feature.tasks.components.IconRow
import com.example.mono.feature.tasks.components.MonoDateTimePicker
import java.time.LocalDate
import java.time.LocalTime

@Composable
internal fun CreateTaskRoute(
    onBackClick: () -> Unit,
    onTaskUpdate: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CreateTaskViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    CreateTaskScreen(
        uiState = uiState,
        onBackClick = onBackClick,
        createNewTask = viewModel::createNewTask,
        updateTitle = viewModel::updateTitle,
        updateDescription = viewModel::updateDescription,
        updateDateTime = viewModel::updateDateTime,
        updateBookmarked = viewModel::toggleBookmarked,
        modifier = modifier
    )
    /**
     * navigate other route whether task is saved.
     */
    LaunchedEffect(key1 = uiState.isTaskSaved) {
        if (uiState.isTaskSaved) {
            onTaskUpdate()
        }
    }
}

@Composable
internal fun CreateTaskScreen(
    uiState: CreateTaskUiState,
    onBackClick: () -> Unit,
    createNewTask: () -> Unit,
    updateTitle: (title: String) -> Unit,
    updateDescription: (description: String) -> Unit,
    updateDateTime: (date: LocalDate?, time: LocalTime?) -> Unit,
    updateBookmarked: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            CreateTaskTopAppBar(
                onBackClick = onBackClick,
                isBookmarked = uiState.isBookmarked,
                toggleBookmark = { updateBookmarked(!uiState.isBookmarked) },
                createNewTask = createNewTask
            )
        }
    ) { padding ->
        CreateTaskContent(
            uiState = uiState,
            updateTitle = updateTitle,
            updateDescription = updateDescription,
            updateDateTime = updateDateTime,
            modifier = Modifier.padding(padding)
        )
    }
}

@Composable
internal fun CreateTaskContent(
    uiState: CreateTaskUiState,
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

@Preview(name = "Create task screen")
@Composable
private fun CreateTaskScreenPreview() {
    MonoTheme {
        CreateTaskScreen(
            uiState = CreateTaskUiState(),
            onBackClick = {},
            createNewTask = {},
            updateTitle = {},
            updateDescription = {},
            updateDateTime = { _, _ -> },
            updateBookmarked = {}
        )
    }
}