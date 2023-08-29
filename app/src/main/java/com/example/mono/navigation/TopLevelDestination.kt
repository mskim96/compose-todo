package com.example.mono.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.CollectionsBookmark
import androidx.compose.material.icons.filled.Today
import androidx.compose.material.icons.outlined.Checklist
import androidx.compose.material.icons.outlined.CollectionsBookmark
import androidx.compose.material.icons.outlined.Today
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
        selectedIcon = Icons.Default.Today,
        unSelectedIcon = Icons.Outlined.Today
    ),
    DIARY(
        selectedIcon = Icons.Default.CollectionsBookmark,
        unSelectedIcon = Icons.Outlined.CollectionsBookmark
    )
}