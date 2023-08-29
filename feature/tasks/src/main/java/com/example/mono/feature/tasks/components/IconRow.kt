package com.example.mono.feature.tasks.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

/**
 * [IconRow] is a Composable for aligning an Icon and Content slot within a single row.
 *
 * @param icon Icon of the row.
 * @param modifier Modifier to be applied to a row.
 * @param iconContentDescription description of the icon.
 * @param onClickRow Called when the user click the row.
 * @param content Row content slot.
 */
@Composable
internal fun IconRow(
    icon: ImageVector?,
    modifier: Modifier = Modifier,
    iconContentDescription: String? = null,
    onClickRow: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val clickable = if (onClickRow != null) {
        Modifier.clickable(onClick = onClickRow)
    } else {
        Modifier
    }
    CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
        Row(
            modifier = modifier
                .then(clickable)
                .fillMaxWidth()
                .heightIn(min = 56.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = iconContentDescription
                )
            } else {
                Spacer(modifier = Modifier.size(24.dp))
            }
            content()
        }
    }
}