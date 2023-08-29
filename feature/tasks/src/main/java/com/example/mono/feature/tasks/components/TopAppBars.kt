package com.example.mono.feature.tasks.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmarks
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Bookmarks
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.mono.core.designsystem.component.MonoTopAppBar
import com.example.mono.feature.tasks.R
import com.example.mono.core.designsystem.R as designSystemR

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasksTopAppBar(
    modifier: Modifier = Modifier,
    navigateToSearch: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior? = null
) {
    MonoTopAppBar(
        titleRes = designSystemR.string.app_name,
        modifier = modifier,
        actions = {
            IconButton(onClick = navigateToSearch) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = stringResource(id = R.string.search)
                )
            }
            IconButton(onClick = { }) {
                // TODO: User profile or setting screen.
                Icon(imageVector = Icons.Default.Settings, contentDescription = null)
            }
        },
        scrollBehavior = scrollBehavior
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun TaskDetailTopAppBar(
    onBackClick: () -> Unit,
    isBookmarked: Boolean,
    updateBookmarked: () -> Unit,
    onDeleteTask: () -> Unit,
    modifier: Modifier = Modifier
) {
    MonoTopAppBar(
        title = "",
        modifier = modifier,
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
            }
        },
        actions = {
            IconButton(onClick = updateBookmarked) {
                val icon =
                    if (isBookmarked) Icons.Default.Bookmarks else Icons.Outlined.Bookmarks
                Icon(
                    imageVector = icon,
                    contentDescription = null
                )
            }
            IconButton(onClick = onDeleteTask) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = null)
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun CreateTaskTopAppBar(
    onBackClick: () -> Unit,
    isBookmarked: Boolean,
    toggleBookmark: () -> Unit,
    createNewTask: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bookmarkedIcon = if (isBookmarked) {
        Icons.Default.Bookmarks
    } else {
        Icons.Outlined.Bookmarks
    }

    MonoTopAppBar(
        title = "",
        modifier = modifier,
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
            }
        },
        actions = {
            IconButton(onClick = toggleBookmark) {
                Icon(
                    imageVector = bookmarkedIcon,
                    contentDescription = null
                )
            }
            TextButton(onClick = createNewTask) {
                Text(text = stringResource(id = R.string.task_save))
            }
        }
    )
}