package com.example.mono.feature.tasks.navigation

import android.app.Activity
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.Bookmarks
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.ui.graphics.vector.ImageVector

enum class BuiltInTaskGroups(
    val groupId: String,
    val groupName: String,
    val icon: ImageVector,
) {
    ALL(
        groupId = (Activity.RESULT_FIRST_USER + 1).toString(),
        groupName = "All Tasks",
        icon = Icons.Outlined.Book,
    ),
    BOOKMARKS(
        groupId = (Activity.RESULT_FIRST_USER + 2).toString(),
        groupName = "Bookmarks",
        icon = Icons.Outlined.Bookmarks
    ),
    REMINDERS(
        groupId = (Activity.RESULT_FIRST_USER + 3).toString(),
        groupName = "Reminders",
        icon = Icons.Outlined.Notifications
    )
}