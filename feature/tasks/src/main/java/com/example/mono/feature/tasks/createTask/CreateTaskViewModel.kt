package com.example.mono.feature.tasks.createTask

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mono.core.data.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject

/**
 * UI state for the create task screen.
 */
data class CreateTaskUiState(
    val title: String = "",
    val description: String = "",
    val isBookmarked: Boolean = false,
    val date: LocalDate? = null,
    val time: LocalTime? = null,
    val isTaskSaved: Boolean = false
)

/**
 * ViewModel for the create task screen.
 */
@HiltViewModel
class CreateTaskViewModel @Inject constructor(
    private val taskRepository: TaskRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateTaskUiState())
    val uiState = _uiState.asStateFlow()

    fun updateTitle(newTitle: String) {
        _uiState.update { it.copy(title = newTitle) }
    }

    fun updateDescription(newDescription: String) {
        _uiState.update { it.copy(description = newDescription) }
    }

    fun updateDateTime(newDate: LocalDate?, newTime: LocalTime?) {
        _uiState.update { it.copy(date = newDate, time = newTime) }
    }

    fun toggleBookmarked(bookmarked: Boolean) {
        _uiState.update { it.copy(isBookmarked = bookmarked) }
    }

    fun createNewTask() = viewModelScope.launch {
        taskRepository.createTask(
            title = uiState.value.title,
            description = uiState.value.description,
            isCompleted = false,
            isBookmarked = uiState.value.isBookmarked,
            date = uiState.value.date,
            time = uiState.value.time
        )
        _uiState.update { it.copy(isTaskSaved = true) }
    }
}