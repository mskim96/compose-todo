package com.example.mono.navigation

import androidx.compose.ui.graphics.vector.ImageVector
import com.example.mono.core.designsystem.icon.MonoIcons
import com.example.mono.feature.bookmarks.R as bookmarksR
import com.example.mono.feature.calendar.R as calendarR
import com.example.mono.feature.reminders.R as remindersR
import com.example.mono.feature.settings.R as settingsR
import com.example.mono.feature.tasklist.R as taskListR
import com.example.mono.feature.tasks.R as taskR

/**
 * Type for the top level destination in the application.
 */
enum class TopLevelDestination(
    val icon: ImageVector,
    val titleTextId: Int
) {
    TASKS(
        icon = MonoIcons.Tasks,
        titleTextId = taskR.string.tasks
    ),
    BOOKMARKS(
        icon = MonoIcons.BookmarksOutlined,
        titleTextId = bookmarksR.string.bookmarks
    ),
    REMINDERS(
        icon = MonoIcons.Reminder,
        titleTextId = remindersR.string.reminders
    ),
    CALENDAR(
        icon = MonoIcons.Calendar,
        titleTextId = calendarR.string.calendar
    ),
    CREATE_LIST(
        icon = MonoIcons.Add,
        titleTextId = taskListR.string.create_new_task_list
    ),
    SETTINGS(
        icon = MonoIcons.Settings,
        titleTextId = settingsR.string.settings
    )
}