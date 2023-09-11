package com.example.mono.core.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmarks
import androidx.compose.material.icons.outlined.Bookmarks
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mono.core.common.datetime.toFormattedDate
import com.example.mono.core.common.datetime.toFormattedTime
import com.example.mono.core.designsystem.component.MonoInputChip
import com.example.mono.core.designsystem.theme.MonoTheme
import com.example.mono.core.model.Task
import java.time.LocalDate
import java.time.LocalTime

@Composable
internal fun TaskItem(
    task: Task,
    onCheckedChange: (Boolean) -> Unit,
    onTaskClick: (Task) -> Unit,
    toggleBookmark: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val formattedDate = task.date?.let(LocalDate::toFormattedDate)
    val formattedTime = task.time?.let(LocalTime::toFormattedTime)

    val bookmarkedIcon = if (task.isBookmarked) {
        Icons.Default.Bookmarks
    } else {
        Icons.Outlined.Bookmarks
    }

    Box(modifier = modifier.clickable { onTaskClick(task) }) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            verticalAlignment = if (task.description.isNotEmpty()) Alignment.Top else Alignment.CenterVertically
        ) {
            Checkbox(
                checked = task.isCompleted,
                onCheckedChange = onCheckedChange
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.bodyLarge,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 5
                )
                if (task.description.isNotEmpty()) {
                    Text(
                        text = task.description,
                        style = MaterialTheme.typography.bodyMedium,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 2
                    )
                }
                formattedDate?.let { date ->
                    MonoInputChip(
                        label = {
                            Text(text = formattedTime?.let { time -> "$date $time" } ?: date)
                        },
                        onClick = {}
                    )
                }
            }

            IconButton(onClick = { toggleBookmark(!task.isBookmarked) }) {
                Icon(
                    imageVector = bookmarkedIcon,
                    contentDescription = null
                )
            }
        }
    }
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
                    description = "Description preview",
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