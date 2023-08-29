package com.example.mono.core.designsystem.component

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import com.example.mono.core.designsystem.theme.MonoTheme

/**
 * Mono outlined text field with included place content slot.
 *
 * @param value text field value.
 * @param onValueChange Called when the user input text into text field.
 * @param modifier Modifier to be applied to the text field.
 * @param textStyle The text style for text field content and placeholder.
 * @param placeholder The placeholder slot for text field.
 */
@Composable
fun MonoOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = MaterialTheme.typography.bodyLarge,
    placeholder: @Composable (() -> Unit)? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        textStyle = textStyle,
        placeholder = if (placeholder != null) {
            {
                ProvideTextStyle(value = textStyle) {
                    placeholder()
                }
            }
        } else {
            null
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
            unfocusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
            focusedBorderColor = MaterialTheme.colorScheme.background,
            unfocusedBorderColor = MaterialTheme.colorScheme.background,
            focusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
            unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    )
}

@Preview(name = "Outlined text field")
@Composable
fun MonoOutlinedTextFieldPreview() {
    MonoTheme {
        Surface {
            MonoOutlinedTextField(
                value = "",
                onValueChange = {},
                placeholder = { Text(text = "Outlined textField") }
            )
        }
    }
}
