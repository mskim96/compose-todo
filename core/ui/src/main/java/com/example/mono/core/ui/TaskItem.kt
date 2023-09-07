package com.example.mono.core.ui

import androidx.compose.foundation.clickable
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
import com.example.mono.core.designsystem.theme.MonoTheme
import com.example.mono.core.model.Task
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
    val bookmarkedIcon = if (task.isBookmarked) {
        Icons.Default.Bookmarks
    } else {
        Icons.Outlined.Bookmarks
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 8.dp)
            .clickable { onTaskClick(task) },
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
        }

        IconButton(onClick = { toggleBookmark(!task.isBookmarked) }) {
            Icon(
                imageVector = bookmarkedIcon,
                contentDescription = null
            )
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
                toggleBookmark = { },
                onCheckedChange = { },
                onTaskClick = { }
            )
        }
    }
}