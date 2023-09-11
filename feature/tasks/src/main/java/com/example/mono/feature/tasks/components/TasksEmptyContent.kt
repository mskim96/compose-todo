package com.example.mono.feature.tasks.components

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Bookmarks
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mono.core.designsystem.theme.MonoTheme
import com.example.mono.feature.tasks.R

@Composable
fun TasksEmptyContent(
    noTaskIcon: ImageVector,
    @StringRes noTasksLabel: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = noTaskIcon,
            contentDescription = stringResource(id = noTasksLabel),
            modifier = Modifier.size(96.dp)
        )
        Text(
            text = stringResource(id = noTasksLabel),
            modifier = Modifier.padding(top = 24.dp)
        )
    }
}

@Preview(name = "Tasks empty content preview")
@Composable
fun TasksEmptyContentPreview() {
    MonoTheme {
        Surface {
            TasksEmptyContent(
                noTaskIcon = Icons.Outlined.Bookmarks,
                noTasksLabel = R.string.no_tasks_bookmark
            )
        }
    }
}