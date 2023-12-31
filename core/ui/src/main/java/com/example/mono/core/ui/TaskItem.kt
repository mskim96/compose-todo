package com.example.mono.core.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Alarm
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mono.core.common.datetime.combineWithTime
import com.example.mono.core.designsystem.component.MonoInputChip
import com.example.mono.core.designsystem.icon.MonoIcons
import com.example.mono.core.designsystem.theme.MonoTheme
import com.example.mono.core.model.task.Task
import java.time.LocalDate
import java.time.LocalTime

@Composable
fun TaskItem(
    task: Task,
    onCheckedChange: (Boolean) -> Unit,
    onTaskClick: (Task) -> Unit,
    toggleBookmark: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier
        .clickable { onTaskClick(task) }
        .padding(vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 4.dp, end = 8.dp),
            verticalAlignment = if (task.detail.isNotEmpty()) Alignment.Top else Alignment.CenterVertically
        ) {
            Checkbox(
                checked = task.isCompleted,
                onCheckedChange = onCheckedChange
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    color = if (task.isCompleted) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.onSurface,
                    textDecoration = if (task.isCompleted) {
                        TextDecoration.LineThrough
                    } else {
                        null
                    },
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 5,
                    style = MaterialTheme.typography.bodyLarge
                )
                if (task.detail.isNotEmpty()) {
                    Text(
                        text = task.detail,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 2,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                task.date?.let {
                    TaskDateTimeChip(
                        date = it,
                        time = task.time,
                        onClick = { /*TODO*/ },
                        isPendingNotification = task.isPendingNotification
                    )
                }
            }

            IconButton(onClick = { toggleBookmark(!task.isBookmarked) }) {
                Icon(
                    imageVector = if (task.isBookmarked) {
                        MonoIcons.Bookmark
                    } else {
                        MonoIcons.BookmarkOutlined
                    },
                    contentDescription = null,
                    tint = if (task.isBookmarked) {
                        MaterialTheme.colorScheme.primaryContainer
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    }
                )
            }
        }
    }
}

@Composable
fun TaskDateTimeChip(
    date: LocalDate,
    time: LocalTime?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isPendingNotification: Boolean = false,
    onTrailingIconClick: (() -> Unit)? = null
) {
    MonoInputChip(
        onClick = onClick,
        label = { Text(text = date.combineWithTime(time)) },
        modifier = modifier,
        leadingIcon = {
            if (isPendingNotification) {
                Icon(
                    imageVector = Icons.Outlined.Alarm, contentDescription = null
                )
            }
        },
        onTrailingIconClick = onTrailingIconClick
    )
}

@Preview(name = "Task full item preview")
@Composable
private fun TaskFullItemPreview() {
    MonoTheme {
        Surface {
            TaskItem(
                task = Task(
                    id = "",
                    title = "Task preview",
                    detail = "Description preview",
                    isCompleted = true,
                    isBookmarked = true,
                    date = LocalDate.of(2023, 8, 24),
                    time = LocalTime.of(16, 42),
                    taskListId = ""
                ),
                toggleBookmark = {},
                onCheckedChange = {},
                onTaskClick = {}
            )
        }
    }
}