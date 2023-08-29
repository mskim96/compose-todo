package com.example.mono.feature.tasks.taskDetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mono.core.data.repository.TaskRepository
import com.example.mono.feature.tasks.navigation.TASK_ID
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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
    val description: String = "",
    val isCompleted: Boolean = false,
    val isBookmarked: Boolean = false,
    val date: LocalDate? = null,
    val time: LocalTime? = null,
    val isTaskDeleted: Boolean = false,
    val isTaskSaved: Boolean = false
)

@HiltViewModel
class TaskDetailViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val taskId: String = savedStateHandle[TASK_ID]!!

    private val _uiState = MutableStateFlow(TaskDetailUiState())
    val uiState: StateFlow<TaskDetailUiState> = _uiState.asStateFlow()

    init {
        loadTask(taskId)
    }

    fun updateTask() {
        viewModelScope.launch {
            taskRepository.updateTask(
                taskId = taskId,
                title = uiState.value.title,
                description = uiState.value.description,
                isCompleted = uiState.value.isCompleted,
                isBookmarked = uiState.value.isBookmarked,
                date = uiState.value.date,
                time = uiState.value.time,
            )
            _uiState.update {
                it.copy(isTaskSaved = true)
            }
        }
    }

    fun deleteTask() = viewModelScope.launch {
        taskRepository.deleteTask(taskId)
        _uiState.update {
            it.copy(isTaskDeleted = true)
        }
    }

    fun updateTitle(newTitle: String) {
        _uiState.update { it.copy(title = newTitle) }
    }

    fun updateDescription(newDescription: String) {
        _uiState.update { it.copy(description = newDescription) }
    }

    fun updateDateTime(newDate: LocalDate?, newTime: LocalTime?) {
        _uiState.update { it.copy(date = newDate, time = newTime) }
    }

    private fun loadTask(taskId: String) {
        viewModelScope.launch {
            taskRepository.getTask(taskId).let { task ->
                if (task != null) {
                    _uiState.update {
                        it.copy(
                            title = task.title,
                            description = task.description,
                            date = task.date,
                            time = task.time,
                            isBookmarked = task.isBookmarked,
                            isCompleted = task.isCompleted
                        )
                    }
                }
            }
        }
    }
}