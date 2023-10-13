package com.example.mono.core.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.material3.rememberTimePickerState
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mono.core.common.datetime.toDateMillis
import com.example.mono.core.common.datetime.toFormattedTime
import com.example.mono.core.common.datetime.toLocalDate
import com.example.mono.core.designsystem.component.MonoDatePickerDialog
import com.example.mono.core.designsystem.component.MonoInputChip
import com.example.mono.core.designsystem.component.MonoTextField
import com.example.mono.core.designsystem.component.MonoTimePickerDialog
import com.example.mono.core.designsystem.icon.MonoIcons
import com.example.mono.core.designsystem.theme.MonoTheme
import com.example.mono.core.model.task.TaskCreationParams
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import com.example.mono.core.designsystem.R as designSystemR

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalComposeUiApi::class
)
@Composable
fun CreateTaskDialog(
    onDismiss: () -> Unit,
    onCreateTask: (TaskCreationParams) -> Unit,
    modifier: Modifier = Modifier,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
) {
    val (titleFocusRef, detailFocusRef) = remember { FocusRequester.createRefs() }

    var showDetail by remember { mutableStateOf(false) }
    var showDateTimeDialog by remember { mutableStateOf(false) }

    var taskCreationParams by remember { mutableStateOf(TaskCreationParams()) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        modifier = modifier,
        sheetState = sheetState,
        dragHandle = null,
        windowInsets = WindowInsets.ime.only(WindowInsetsSides.Bottom)
    ) {
        // TODO: The animation for hiding the IME when the bottom sheet dialog transitions to the 'hide' state is not functioning.
        // This needs to be rechecked when there are future API changes.
        LaunchedEffect(Unit) {
            titleFocusRef.requestFocus()
        }
        LaunchedEffect(showDetail) {
            if (showDateTimeDialog) {
                detailFocusRef.requestFocus()
            }
        }

        MonoTextField(
            value = taskCreationParams.title,
            onValueChange = { taskCreationParams = taskCreationParams.copy(title = it) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .focusRequester(titleFocusRef),
            placeholder = { Text(text = stringResource(designSystemR.string.placeholder_task_title)) }
        )

        AnimatedVisibility(visible = showDetail) {
            MonoTextField(
                value = taskCreationParams.detail,
                onValueChange = { taskCreationParams = taskCreationParams.copy(detail = it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .focusRequester(detailFocusRef),
                textStyle = MaterialTheme.typography.labelLarge,
                placeholder = { Text(text = "Add description") }
            )
        }

        AnimatedContent(
            targetState = taskCreationParams.date,
            label = "create_date"
        ) { date ->
            date?.let {
                TaskDateTimeChip(
                    date = date,
                    time = taskCreationParams.time,
                    onClick = { showDateTimeDialog = true },
                    onTrailingIconClick = {
                        taskCreationParams = taskCreationParams.copy(
                            date = null,
                            time = null
                        )
                    },
                    modifier = Modifier.padding(start = 24.dp)
                )
            }
        }

        CreationTaskDialogButtonRow(
            isBookmarked = taskCreationParams.isBookmarked,
            onDismiss = onDismiss,
            showDetail = { showDetail = true },
            showDateDialog = { showDateTimeDialog = true },
            onBookmarkedChange = {
                taskCreationParams = taskCreationParams.copy(isBookmarked = it)
            },
            onCreateTask = { onCreateTask(taskCreationParams) },
            validateCreateTask = { taskCreationParams.title.isNotEmpty() })

        if (showDateTimeDialog) {
            MonoDateTimePicker(
                onDismiss = { showDateTimeDialog = false },
                onConfirm = { date, time ->
                    taskCreationParams = taskCreationParams.copy(
                        date = date,
                        time = time
                    )
                },
                previousDate = taskCreationParams.date,
                previousTime = taskCreationParams.time
            )
        }
        Spacer(modifier = Modifier.windowInsetsBottomHeight(WindowInsets.systemBars))
    }
}

@Composable
private fun CreationTaskDialogButtonRow(
    isBookmarked: Boolean,
    onDismiss: () -> Unit,
    showDetail: () -> Unit,
    showDateDialog: () -> Unit,
    onBookmarkedChange: (Boolean) -> Unit,
    onCreateTask: () -> Unit,
    validateCreateTask: () -> Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    ) {
        IconButton(onClick = showDetail) {
            Icon(imageVector = MonoIcons.Sort, contentDescription = null)
        }
        IconButton(onClick = showDateDialog) {
            Icon(imageVector = MonoIcons.Clock, contentDescription = null)
        }
        IconButton(onClick = { onBookmarkedChange(!isBookmarked) }) {
            Icon(
                imageVector = if (isBookmarked) MonoIcons.Bookmark else MonoIcons.BookmarkOutlined,
                contentDescription = null,
                tint = if (isBookmarked) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.onSurface
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        TextButton(
            onClick = { onCreateTask(); onDismiss() },
            enabled = validateCreateTask()
        ) {
            Text(
                text = stringResource(designSystemR.string.dialog_confirm_save),
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

/**
 * [MonoDateTimePicker] A Composable with the added dependency of a Time Picker to a Date Picker.
 *
 * @param previousDate previous date if it had date.
 * @param previousTime previous time if it had time.
 * @param onDismiss Called when the user tries to dismiss.
 * @param onConfirm Called when the user confirm dialog action.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonoDateTimePicker(
    previousDate: LocalDate?,
    previousTime: LocalTime?,
    onDismiss: () -> Unit,
    onConfirm: (date: LocalDate?, time: LocalTime?) -> Unit
) {
    val currentLocalDateTime = LocalDateTime.now()

    // set initial selected date if a previous date is exists.
    val dateState = rememberDatePickerState(
        initialSelectedDateMillis = previousDate?.toDateMillis()
            ?: currentLocalDateTime.toLocalDate().toDateMillis()
    )
    // set initial hour and minute if a previous time is exists.
    val timeState = rememberTimePickerState(
        initialHour = previousTime?.hour ?: currentLocalDateTime.hour,
        initialMinute = previousTime?.minute ?: currentLocalDateTime.minute
    )

    // State value for temporarily storing time.
    var time by remember { mutableStateOf(previousTime) }

    var showTimeDialog by rememberSaveable { mutableStateOf(false) }
    var toggleTimeDialogType by rememberSaveable { mutableStateOf(false) }

    MonoDatePickerDialog(
        onDismiss = onDismiss,
        onConfirm = { onConfirm(dateState.selectedDateMillis.toLocalDate(), time); onDismiss() },
        state = dateState
    ) {
        IconRowItem(
            iconContent = {
                Icon(
                    imageVector = Icons.Default.AccessTime,
                    contentDescription = null
                )
            },
            onClick = { showTimeDialog = true }
        ) {
            Spacer(modifier = Modifier.width(8.dp))
            AnimatedContent(targetState = time, label = "create_time") {
                if (it != null) {
                    MonoInputChip(
                        label = { Text(text = it.toFormattedTime()) },
                        onClick = { showTimeDialog = true },
                        onTrailingIconClick = { time = null }
                    )
                } else {
                    Text(text = stringResource(id = designSystemR.string.placeholder_time))
                }
            }
        }
    }

    if (showTimeDialog) {
        MonoTimePickerDialog(
            onDismiss = { showTimeDialog = false },
            onConfirm = { time = LocalTime.of(timeState.hour, timeState.minute) },
            isInputType = toggleTimeDialogType,
            toggleDialogType = { toggleTimeDialogType = !toggleTimeDialogType },
            state = timeState
        )
    }
}

/**
 * [IconRowItem] is a Composable for aligning an Icon and Content slot within a single row.
 *
 * @param iconContent Icon content slot.
 * @param onClick Called when the user click the row.
 * @param modifier Modifier to be applied to a row.
 * @param contentPadding content's padding.
 * @param content Row content slot.
 */
@Composable
fun IconRowItem(
    iconContent: @Composable () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
    content: @Composable () -> Unit
) {
    Surface(
        modifier
            .fillMaxWidth()
            .clickable { onClick() },
        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .heightIn(min = 56.dp)
                .padding(contentPadding),
            verticalAlignment = Alignment.CenterVertically
        ) {
            iconContent()
            content()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(name = "Create task dialog")
@Composable
private fun CreateTaskDialogPreview() {
    MonoTheme {
        CreateTaskDialog(
            onDismiss = {},
            onCreateTask = {},
            sheetState = rememberStandardBottomSheetState(
                initialValue = SheetValue.Expanded
            )
        )
    }
}