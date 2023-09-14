package com.example.mono.feature.tasks.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.mono.core.common.datetime.combineWithTime
import com.example.mono.core.designsystem.component.MonoInputChip
import java.time.LocalDate
import java.time.LocalTime

@Composable
internal fun TaskDateTimeChip(
    date: LocalDate,
    time: LocalTime?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    onTrailingIconClick: (() -> Unit)? = null
) {
    MonoInputChip(
        onClick = onClick,
        label = { Text(text = date.combineWithTime(time)) },
        modifier = modifier,
        onTrailingIconClick = onTrailingIconClick
    )
}