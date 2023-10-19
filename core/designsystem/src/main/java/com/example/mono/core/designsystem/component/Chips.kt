package com.example.mono.core.designsystem.component

import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChip
import androidx.compose.material3.InputChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.mono.core.designsystem.theme.MonoTheme

/**
 * Mono Input chip with included trailing icon and text content slot.
 *
 * @param onClick Called when the user clicks the chip.
 * @param label The text label content.
 * @param modifier Modifier to be applied to the chip.
 * @param onTrailingIconClick Called when the user clicks the chip's trailing icon.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonoInputChip(
    onClick: () -> Unit,
    label: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    leadingIcon: (@Composable () -> Unit)? = null,
    onTrailingIconClick: (() -> Unit)? = null,
) {
    InputChip(
        selected = false,
        onClick = onClick,
        label = {
            ProvideTextStyle(value = MaterialTheme.typography.labelLarge) {
                label()
            }
        },
        modifier = modifier,
        leadingIcon = leadingIcon,
        trailingIcon = if (onTrailingIconClick != null) {
            {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = null,
                    modifier = Modifier.clickable(onClick = onTrailingIconClick)
                )
            }
        } else {
            null
        },
        shape = MaterialTheme.shapes.extraSmall,
        colors = InputChipDefaults.inputChipColors()
    )
}

@Preview(name = "Input chip")
@Composable
private fun MonoInputChipPreview() {
    MonoTheme {
        Surface {
            MonoInputChip(
                onClick = { },
                onTrailingIconClick = { },
                label = { Text(text = "Input chip preview") }
            )
        }
    }
}