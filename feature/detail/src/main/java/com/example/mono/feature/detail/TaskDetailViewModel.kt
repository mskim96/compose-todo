package com.example.mono.feature.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mono.core.common.scope.Dispatcher
import com.example.mono.core.common.scope.MonoDispatchers
import com.example.mono.core.data.repository.SubTaskRepository
import com.example.mono.core.data.repository.TaskListRepository
import com.example.mono.core.data.repository.TaskRepository
import com.example.mono.core.model.task.SubTask
import com.example.mono.core.model.task.Task
import com.example.mono.core.model.task.TaskColorPalette
import com.example.mono.core.model.task.TaskList
import com.example.mono.feature.detail.navgation.TaskIdArgs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject

/**
 * UI state for the task detail screen.
 */
data class TaskDetailUiState(
    val title: String = "",
    val detail: String = "",
    val isTaskCompleted: Boolean = false,
    val isTaskBookmarked: Boolean = false,
    val date: LocalDate? = null,
    val time: LocalTime? = null,
    val color: Long = TaskColorPalette.Burgundy.color,
    val taskList: TaskList? = null,
    val isLoading: Boolean = false,
    val isTaskSaved: Boolean = false,
    val isTaskDeleted: Boolean = false,
    val attachments: List<String> = emptyList(),
    val recorders: List<String> = emptyList()
)

@HiltViewModel
class TaskDetailViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
    private val subTaskRepository: SubTaskRepository,
    private val taskListRepository: TaskListRepository,
    @Dispatcher(MonoDispatchers.IO) private val dispatcher: CoroutineDispatcher,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    // Task id from navigation back stack entry.
    val taskId = TaskIdArgs(savedStateHandle).taskId
    val deleteAttachmentId = savedStateHandle.getStateFlow("attachment", "")

    // Initial snapshot for detecting changes in task.
    private var snapshotTask: Task? = null

    private val _taskUiState = MutableStateFlow(TaskDetailUiState())
    val taskUiState: StateFlow<TaskDetailUiState> = _taskUiState.asStateFlow()

    val subTaskUiState = subTaskRepository.getSubTasksStream(taskId)
        .map(SubTaskUiState::Success)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = SubTaskUiState.Loading
        )

    val taskListUiState = taskListRepository.getTaskLists()
        .map(TaskListUiState::Success)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = TaskListUiState.Loading
        )

    init {
        loadTask(taskId)
    }

    /**
     * When the user triggers a back button event, the logic to detect change in Task.
     * The logic to update when there are changes or execute the previous back button event when there are no changes.
     *
     * @param onBackClick default back button event.
     */
    fun saveTask(onBackClick: () -> Unit) {
        if (isTaskChanged(taskUiState.value)) {
            updateTask(); onBackClick()
        } else {
            onBackClick()
        }
    }

    fun updateTaskCompleted(completed: Boolean) {
        viewModelScope.launch {
            taskRepository.updateCompleteTask(taskId, completed)
            _taskUiState.update {
                it.copy(isTaskCompleted = completed, isTaskSaved = true)
            }
        }
    }

    fun updateTaskBookmark(bookmarked: Boolean) {
        viewModelScope.launch {
            taskRepository.updateTaskBookmark(taskId, bookmarked)
            _taskUiState.update {
                it.copy(isTaskBookmarked = bookmarked)
            }
        }
    }

    fun deleteTask() {
        viewModelScope.launch {
            taskRepository.deleteTask(taskId)
            _taskUiState.update {
                it.copy(isTaskDeleted = true)
            }
        }
    }

    fun updateTitle(newTitle: String) {
        _taskUiState.update { it.copy(title = newTitle) }
    }

    fun updateDetail(newDescription: String) {
        _taskUiState.update { it.copy(detail = newDescription) }
    }

    fun updateDateTime(newDate: LocalDate?, newTime: LocalTime?) {
        _taskUiState.update { it.copy(date = newDate, time = newTime) }
    }

    fun updateTaskList(newTaskList: TaskList?) {
        _taskUiState.update { it.copy(taskList = newTaskList) }
    }

    fun updateTaskColor(newColor: Long) {
        _taskUiState.update { it.copy(color = newColor) }
    }

    fun createSubTask() {
        viewModelScope.launch(dispatcher) {
            subTaskRepository.createSubTask(taskId = taskId)
        }
    }

    fun editSubTask(subTaskId: String, title: String) {
        viewModelScope.launch(dispatcher) {
            subTaskRepository.updateSubTask(
                subTaskId = subTaskId,
                title = title
            )
        }
    }

    fun updateSubTaskComplete(subTask: SubTask, completed: Boolean) {
        viewModelScope.launch(dispatcher) {
            subTaskRepository.updateCompletedSubTask(subTask.id, completed)
        }
    }

    fun deleteSubTask(subTaskId: String) {
        viewModelScope.launch(dispatcher) {
            subTaskRepository.deleteSubTask(subTaskId)
        }
    }

    fun updateAttachment(imageUrlList: List<String>) {
        val previousImageUrlList = _taskUiState.value.attachments.toMutableList()
        viewModelScope.launch {
            taskRepository.updateAttachments(taskId, previousImageUrlList + imageUrlList)
            _taskUiState.update {
                it.copy(
                    attachments = previousImageUrlList + imageUrlList
                )
            }
        }
    }

    fun deleteAttachment(imageUrl: String) {
        _taskUiState.update { state ->
            state.copy(
                attachments = _taskUiState.value.attachments.filterNot { deleteAttachmentId.value != imageUrl }
            )
        }
    }

    fun updateAttachmentFromCamera(imageUrl: String) {
        val previousImageUrlList = _taskUiState.value.attachments.toMutableList()
        viewModelScope.launch {
            taskRepository.updateAttachments(taskId, previousImageUrlList + imageUrl)
            _taskUiState.update {
                it.copy(
                    attachments = previousImageUrlList + imageUrl
                )
            }
        }
    }

    fun createRecord(recordUrl: String) {
        val previousRecords = _taskUiState.value.recorders
        viewModelScope.launch {
            taskRepository.updateRecord(taskId, previousRecords + recordUrl)
            _taskUiState.update {
                it.copy(
                    recorders = previousRecords + recordUrl
                )
            }
        }
    }

    fun deleteRecord(recordUrl: String) {
        val currentRecords = _taskUiState.value.recorders
        val remainRecords = currentRecords.filterNot { it == recordUrl }
        viewModelScope.launch {
            taskRepository.updateRecord(taskId, currentRecords.filterNot { it == recordUrl })
            _taskUiState.update {
                it.copy(
                    recorders = remainRecords
                )
            }
        }
    }

    private fun updateTask() {
        viewModelScope.launch {
            taskRepository.updateTask(
                taskId = taskId,
                title = taskUiState.value.title,
                detail = taskUiState.value.detail,
                date = taskUiState.value.date,
                time = taskUiState.value.time,
                color = taskUiState.value.color,
                taskListId = taskUiState.value.taskList?.id,
                attachments = taskUiState.value.attachments,
                recorders = taskUiState.value.recorders
            )
        }
    }

    /**
     * In the data layer, load the Task and set it in the state and snapshot.
     */
    private fun loadTask(taskId: String) {
        _taskUiState.update {
            it.copy(isLoading = true)
        }
        viewModelScope.launch {
            val currentTask = taskRepository.getTask(taskId)
            if (currentTask != null) {
                snapshotTask = currentTask
                val currentTaskList = currentTask.taskListId?.let {
                    taskListRepository.getOneOffTaskList(it)
                }
                _taskUiState.update {
                    it.copy(
                        title = currentTask.title,
                        detail = currentTask.detail,
                        isTaskBookmarked = currentTask.isBookmarked,
                        isTaskCompleted = currentTask.isCompleted,
                        date = currentTask.date,
                        time = currentTask.time,
                        color = currentTask.color,
                        taskList = currentTaskList,
                        attachments = currentTask.attachments,
                        recorders = currentTask.recorders,
                        isLoading = false
                    )
                }
            } else {
                _taskUiState.update { it.copy(isLoading = false) }
            }
        }
    }

    /**
     * The logic to compare the current state with the snapshot task to determine if there are
     * any change.
     *
     * @param currentState current task detail state.
     */
    private fun isTaskChanged(currentState: TaskDetailUiState): Boolean {
        return currentState.title != snapshotTask?.title ||
                currentState.detail != snapshotTask?.detail ||
                currentState.isTaskCompleted != snapshotTask?.isCompleted ||
                currentState.isTaskBookmarked != snapshotTask?.isBookmarked ||
                currentState.date != snapshotTask?.date ||
                currentState.time != snapshotTask?.time ||
                currentState.color != snapshotTask?.color ||
                currentState.taskList?.id != snapshotTask?.taskListId ||
                currentState.attachments != snapshotTask?.attachments ||
                currentState.recorders != snapshotTask?.recorders
    }
}

sealed interface SubTaskUiState {
    data class Success(val subTasks: List<SubTask>) : SubTaskUiState
    data object Loading : SubTaskUiState
}

sealed interface TaskListUiState {
    data class Success(val taskLists: List<TaskList>) : TaskListUiState
    data object Loading : TaskListUiState
}