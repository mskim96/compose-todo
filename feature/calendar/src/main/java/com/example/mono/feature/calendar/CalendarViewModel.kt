package com.example.mono.feature.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mono.core.data.repository.TaskRepository
import com.example.mono.core.model.task.Task
import com.example.mono.core.model.task.TaskCreationParams
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

data class CalendarUiState(
    val selectedDate: LocalDate = LocalDate.now(),
    val tasks: List<Task> = emptyList(),
    val taskIndicator: Map<LocalDate?, List<Task>> = emptyMap(),
    val isLoading: Boolean = false
)

@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val taskRepository: TaskRepository
) : ViewModel() {

    private val _selectedDate = MutableStateFlow(LocalDate.now())

    val calendarUiState = combine(
        _selectedDate, taskRepository.getTasksStream()
    ) { selectedDate, tasks ->
        CalendarUiState(
            selectedDate = selectedDate,
            tasks = tasks.filter { it.date == selectedDate },
            taskIndicator = tasks.groupBy { it.date },
            isLoading = false
        )
    }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            CalendarUiState(
                isLoading = true
            )
        )

    fun updateSelectedDate(newDate: LocalDate) {
        viewModelScope.launch {
            _selectedDate.value = newDate
        }
    }

    fun createNewTask(taskCreationParams: TaskCreationParams) = viewModelScope.launch {
        taskRepository.createTask(
            title = taskCreationParams.title,
            detail = taskCreationParams.detail,
            isBookmarked = taskCreationParams.isBookmarked,
            date = taskCreationParams.date,
            time = taskCreationParams.time,
            taskListId = taskCreationParams.taskListId
        )
    }

    fun updateComplete(task: Task, completed: Boolean) {
        viewModelScope.launch {
            taskRepository.updateCompleteTask(task.id, completed)
        }
    }

    fun updateBookmarked(task: Task, bookmarked: Boolean) {
        viewModelScope.launch {
            taskRepository.updateTaskBookmark(task.id, bookmarked)
        }
    }
}