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
 * @param modifier Modifier to be applied to the chip.
 * @param onTrailingClick Called when the user clicks the chip's trailing icon.
 * @param label The text label content.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonoInputChip(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    onTrailingClick: (() -> Unit)? = null,
    label: @Composable () -> Unit
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
        trailingIcon = if (onTrailingClick != null) {
            {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = null,
                    modifier = Modifier.clickable(onClick = onTrailingClick)
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
                onTrailingClick = { },
                label = { Text(text = "Input chip preview") }
            )
        }
    }
}