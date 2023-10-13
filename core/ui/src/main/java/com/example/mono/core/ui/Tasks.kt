package com.example.mono.core.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.Modifier
import com.example.mono.core.model.task.Task

@OptIn(ExperimentalFoundationApi::class)
fun LazyListScope.tasks(
    items: List<Task>,
    onCheckedChange: (Task, Boolean) -> Unit,
    onTaskClick: (Task) -> Unit,
    toggleBookmark: (Task, Boolean) -> Unit,
) = items(
    items = items,
    key = { task -> task.id },
    itemContent = { task ->
        TaskItem(
            task = task,
            onCheckedChange = { onCheckedChange(task, it) },
            onTaskClick = onTaskClick,
            toggleBookmark = { toggleBookmark(task, it) },
            modifier = Modifier.animateItemPlacement()
        )
    }
)