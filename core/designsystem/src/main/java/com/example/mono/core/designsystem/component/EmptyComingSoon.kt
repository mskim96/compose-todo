package com.example.mono.core.designsystem.component

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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mono.core.designsystem.R
import com.example.mono.core.designsystem.icon.MonoIcons
import com.example.mono.core.designsystem.theme.MonoTheme

/**
 * TODO: Delete this composable when all features are implemented.
 * Show this composable while the screen is still under construction and remove it when construction is complete.
 */
@Composable
fun EmptyComingSoon(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(id = R.string.coming_screen_title),
            modifier = Modifier.padding(8.dp),
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            modifier = Modifier.padding(horizontal = 8.dp),
            text = stringResource(id = R.string.coming_screen_subtitle),
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.outline
        )
    }
}

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
            modifier = Modifier.size(96.dp),
            tint = MaterialTheme.colorScheme.secondaryContainer
        )
        Text(
            text = stringResource(id = noTasksLabel),
            modifier = Modifier.padding(top = 24.dp)
        )
    }
}

@Preview(name = "Coming soon screen")
@Composable
fun ComingSoonPreview() {
    MonoTheme {
        Surface {
            EmptyComingSoon()
        }
    }
}

@Preview(name = "Tasks empty content preview")
@Composable
fun TasksEmptyContentPreview() {
    MonoTheme {
        Surface {
            TasksEmptyContent(
                noTaskIcon = MonoIcons.Bookmark,
                noTasksLabel = R.string.no_tasks_bookmark
            )
        }
    }
}