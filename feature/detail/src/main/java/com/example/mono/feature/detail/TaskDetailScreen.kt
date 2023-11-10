package com.example.mono.feature.detail

import android.Manifest
import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.SubdirectoryArrowRight
import androidx.compose.material.icons.outlined.AddBox
import androidx.compose.material.icons.outlined.Attachment
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.MicNone
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.mono.core.designsystem.component.MonoIconDropdownMenu
import com.example.mono.core.designsystem.component.MonoNoPaddingTextField
import com.example.mono.core.designsystem.component.MonoTextField
import com.example.mono.core.designsystem.component.MonoTopAppBar
import com.example.mono.core.designsystem.icon.MonoIcons
import com.example.mono.core.model.task.SubTask
import com.example.mono.core.model.task.TaskColorPalette
import com.example.mono.core.model.task.TaskList
import com.example.mono.core.ui.IconRowItem
import com.example.mono.core.ui.MonoDateTimePicker
import com.example.mono.core.ui.TaskDateTimeChip
import com.example.mono.feature.detail.recorder.AudioRecordImpl
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import java.io.File
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalTime
import java.util.Date
import java.util.Objects.requireNonNull

@OptIn(ExperimentalComposeUiApi::class)
@Composable
internal fun TaskDetailRoute(
    onBackClick: () -> Unit,
    navigateToAttachment: (taskId: String, attachment: String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TaskDetailViewModel = hiltViewModel()
) {
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    val savedUpdateBackClick = {
        focusManager.clearFocus()
        keyboardController?.hide()
        viewModel.saveTask(onBackClick)
    }

    val taskDetailUiState by viewModel.taskUiState.collectAsStateWithLifecycle()
    val subTaskUiState by viewModel.subTaskUiState.collectAsStateWithLifecycle()
    val taskListUiState by viewModel.taskListUiState.collectAsStateWithLifecycle()

    TaskDetailScreen(
        taskId = viewModel.taskId,
        taskDetailUiState = taskDetailUiState,
        subTaskUiState = subTaskUiState,
        taskListUiState = taskListUiState,
        onBackClick = savedUpdateBackClick,
        onDeleteTask = viewModel::deleteTask,
        onTitleChanged = viewModel::updateTitle,
        onDetailChanged = viewModel::updateDetail,
        onDateTimeSelected = viewModel::updateDateTime,
        onTaskColorChanged = viewModel::updateTaskColor,
        onTaskCompletedChanged = viewModel::updateTaskCompleted,
        onTaskBookmarkChanged = viewModel::updateTaskBookmark,
        onTaskListSelected = viewModel::updateTaskList,
        onCreateSubTask = viewModel::createSubTask,
        onSubTaskTitleChanged = viewModel::editSubTask,
        onSubTaskCompletedChanged = viewModel::updateSubTaskComplete,
        onDeleteSubTask = viewModel::deleteSubTask,
        onAttachmentSelected = viewModel::updateAttachment,
        onAttachmentFromCamera = viewModel::updateAttachmentFromCamera,
        onRecordCreated = viewModel::createRecord,
        navigateToAttachment = navigateToAttachment,
        onDeleteRecord = viewModel::deleteRecord,
        modifier = modifier
    )

    BackHandler {
        savedUpdateBackClick()
    }

    LaunchedEffect(taskDetailUiState.isTaskSaved, taskDetailUiState.isTaskDeleted) {
        when {
            taskDetailUiState.isTaskSaved -> savedUpdateBackClick()
            taskDetailUiState.isTaskDeleted -> onBackClick()
        }
    }

    LaunchedEffect(viewModel.deleteAttachmentId) {
        if (viewModel.deleteAttachmentId.value.isNotEmpty()) {
            viewModel.deleteAttachment(viewModel.deleteAttachmentId.value)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
internal fun TaskDetailScreen(
    taskId: String,
    taskDetailUiState: TaskDetailUiState,
    subTaskUiState: SubTaskUiState,
    taskListUiState: TaskListUiState,
    onBackClick: () -> Unit,
    onDeleteTask: () -> Unit,
    onTitleChanged: (title: String) -> Unit,
    onDetailChanged: (detail: String) -> Unit,
    onDateTimeSelected: (date: LocalDate?, time: LocalTime?) -> Unit,
    onTaskColorChanged: (colorCode: Long) -> Unit,
    onTaskCompletedChanged: (Boolean) -> Unit,
    onTaskBookmarkChanged: (Boolean) -> Unit,
    onTaskListSelected: (TaskList?) -> Unit,
    onCreateSubTask: () -> Unit,
    onSubTaskCompletedChanged: (subTask: SubTask, isCompleted: Boolean) -> Unit,
    onSubTaskTitleChanged: (subTaskId: String, title: String) -> Unit,
    onDeleteSubTask: (subtaskId: String) -> Unit,
    onAttachmentSelected: (List<String>) -> Unit,
    onAttachmentFromCamera: (String) -> Unit,
    navigateToAttachment: (String, String) -> Unit,
    onRecordCreated: (String) -> Unit,
    onDeleteRecord: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    var showDeleteTaskDialog by remember { mutableStateOf(false) }
    var showTaskListDialog by rememberSaveable { mutableStateOf(false) }
    var showAttachmentDialog by remember { mutableStateOf(false) }
    var showRecordDialog by remember { mutableStateOf(false) }

    val file = context.createImageFile()
    val uri = FileProvider.getUriForFile(
        requireNonNull(context),
        context.packageName + ".provider", file
    )

    val cameraLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.TakePicture()) {
            if (it) onAttachmentFromCamera(uri.toString())
        }

    val cameraPermissionState = rememberPermissionState(
        Manifest.permission.CAMERA,
    ) {
        if (it) {
            cameraLauncher.launch(uri)
        }
    }

    val recordPermissionState = rememberPermissionState(
        Manifest.permission.RECORD_AUDIO
    ) {
        if (it) {
            showRecordDialog = true
        }
    }

    val imageLoaderLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetMultipleContents()) { urlList ->
            val imageUrls = urlList.mapIndexed { index, uri ->
                val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
                val input = context.contentResolver.openInputStream(uri)
                    ?: return@rememberLauncherForActivityResult
                val outputFile = context.filesDir.resolve("JPEG_" + timeStamp + "_" + index)
                input.copyTo(outputFile.outputStream())
                input.close()
                outputFile.toUri().toString()
            }
            onAttachmentSelected(imageUrls)
        }

    val loadImagesPermissionState = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        rememberPermissionState(
            Manifest.permission.READ_MEDIA_IMAGES,
        ) {
            if (it) imageLoaderLauncher.launch("image/*")
        }
    } else {
        rememberPermissionState(
            Manifest.permission.READ_EXTERNAL_STORAGE,
        ) {
            if (it) imageLoaderLauncher.launch("image/*")
        }
    }

    Scaffold(
        topBar = {
            TaskDetailTopAppBar(
                isBookmarked = taskDetailUiState.isTaskBookmarked,
                onBackClick = onBackClick,
                onBookmarkChange = { onTaskBookmarkChanged(!taskDetailUiState.isTaskBookmarked) },
                onDeleteTask = {
                    if (subTaskUiState is SubTaskUiState.Success) {
                        if (subTaskUiState.subTasks.isNotEmpty()) showDeleteTaskDialog =
                            true else onDeleteTask()
                    }
                }
            )
        },
        bottomBar = {
            AnimatedVisibility(
                visible = !showTaskListDialog,
                enter = slideInVertically { it },
                exit = slideOutVertically { it }
            ) {
                BottomAppBar(
                    actions = {
                        IconButton(onClick = { showAttachmentDialog = true }) {
                            Icon(imageVector = Icons.Outlined.AddBox, contentDescription = null)
                        }
                    },
                    floatingActionButton = {
                        TextButton(onClick = { onTaskCompletedChanged(!taskDetailUiState.isTaskCompleted) }) {
                            Text(
                                text = if (taskDetailUiState.isTaskCompleted) {
                                    stringResource(id = R.string.task_mark_activate)
                                } else {
                                    stringResource(id = R.string.task_mark_completed)
                                },
                                style = MaterialTheme.typography.titleSmall,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                        }
                    },
                    windowInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Bottom)
                )
            }
        }
    ) { padding ->
        TaskDetailContent(
            taskId = taskId,
            taskDetailUiState = taskDetailUiState,
            subTaskUiState = subTaskUiState,
            taskListUiState = taskListUiState,
            onTitleChanged = onTitleChanged,
            onDetailChanged = onDetailChanged,
            onDateTimeSelected = onDateTimeSelected,
            onTaskColorChanged = onTaskColorChanged,
            showTaskListDialog = { showTaskListDialog = true },
            onCreateSubTask = onCreateSubTask,
            onSubTaskCompletedChanged = onSubTaskCompletedChanged,
            onSubTaskTitleChanged = onSubTaskTitleChanged,
            onDeleteSubTask = onDeleteSubTask,
            navigateToAttachment = navigateToAttachment,
            onDeleteRecord = onDeleteRecord,
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
        )
    }

    if (showAttachmentDialog) {
        AttachmentsBottomSheet(
            onDismiss = { showAttachmentDialog = false },
            onTakePhoto = {
                showAttachmentDialog =
                    if (cameraPermissionState.status is PermissionStatus.Denied) {
                        cameraPermissionState.launchPermissionRequest()
                        false
                    } else {
                        cameraLauncher.launch(uri)
                        false
                    }
            },
            onImageSelected = {
                val status = loadImagesPermissionState.status
                showAttachmentDialog =
                    if (status is PermissionStatus.Denied && !status.shouldShowRationale) {
                        loadImagesPermissionState.launchPermissionRequest()
                        false
                    } else {
                        imageLoaderLauncher.launch("image/*")
                        false
                    }
            },
            onDrawCanvas = {

            },
            onRecordVoice = {
                val status = recordPermissionState.status
                if (status is PermissionStatus.Denied && !status.shouldShowRationale) {
                    recordPermissionState.launchPermissionRequest()
                    showAttachmentDialog = false
                } else {
                    showRecordDialog = true
                    showAttachmentDialog = false
                }
            }
        )
    }

    if (showDeleteTaskDialog) {
        DeleteTaskDialog(
            onDismiss = { showDeleteTaskDialog = false },
            onDeleteTask = onDeleteTask
        )
    }
    if (showTaskListDialog) {
        SelectTaskListDialog(
            currentTaskList = taskDetailUiState.taskList,
            taskListUiState = taskListUiState,
            onDismiss = { showTaskListDialog = false },
            onConfirm = onTaskListSelected
        )
    }

    if (showRecordDialog) {
        RecordDialog(
            onDismiss = { showRecordDialog = false },
            saveRecordUri = onRecordCreated
        )
    }
}

@Composable
internal fun TaskDetailContent(
    taskId: String,
    taskDetailUiState: TaskDetailUiState,
    subTaskUiState: SubTaskUiState,
    taskListUiState: TaskListUiState,
    onTitleChanged: (title: String) -> Unit,
    onDetailChanged: (description: String) -> Unit,
    onDateTimeSelected: (date: LocalDate?, time: LocalTime?) -> Unit,
    onTaskColorChanged: (colorCode: Long) -> Unit,
    showTaskListDialog: () -> Unit,
    onCreateSubTask: () -> Unit,
    onSubTaskCompletedChanged: (subTask: SubTask, isCompleted: Boolean) -> Unit,
    onSubTaskTitleChanged: (subTaskId: String, title: String) -> Unit,
    onDeleteSubTask: (subTaskId: String) -> Unit,
    navigateToAttachment: (String, String) -> Unit,
    onDeleteRecord: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showDateDialog by rememberSaveable { mutableStateOf(false) }
    val attachmentsState = rememberLazyListState()

    if (showDateDialog) {
        MonoDateTimePicker(
            previousDate = taskDetailUiState.date,
            previousTime = taskDetailUiState.time,
            onDismiss = { showDateDialog = false },
            onConfirm = onDateTimeSelected
        )
    }

    Column(modifier = modifier.verticalScroll(rememberScrollState())) {
        SelectTaskListButton(
            selectedTaskList = taskDetailUiState.taskList,
            taskListUiState = taskListUiState,
            openTaskListDialog = showTaskListDialog
        )
        TaskTitle(
            title = taskDetailUiState.title,
            onTitleChanged = onTitleChanged,
            modifier = Modifier.fillMaxWidth()
        )
        TaskDetail(
            detail = taskDetailUiState.detail,
            onDetailChanged = onDetailChanged,
            modifier = Modifier.fillMaxWidth()
        )
        TaskDateTime(
            date = taskDetailUiState.date,
            time = taskDetailUiState.time,
            openDateTimeDialog = { showDateDialog = true },
            onDeleteDateTimeSelected = { onDateTimeSelected(null, null) }
        )
        IconRowItem(
            iconContent = {
                Icon(imageVector = Icons.Outlined.Palette, contentDescription = null)
            },
            onClick = {},
            onClickEnabled = false,
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 40.dp)
                    .padding(start = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                items(TaskColorPalette.values()) {
                    val selected = it.color == taskDetailUiState.color
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(color = Color(it.color))
                            .clickable {
                                onTaskColorChanged(it.color)
                            }
                    ) {
                        if (selected) {
                            Icon(
                                imageVector = Icons.Outlined.Check,
                                contentDescription = null,
                                modifier = Modifier.align(Alignment.Center),
                                tint = Color.White
                            )
                        }
                    }
                }
            }
        }
        AddEditSubTask(
            subTaskUiState = subTaskUiState,
            onCreateSubTask = onCreateSubTask,
            onSubTaskCompletedChanged = onSubTaskCompletedChanged,
            onSubTaskTitleChanged = onSubTaskTitleChanged,
            onDeleteSubTask = onDeleteSubTask
        )
        Spacer(modifier = Modifier.height(16.dp))
        if (taskDetailUiState.attachments.isNotEmpty()) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(56.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Attachment,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    LazyRow(
                        state = attachmentsState,
                        modifier = Modifier.heightIn(max = 200.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        items(taskDetailUiState.attachments) { attachment ->
                            val url = Uri.parse(attachment)
                            AsyncImage(
                                model = url,
                                contentDescription = null,
                                modifier = Modifier.clickable {
                                    navigateToAttachment(taskId, attachment)
                                }
                            )
                        }
                    }
                }
            }
        }
        if (taskDetailUiState.recorders.isNotEmpty()) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(56.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.MicNone,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    taskDetailUiState.recorders.forEach {
                        key(it) {
                            RecordPlayer(
                                fileUrl = it,
                                onDeleteRecord = { onDeleteRecord(it) }
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

@Composable
fun SelectTaskListButton(
    selectedTaskList: TaskList?,
    taskListUiState: TaskListUiState,
    openTaskListDialog: () -> Unit,
    modifier: Modifier = Modifier
) {
    TextButton(
        onClick = openTaskListDialog,
        modifier = modifier.padding(start = 4.dp),
        enabled = if (taskListUiState is TaskListUiState.Success) {
            taskListUiState.taskLists.isNotEmpty()
        } else {
            false
        }
    ) {
        Text(text = selectedTaskList?.name ?: stringResource(id = R.string.no_task_list))
        Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = null)
    }
}

@Composable
fun TaskTitle(
    title: String,
    onTitleChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    MonoTextField(
        value = title,
        onValueChange = onTitleChanged,
        modifier = modifier,
        textStyle = MaterialTheme.typography.headlineSmall,
        placeholder = { Text(text = stringResource(id = R.string.placeholder_title)) }
    )
}

@Composable
fun TaskDetail(
    detail: String,
    onDetailChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    IconRowItem(
        iconContent = { Icon(imageVector = MonoIcons.Sort, contentDescription = null) },
        onClick = {},
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        MonoTextField(
            value = detail,
            onValueChange = onDetailChanged,
            modifier = modifier,
            placeholder = { Text(text = stringResource(id = R.string.placeholder_description)) }
        )
    }
}

@Composable
fun TaskDateTime(
    date: LocalDate?,
    time: LocalTime?,
    openDateTimeDialog: () -> Unit,
    onDeleteDateTimeSelected: () -> Unit,
    modifier: Modifier = Modifier
) {
    IconRowItem(
        iconContent = {
            Icon(
                imageVector = MonoIcons.Clock,
                contentDescription = null
            )
        },
        onClick = openDateTimeDialog,
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        AnimatedContent(targetState = date, label = "dateTime") { date ->
            if (date != null) {
                TaskDateTimeChip(
                    date = date,
                    time = time,
                    onClick = openDateTimeDialog,
                    modifier = Modifier.padding(start = 16.dp),
                    onTrailingIconClick = onDeleteDateTimeSelected
                )
            } else {
                Text(
                    text = stringResource(id = R.string.placeholder_date_time),
                    modifier = Modifier.padding(start = 16.dp)
                )
            }
        }
    }
}

@Composable
fun AddEditSubTask(
    subTaskUiState: SubTaskUiState,
    onCreateSubTask: () -> Unit,
    onSubTaskCompletedChanged: (subTask: SubTask, isCompleted: Boolean) -> Unit,
    onSubTaskTitleChanged: (subTaskId: String, title: String) -> Unit,
    onDeleteSubTask: (subTaskId: String) -> Unit
) {
    when (subTaskUiState) {
        SubTaskUiState.Loading -> Unit
        is SubTaskUiState.Success -> {
            if (subTaskUiState.subTasks.isEmpty()) {
                AddSubListButton(
                    iconEnabled = true,
                    onCreateSubTask = onCreateSubTask,
                    modifier = Modifier,
                    contentPadding = PaddingValues(start = 16.dp)
                )
            } else {
                SubTasks(
                    subTasks = subTaskUiState.subTasks,
                    onSubTaskCompletedChanged = onSubTaskCompletedChanged,
                    onSubTaskTitleChanged = onSubTaskTitleChanged,
                    onDeleteSubTask = onDeleteSubTask
                )
                AddSubListButton(
                    iconEnabled = false,
                    onCreateSubTask = onCreateSubTask,
                    modifier = Modifier
                )
            }
        }
    }
}

@Composable
fun AddSubListButton(
    iconEnabled: Boolean,
    onCreateSubTask: () -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues()
) {
    val iconPadding = if (!iconEnabled) 20.dp else 0.dp
    IconRowItem(
        iconContent = {
            if (iconEnabled) {
                Icon(
                    imageVector = Icons.Default.SubdirectoryArrowRight,
                    contentDescription = null
                )
            }
        },
        onClick = onCreateSubTask,
        modifier = modifier.padding(horizontal = iconPadding),
        contentPadding = contentPadding
    ) {
        Text(
            text = stringResource(id = R.string.add_sub_tasks),
            modifier = Modifier.padding(start = 16.dp)
        )
    }
}

@Composable
fun SubTasks(
    subTasks: List<SubTask>,
    onSubTaskCompletedChanged: (subTask: SubTask, isCompleted: Boolean) -> Unit,
    onSubTaskTitleChanged: (subTaskId: String, title: String) -> Unit,
    onDeleteSubTask: (subTaskId: String) -> Unit
) {
    subTasks.forEach { subTask ->
        key(subTask.id) {
            Row(
                modifier = Modifier
                    .heightIn(min = 56.dp)
                    .padding(start = 16.dp)
            ) {
                if (subTasks.first() == subTask) {
                    Icon(
                        imageVector = Icons.Default.SubdirectoryArrowRight,
                        contentDescription = null,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                } else {
                    Spacer(modifier = Modifier.size(24.dp))
                }

                SubTaskItem(
                    subTask = subTask,
                    onCompletedChanged = { isCompleted ->
                        onSubTaskCompletedChanged(
                            subTask,
                            isCompleted
                        )
                    },
                    onUpdateSubTask = onSubTaskTitleChanged,
                    onDeleteSubTask = { onDeleteSubTask(subTask.id) }
                )
            }
        }
    }
}

@Composable
fun SubTaskItem(
    subTask: SubTask,
    onCompletedChanged: (Boolean) -> Unit,
    onDeleteSubTask: () -> Unit,
    onUpdateSubTask: (subTaskId: String, title: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var isFocused by remember { mutableStateOf(false) }
    var title by remember { mutableStateOf(subTask.title) }

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = subTask.isCompleted,
            onCheckedChange = { onCompletedChanged(!subTask.isCompleted) },
            modifier = Modifier
        )
        MonoNoPaddingTextField(
            value = title,
            onValueChange = { title = it },
            modifier = Modifier
                .weight(1f)
                .onFocusChanged { isFocused = it.isFocused },
            placeholder = { Text(text = stringResource(id = R.string.placeholder_subtask_title)) }
        )
        if (isFocused) {
            IconButton(
                onClick = onDeleteSubTask,
                modifier = Modifier.padding(end = 8.dp)
            ) {
                Icon(imageVector = Icons.Default.Close, contentDescription = null)
            }
        } else {
            Spacer(
                modifier = Modifier
                    .size(48.dp)
                    .padding(end = 8.dp)
            )
        }
    }
    LaunchedEffect(isFocused) {
        if (!isFocused && subTask.title != title) {
            onUpdateSubTask(subTask.id, title)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectTaskListDialog(
    currentTaskList: TaskList?,
    taskListUiState: TaskListUiState,
    onDismiss: () -> Unit,
    onConfirm: (taskList: TaskList?) -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberStandardBottomSheetState(
            initialValue = SheetValue.Expanded,
            skipHiddenState = false
        ),
        dragHandle = null,
        windowInsets = WindowInsets.ime.only(WindowInsetsSides.Bottom)
    ) {
        when (taskListUiState) {
            TaskListUiState.Loading -> Unit
            is TaskListUiState.Success -> {
                LazyColumn(modifier = Modifier.padding(16.dp)) {
                    item {
                        Text(
                            text = stringResource(id = R.string.move_task_list),
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                    item {
                        IconRowItem(
                            iconContent = {
                                if (currentTaskList == null) Icon(
                                    imageVector = MonoIcons.Check,
                                    contentDescription = null
                                ) else {
                                    Spacer(modifier = Modifier.size(24.dp))
                                }
                            },
                            onClick = { onConfirm(null); onDismiss() },
                            contentPadding = PaddingValues(horizontal = 16.dp)
                        ) {
                            Text(
                                text = stringResource(id = R.string.no_task_list),
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }
                    items(
                        taskListUiState.taskLists,
                        key = { taskList -> taskList.id }
                    ) {
                        IconRowItem(
                            iconContent = {
                                if (currentTaskList == it) Icon(
                                    imageVector = MonoIcons.Check,
                                    contentDescription = null
                                ) else {
                                    Spacer(modifier = Modifier.size(24.dp))
                                }
                            },
                            onClick = { onConfirm(it); onDismiss() },
                            contentPadding = PaddingValues(horizontal = 16.dp)
                        ) {
                            Text(text = it.name, modifier = Modifier.padding(start = 8.dp))
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TaskDetailTopAppBar(
    isBookmarked: Boolean,
    onBackClick: () -> Unit,
    onBookmarkChange: () -> Unit,
    onDeleteTask: () -> Unit,
    modifier: Modifier = Modifier
) {
    MonoTopAppBar(
        title = {},
        modifier = modifier,
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = MonoIcons.ArrowBack,
                    contentDescription = "Back"
                )
            }
        },
        actions = {
            IconButton(onClick = onBookmarkChange) {
                Icon(
                    imageVector = if (isBookmarked) {
                        MonoIcons.Bookmark
                    } else {
                        MonoIcons.BookmarkOutlined
                    },
                    contentDescription = null,
                    tint = if (isBookmarked) {
                        MaterialTheme.colorScheme.primaryContainer
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    }
                )
            }
            TaskDetailMoreMenu(
                onDeleteTask = onDeleteTask
            )
        }
    )
}

@Composable
private fun TaskDetailMoreMenu(
    onDeleteTask: () -> Unit
) {
    MonoIconDropdownMenu(
        iconContent = {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = stringResource(id = R.string.menu_delete_task)
            )
        }
    ) { closeMenu ->
        DropdownMenuItem(
            text = {
                Text(text = stringResource(id = R.string.delete_task))
            },
            onClick = { onDeleteTask(); closeMenu() },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = null
                )
            },
            colors = MenuDefaults.itemColors(
                textColor = MaterialTheme.colorScheme.error,
                leadingIconColor = MaterialTheme.colorScheme.error
            )
        )
    }
}

@Composable
private fun DeleteTaskDialog(
    onDismiss: () -> Unit,
    onDeleteTask: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = { onDeleteTask(); onDismiss() }) {
                Text(text = "Delete")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "Cancel")
            }
        },
        title = {
            Text(text = "This task has subtasks")
        },
        text = {
            Text(text = "Deleting this task will also delete subtasks")
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AttachmentsBottomSheet(
    onDismiss: () -> Unit,
    onTakePhoto: () -> Unit,
    onImageSelected: () -> Unit,
    onDrawCanvas: () -> Unit,
    onRecordVoice: () -> Unit,
    modifier: Modifier = Modifier,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        modifier = modifier,
        sheetState = sheetState,
        shape = RectangleShape,
        dragHandle = null,
        windowInsets = WindowInsets.ime
    ) {
        Column {
            Row(
                modifier = Modifier
                    .height(50.dp)
                    .fillMaxWidth()
                    .clickable { onTakePhoto() },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.width(4.dp))
                Box(
                    modifier = Modifier.size(40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = Icons.Outlined.PhotoCamera, contentDescription = null)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Take photo")
            }
            Row(
                modifier = Modifier
                    .height(50.dp)
                    .fillMaxWidth()
                    .clickable { onImageSelected() },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.width(4.dp))
                Box(
                    modifier = Modifier.size(40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = Icons.Outlined.Image, contentDescription = null)
                }
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "Add image")
            }
            Row(
                modifier = Modifier
                    .height(50.dp)
                    .fillMaxWidth()
                    .clickable { onRecordVoice() },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.width(4.dp))
                Box(
                    modifier = Modifier.size(40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = Icons.Outlined.MicNone, contentDescription = null)
                }
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "Recording")
            }
            Spacer(modifier = Modifier.windowInsetsBottomHeight(WindowInsets.navigationBars))
        }
    }
}

@Composable
private fun RecordPlayer(
    fileUrl: String,
    onDeleteRecord: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val player: MediaPlayer = remember {
        MediaPlayer().apply {
            setDataSource(context, Uri.parse(fileUrl))
            prepare()
        }
    }
    var playState by remember { mutableStateOf(PlayerState.Stop) }

    val playStopIcon =
        if (playState == PlayerState.Stop) Icons.Default.PlayArrow else Icons.Default.Stop

    var progress by remember {
        mutableFloatStateOf(0f)
    }
    LaunchedEffect(Unit) {
        while (isActive) {
            progress = (player.currentPosition / player.duration.coerceAtLeast(1)).toFloat()
            delay(200)
        }
    }
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.primaryContainer)
            .height(56.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    playState = when (playState) {
                        PlayerState.Play -> {
                            player.stop()
                            PlayerState.Stop
                        }

                        PlayerState.Stop -> {
                            player.start()
                            PlayerState.Play
                        }
                    }
                }
            ) {
                Icon(imageVector = playStopIcon, contentDescription = null)
            }
//            PlayerProgress(
//                progress = progress,
//                modifier = Modifier.weight(1f)
//            )
            IconButton(onClick = onDeleteRecord) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = null
                )
            }
        }
    }
}

@Composable
fun PlayerProgress(
    progress: Float,
    modifier: Modifier
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec,
        label = ""
    )
    LinearProgressIndicator(
        progress = animatedProgress,
        modifier = modifier,
        trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
    )
}

@Composable
private fun RecordDialog(
    onDismiss: () -> Unit,
    saveRecordUri: (url: String) -> Unit,
) {
    val context = LocalContext.current
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
    val fileName = "${context.externalCacheDir?.absolutePath}/record_$timeStamp.mp3"
    val file = File(fileName)

    if (!file.exists()) {
        file.createNewFile()
    }

    val recorder = remember { AudioRecordImpl(context) }
    DisposableEffect(key1 = Unit) {
        file.also {
            recorder.start(it)
        }
        onDispose {
            recorder.stop()
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    saveRecordUri(fileName)
                    onDismiss()
                }
            ) {
                Text(text = "Confirm")
            }
        },
        icon = {
            Icon(imageVector = Icons.Outlined.MicNone, contentDescription = null)
        },
        text = {
            Text(text = "On Recording..")
        }
    )
}

fun Context.createImageFile(): File {
    // Create an image file name
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
    val imageFileName = "JPEG_" + timeStamp + "_"
    return File.createTempFile(
        imageFileName, /* prefix */
        ".jpg", /* suffix */
        externalCacheDir /* directory */
    )
}

fun Context.createRecordFile(): File {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
    val imageFileName = "MPEG_" + timeStamp + "_"
    return File.createTempFile(
        imageFileName, /* prefix */
        ".mp3", /* suffix */
        externalCacheDir /* directory */
    )
}

private enum class PlayerState {
    Play, Stop
}