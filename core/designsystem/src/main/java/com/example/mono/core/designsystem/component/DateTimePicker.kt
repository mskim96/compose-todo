package com.example.mono.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.outlined.KeyboardAlt
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.mono.core.designsystem.R
import com.example.mono.core.designsystem.theme.MonoTheme

/**
 * Mono date picker dialog wrapped datePicker dialog as well as other content slot.
 *
 * @param onDismiss Called when the user tries to dismiss.
 * @param onConfirm Called when the user confirm dialog action.
 * @param modifier Modifier to be applied to the Date picker dialog.
 * @param state state of date picker.
 * @param content content of only date picker or content with the dialog.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonoDatePickerDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier,
    state: DatePickerState = rememberDatePickerState(),
    content: @Composable (() -> Unit)? = null
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = MaterialTheme.shapes.extraLarge,
            tonalElevation = 6.dp,
            modifier = modifier
                .widthIn(min = 360.dp)
                .background(
                    shape = MaterialTheme.shapes.extraLarge,
                    color = MaterialTheme.colorScheme.surface
                ),
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                DatePicker(
                    state = state,
                    colors = DatePickerDefaults.colors()
                )
                if (content != null) {
                    Divider()
                    content()
                }
                Row(
                    modifier = Modifier
                        .height(40.dp)
                        .fillMaxWidth()
                ) {
                    Spacer(modifier = Modifier.weight(1f))
                    TextButton(onClick = onDismiss) {
                        Text(stringResource(id = R.string.dialog_dismiss))
                    }
                    TextButton(onClick = { onConfirm(); onDismiss() }) {
                        Text(stringResource(id = R.string.dialog_confirm))
                    }
                }
            }
        }
    }
}

/**
 * Mono time picker dialog wrapped timePicker dialog as well as other content slot.
 *
 * @param onDismiss Called when the user tries to dismiss.
 * @param onConfirm Called when the user confirm dialog action.
 * @param modifier Modifier to be applied to the time picker.
 * @param title Title of dialog title
 * @param isInputType Check time picker dialog type.
 * @param toggleDialogType Change dialog type when user clicked toggle icon.
 * @param state State of time picker.
 * @param content content of only time picker or content with the dialog.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonoTimePickerDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier,
    title: String = "Select Time",
    isInputType: Boolean = false,
    toggleDialogType: () -> Unit = {},
    state: TimePickerState = rememberTimePickerState(),
    content: @Composable (() -> Unit)? = null,
) {
    val toggleIcon = if (isInputType) {
        Icons.Default.AccessTime
    } else {
        Icons.Outlined.KeyboardAlt
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = MaterialTheme.shapes.extraLarge,
            tonalElevation = 6.dp,
            modifier = modifier
                .width(IntrinsicSize.Min)
                .height(IntrinsicSize.Min)
                .background(
                    shape = MaterialTheme.shapes.extraLarge,
                    color = MaterialTheme.colorScheme.surface
                ),
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp),
                    text = title,
                    style = MaterialTheme.typography.labelMedium
                )
                if (isInputType) {
                    TimeInput(
                        state = state,
                        colors = TimePickerDefaults.colors()
                    )
                } else {
                    TimePicker(
                        state = state,
                        colors = TimePickerDefaults.colors()
                    )
                }
                if (content != null) {
                    Divider()
                    content()
                    Divider()
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                ) {
                    IconButton(onClick = toggleDialogType) {
                        Icon(imageVector = toggleIcon, contentDescription = null)
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    TextButton(onClick = onDismiss) {
                        Text(stringResource(id = R.string.dialog_dismiss))
                    }
                    TextButton(onClick = { onConfirm(); onDismiss() }) {
                        Text(stringResource(id = R.string.dialog_confirm))
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(name = "Date picker")
@Composable
private fun MonoDatePicker() {
    MonoTheme {
        MonoDatePickerDialog(
            onDismiss = {},
            onConfirm = {}
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(name = "Time picker")
@Composable
private fun MonoTimePickerPreview() {
    MonoTheme {
        MonoTimePickerDialog(
            onDismiss = {},
            onConfirm = {}
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(name = "Time input")
@Composable
private fun MonoTimeInputPreview() {
    MonoTheme {
        MonoTimePickerDialog(
            onDismiss = {},
            onConfirm = {},
            isInputType = true
        )
    }
}