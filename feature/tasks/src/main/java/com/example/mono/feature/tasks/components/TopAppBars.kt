package com.example.mono.feature.tasks.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmarks
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Bookmarks
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.mono.core.designsystem.component.MonoTopAppBar
import com.example.mono.feature.tasks.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasksTopAppBar(
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    openDrawer: () -> Unit,
    navigateToSearch: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior? = null
) {
    MonoTopAppBar(
        title = title,
        modifier = modifier,
        navigationIcon = {
            IconButton(onClick = openDrawer) {
                Icon(imageVector = Icons.Default.Menu, contentDescription = null)
            }
        },
        actions = {
            IconButton(onClick = navigateToSearch) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = stringResource(id = R.string.search)
                )
            }
            IconButton(onClick = { }) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = null
                )
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
    toggleBookmark: () -> Unit,
    onDeleteTask: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bookmarkedIcon = if (isBookmarked) {
        Icons.Default.Bookmarks
    } else {
        Icons.Outlined.Bookmarks
    }

    MonoTopAppBar(
        title = { Text(text = "") },
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
            IconButton(onClick = onDeleteTask) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = null)
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditTaskGroupTopAppBar(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior? = null
) {
    MonoTopAppBar(
        titleRes = R.string.task_group_add_edit,
        modifier = modifier,
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
            }
        },
        scrollBehavior = scrollBehavior
    )
}