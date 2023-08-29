package com.example.mono.core.designsystem.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import com.example.mono.core.designsystem.theme.MonoTheme

/**
 * Mono floating action button.
 *
 * @param icon floating action button icon.
 * @param onClick Called when the user clicks the button.
 * @param modifier Modifier to be applied to the floating action button.
 * @param iconContentDescription icon's content description.
 */
@Composable
fun MonoFloatingButton(
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    iconContentDescription: String? = null
) {
    FloatingActionButton(
        onClick = onClick,
        modifier = modifier,
        shape = FloatingActionButtonDefaults.shape,
        containerColor = FloatingActionButtonDefaults.containerColor,
        contentColor = MaterialTheme.colorScheme.primary,
    ) {
        Icon(imageVector = icon, contentDescription = iconContentDescription)
    }
}

@Preview(name = "Floating action button")
@Composable
private fun MonoFloatingButtonPreview() {
    MonoTheme {
        MonoFloatingButton(
            icon = Icons.Default.Add,
            onClick = { }
        )
    }
}