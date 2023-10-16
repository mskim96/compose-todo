package com.example.mono.feature.calendar.component

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.mono.core.model.task.Task
import java.time.LocalDate

@Composable
fun DayOfWeekHeading(day: String, modifier: Modifier = Modifier) {
    DayContainer(modifier) {
        Text(
            text = day,
            modifier = Modifier
                .fillMaxSize()
                .wrapContentHeight(Alignment.CenterVertically),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.labelLarge
        )
    }
}

@Composable
fun Day(
    day: LocalDate,
    isToday: Boolean,
    isSelected: Boolean,
    taskIndicator: List<Task>,
    onDayClicked: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    val contentColor = when {
        isToday -> MaterialTheme.colorScheme.surface
        else -> MaterialTheme.colorScheme.onSurface
    }
    DayContainer(
        modifier = modifier,
        isToday = isToday,
        selected = isSelected,
        onClick = { onDayClicked(day) }
    ) {
        Box(
            Modifier.fillMaxSize()
        ) {
            Text(
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentSize(Alignment.Center),
                text = day.dayOfMonth.toString(),
                color = contentColor,
                style = MaterialTheme.typography.bodyLarge,
            )
            if (!isToday && !isSelected) {
                Row(modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .offset(y = (-3).dp),
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    if (taskIndicator.size > 2) {
                        for((index, task) in taskIndicator.withIndex()) {
                            if(index < 2) {
                                Spacer(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(Color(task.color))
                                )
                            }
                        }
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.size(6.dp)
                        )
                    } else {
                        taskIndicator.forEach { task ->
                            Spacer(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(Color(task.color))
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DayContainer(
    modifier: Modifier = Modifier,
    isToday: Boolean = false,
    selected: Boolean = false,
    onClick: () -> Unit = {},
    backgroundColor: Color = Color.Transparent,
    content: @Composable () -> Unit
) {
    val containerColor = when {
        isToday -> MaterialTheme.colorScheme.primaryContainer
        isToday && selected -> MaterialTheme.colorScheme.primaryContainer
        !isToday && selected -> MaterialTheme.colorScheme.secondaryContainer.copy(0.9f)
        else -> backgroundColor
    }
    Box(
        modifier = modifier
            .size(56.dp)
            .padding(8.dp)
            .clip(CircleShape)
            .pointerInput(Unit) {
                detectTapGestures {
                    onClick()
                }
            }
            .background(containerColor)
    ) {
        content()
    }
}