package com.example.mono.feature.bookmarks.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.example.mono.core.model.Task
import com.example.mono.feature.bookmarks.BookmarksRoute

const val bookmarksRoute = "bookmarks_route"

fun NavController.navigateToBookmarks(navOptions: NavOptions? = null) {
    this.navigate(bookmarksRoute, navOptions)
}

fun NavGraphBuilder.bookmarksScreen(
    onTaskClick: (Task) -> Unit,
    openDrawer: () -> Unit
) {
    composable(route = bookmarksRoute) {
        BookmarksRoute(
            onTaskClick = onTaskClick,
            openDrawer = openDrawer
        )
    }
}