package com.example.mono.feature.detail.navgation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.mono.feature.detail.AttachmentsRoute

internal const val attachmentArgs = "attachment"
internal const val attachmentsRoute = "attachment_route"

fun NavController.navigateToAttachments(
    taskId: String,
    attachment: String
) {
    val url = attachment.replace("/", "&")
    this.navigate("$attachmentsRoute/$taskId/$url")
}

fun NavGraphBuilder.attachmentsScreen(
    onBackClick: (String, String) -> Unit
) {
    composable(
        route = "$attachmentsRoute/{$taskIdArgs}/{$attachmentArgs}",
        arguments = listOf(
            navArgument(taskIdArgs) { type = NavType.StringType },
            navArgument(attachmentArgs) { type = NavType.StringType }
        )
    ) {
        AttachmentsRoute(
            onBackClick = onBackClick
        )
    }
}