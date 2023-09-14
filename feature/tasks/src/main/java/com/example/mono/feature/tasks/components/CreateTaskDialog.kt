package com.example.mono.feature.tasks.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Bookmarks
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.outlined.Bookmarks
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mono.core.common.datetime.toFormattedDate
import com.example.mono.core.common.datetime.toFormattedTime
import com.example.mono.core.designsystem.component.MonoInputChip
import com.example.mono.core.designsystem.component.MonoTextField
import com.example.mono.core.designsystem.theme.MonoTheme
import java.time.LocalDate
import java.time.LocalTime

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun CreateTaskDialog(
    onDismiss: () -> Unit,
    onCreateTask: (title: String, description: String, isBookmarked: Boolean, date: LocalDate?, time: LocalTime?) -> Unit,
    modifier: Modifier = Modifier,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
) {
    val (titleRef, descriptionRef) = remember { FocusRequester.createRefs() }

    var showDescription by rememberSaveable { mutableStateOf(false) }
    var showDateDialog by remember { mutableStateOf(false) }

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var bookmark by remember { mutableStateOf(false) }
    var date: LocalDate? by remember { mutableStateOf(null) }
    var time: LocalTime? by remember { mutableStateOf(null) }

    val formattedDate = date?.let(LocalDate::toFormattedDate)
    val formattedTime = time?.let(LocalTime::toFormattedTime)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        modifier = modifier,
        sheetState = sheetState,
        dragHandle = null,
        windowInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Bottom)
    ) {
        // TODO: The animation for hiding the IME when the bottom sheet dialog transitions to the 'hide' state is not functioning.
        // This needs to be rechecked when there are future API changes.
        DisposableEffect(Unit) {
            titleRef.requestFocus()
            onDispose {}
        }
        LaunchedEffect(showDescription) {
            if (showDescription) {
                descriptionRef.requestFocus()
            }
        }

        MonoTextField(
            value = title,
            onValueChange = { title = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .focusRequester(titleRef),
            placeholder = { Text(text = "New task") }
        )

        AnimatedVisibility(visible = showDescription) {
            MonoTextField(
                value = description,
                onValueChange = { description = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .focusRequester(descriptionRef),
                textStyle = MaterialTheme.typography.labelLarge,
                placeholder = { Text(text = "Add description") }
            )
        }

        AnimatedContent(
            targetState = formattedDate,
            label = "create_date"
        ) { it ->
            it?.let {
                MonoInputChip(
                    label = { Text(text = formattedTime?.let { time -> "$it $time" } ?: it) },
                    onClick = { showDateDialog = true },
                    onTrailingIconClick = { date = null; time = null },
                    modifier = Modifier.padding(start = 24.dp)
                )
            }
        }

        CreateTaskDialogButtonRow(
            isBookmarked = bookmark,
            onDismiss = onDismiss,
            showDescription = { showDescription = true },
            showDateDialog = { showDateDialog = true },
            toggleBookmark = { bookmark = it },
            onCreateTask = { onCreateTask(title, description, bookmark, date, time) },
            validateCreateTask = { title.isNotEmpty() || description.isNotEmpty() }
        )

        if (showDateDialog) {
            MonoDateTimePicker(
                onDismiss = { showDateDialog = false },
                onConfirm = { setDate, setTime ->
                    date = setDate
                    time = setTime
                },
                previousDate = date,
                previousTime = time
            )
        }
    }
}

@Composable
private fun CreateTaskDialogButtonRow(
    isBookmarked: Boolean,
    onDismiss: () -> Unit,
    showDescription: () -> Unit,
    showDateDialog: () -> Unit,
    toggleBookmark: (Boolean) -> Unit,
    onCreateTask: () -> Unit,
    validateCreateTask: () -> Boolean,
    modifier: Modifier = Modifier
) {
    val bookmarkIcon = if (isBookmarked) {
        Icons.Default.Bookmarks
    } else {
        Icons.Outlined.Bookmarks
    }

    Row(
        modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    ) {
        IconButton(onClick = showDescription) {
            Icon(imageVector = Icons.Default.Sort, contentDescription = null)
        }
        IconButton(onClick = showDateDialog) {
            Icon(imageVector = Icons.Default.AccessTime, contentDescription = null)
        }
        IconButton(onClick = { toggleBookmark(!isBookmarked) }) {
            Icon(
                imageVector = bookmarkIcon, contentDescription = null
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        TextButton(
            onClick = { onCreateTask(); onDismiss() },
            enabled = validateCreateTask()
        ) {
            Text(text = "Save", style = MaterialTheme.typography.titleMedium)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun CreateTaskDialogPreview() {
    MonoTheme {
        CreateTaskDialog(
            onDismiss = {},
            onCreateTask = { _, _, _, _, _-> },
            sheetState = rememberStandardBottomSheetState(
                initialValue = SheetValue.Expanded
            )
        )
    }
}