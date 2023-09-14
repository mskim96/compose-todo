package com.example.mono.feature.tasks.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.mono.core.common.datetime.toDateMillis
import com.example.mono.core.common.datetime.toFormattedTime
import com.example.mono.core.common.datetime.toLocalDate
import com.example.mono.core.designsystem.component.MonoDatePickerDialog
import com.example.mono.core.designsystem.component.MonoInputChip
import com.example.mono.core.designsystem.component.MonoTimePickerDialog
import com.example.mono.feature.tasks.R
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

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
                    Text(text = stringResource(id = R.string.placeholder_time))
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