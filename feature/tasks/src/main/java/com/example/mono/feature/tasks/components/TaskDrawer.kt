package com.example.mono.feature.tasks.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MenuOpen
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mono.core.designsystem.component.MonoModalSheet
import com.example.mono.core.designsystem.component.MonoNavigationDrawerItem
import com.example.mono.core.designsystem.theme.MonoTheme
import com.example.mono.core.model.TaskGroup
import com.example.mono.feature.tasks.navigation.BuiltInTaskGroups

@Composable
fun TaskModalDrawerContent(
    selectedGroupId: String,
    builtInTaskGroups: List<BuiltInTaskGroups>,
    taskGroups: List<TaskGroup>,
    navigateToTaskGroup: (groupId: String) -> Unit,
    navigateToAddEditTaskGroup: () -> Unit,
    onDrawerClicked: () -> Unit,
) {
    MonoModalSheet {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Mono",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold
            )
            IconButton(onClick = onDrawerClicked) {
                Icon(
                    imageVector = Icons.Default.MenuOpen,
                    contentDescription = null
                )
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 8.dp)
        ) {
            items(builtInTaskGroups) { builtInTaskGroup ->
                MonoNavigationDrawerItem(
                    label = {
                        Text(text = builtInTaskGroup.groupName)
                    },
                    selected = selectedGroupId == builtInTaskGroup.groupId,
                    onClick = { navigateToTaskGroup(builtInTaskGroup.groupId) },
                    icon = {
                        Icon(imageVector = builtInTaskGroup.icon, contentDescription = null)
                    }
                )
            }

            item {
                Divider(modifier = Modifier.padding(16.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 8.dp, end = 8.dp, bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Groups",
                        style = MaterialTheme.typography.labelLarge
                    )
                    if (taskGroups.isNotEmpty()) {
                        TextButton(onClick = navigateToAddEditTaskGroup) {
                            Text(text = "Edit")
                        }
                    }
                }
            }

            items(
                items = taskGroups,
                key = { it.id }
            ) { taskGroup ->
                MonoNavigationDrawerItem(
                    label = { Text(text = taskGroup.name) },
                    selected = selectedGroupId == taskGroup.id,
                    onClick = { navigateToTaskGroup(taskGroup.id) },
                    icon = {
                        Icon(imageVector = Icons.Outlined.Folder, contentDescription = null)
                    }
                )
            }

            item {
                MonoNavigationDrawerItem(
                    label = { Text(text = "Create new group") },
                    selected = false,
                    onClick = navigateToAddEditTaskGroup,
                    icon = { Icon(imageVector = Icons.Default.Add, contentDescription = null) }
                )
            }
        }
    }
}

@Preview(name = "Task modal drawer preview")
@Composable
fun TaskModalDrawerPreview() {
    MonoTheme {
        TaskModalDrawerContent(
            selectedGroupId = BuiltInTaskGroups.ALL.groupId,
            builtInTaskGroups = BuiltInTaskGroups.values().toList(),
            taskGroups = listOf(
                TaskGroup("10", "Preview1"),
                TaskGroup("11", "Preview2"),
                TaskGroup("12", "Preview3"),
                TaskGroup("13", "Preview4"),
            ),
            navigateToTaskGroup = {},
            navigateToAddEditTaskGroup = {},
            onDrawerClicked = {},
        )
    }
}