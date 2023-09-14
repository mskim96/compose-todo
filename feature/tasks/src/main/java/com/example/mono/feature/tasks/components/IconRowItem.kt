package com.example.mono.feature.tasks.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * [IconRowItem] is a Composable for aligning an Icon and Content slot within a single row.
 *
 * @param iconContent Icon content slot.
 * @param onClick Called when the user click the row.
 * @param modifier Modifier to be applied to a row.
 * @param contentPadding content's padding.
 * @param content Row content slot.
 */
@Composable
internal fun IconRowItem(
    iconContent: @Composable () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
    content: @Composable () -> Unit
) {
    Surface(
        modifier
            .fillMaxWidth()
            .clickable { onClick() },
        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .heightIn(min = 56.dp)
                .padding(contentPadding),
            verticalAlignment = Alignment.CenterVertically
        ) {
            iconContent()
            content()
        }
    }
}