package com.example.mono.feature.tasks.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.mono.feature.tasks.R

@Composable
internal fun TaskDetailBottomAppBar(
    isTaskCompleted: Boolean,
    onTaskCompletedChanged: (Boolean) -> Unit
) {
    val markCompleted = if (isTaskCompleted) {
        stringResource(id = R.string.task_mark_activate)
    } else {
        stringResource(id = R.string.task_mark_completed)
    }

    BottomAppBar(
        windowInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Bottom)
    ) {
        Spacer(modifier = Modifier.weight(1f))
        TextButton(onClick = { onTaskCompletedChanged(!isTaskCompleted) }) {
            Text(
                text = markCompleted,
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}