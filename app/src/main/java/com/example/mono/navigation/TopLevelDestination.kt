package com.example.mono.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.StickyNote2
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Checklist
import androidx.compose.material.icons.outlined.StickyNote2
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Type for the top level destination in the application.
 *
 * @param selectedIcon Icon when selected.
 * @param unSelectedIcon Icon when unselected.
 */
enum class TopLevelDestination(
    val selectedIcon: ImageVector,
    val unSelectedIcon: ImageVector,
) {
    TASKS(
        selectedIcon = Icons.Default.Checklist,
        unSelectedIcon = Icons.Outlined.Checklist
    ),
    CALENDAR(
        selectedIcon = Icons.Default.CalendarToday,
        unSelectedIcon = Icons.Outlined.CalendarToday
    ),
    NOTES(
        selectedIcon = Icons.Default.StickyNote2,
        unSelectedIcon = Icons.Outlined.StickyNote2
    )
}