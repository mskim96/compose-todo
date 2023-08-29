package com.example.mono.feature.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mono.core.data.repository.TaskRepository
import com.example.mono.core.model.Task
import com.example.mono.feature.tasks.navigation.DELETE_RESULT_OK
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class TasksViewModel @Inject constructor(
    private val taskRepository: TaskRepository
) : ViewModel() {

    private val _userMessage: MutableStateFlow<Int?> = MutableStateFlow(null)
    val userMessage = _userMessage.asStateFlow()

    val uiState: StateFlow<TasksUiState> = taskRepository.getTasksStream()
        .map { tasks ->
            if (tasks.isNotEmpty()) {
                val (completedTasks, activeTasks) = tasks.partition(Task::isCompleted)
                TasksUiState.Tasks(
                    activeTasks = activeTasks,
                    completedTasks = completedTasks
                )
            } else {
                TasksUiState.Empty
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = TasksUiState.Loading
        )

    fun clearCompletedTasks() {
        viewModelScope.launch {
            taskRepository.clearCompletedTask()
        }
    }

    fun completeTask(task: Task, completed: Boolean) = viewModelScope.launch {
        if (completed) {
            taskRepository.completeTask(task.id)
            showSnackBarMessage(R.string.task_marked_completed)
        } else {
            taskRepository.activeTask(task.id)
            showSnackBarMessage(R.string.task_marked_activate)
        }
    }

    fun updateBookmarked(task: Task, bookmarked: Boolean) = viewModelScope.launch {
        if (bookmarked) {
            taskRepository.addTaskBookmark(task.id)
            showSnackBarMessage(R.string.task_set_bookmarked)
        } else {
            taskRepository.removeTaskBookmark(task.id)
            showSnackBarMessage(R.string.task_undo_bookmarked)
        }
    }

    fun snackBarMessageShown() {
        _userMessage.value = null
    }

    private fun showSnackBarMessage(message: Int) {
        _userMessage.value = message
    }

    fun showEditResultMessage(result: Int) {
        when (result) {
            DELETE_RESULT_OK -> showSnackBarMessage(R.string.successfully_deleted_task_message)
        }
    }
}

sealed interface TasksUiState {

    data object Loading : TasksUiState

    data class Tasks(
        val activeTasks: List<Task>,
        val completedTasks: List<Task>
    ) : TasksUiState

    data object Empty : TasksUiState
}