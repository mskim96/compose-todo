package com.example.mono.feature.tasks.bookmarks

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mono.core.common.result.Result
import com.example.mono.core.common.result.asResult
import com.example.mono.core.data.repository.TaskListRepository
import com.example.mono.core.data.repository.TaskRepository
import com.example.mono.core.model.Task
import com.example.mono.core.model.TaskFilterType
import com.example.mono.feature.tasks.tasks.TASKS_FILTER_SAVED_STATE_KEY
import com.example.mono.feature.tasks.tasks.TasksUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject

@HiltViewModel
class BookmarksViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
    taskListRepository: TaskListRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _savedFilterType =
        savedStateHandle.getStateFlow(TASKS_FILTER_SAVED_STATE_KEY, TaskFilterType.ALL_TASKS)

    private val _isLoading = MutableStateFlow(false)
    private val _taskLists = taskListRepository.getTaskLists()
    private val _filteredTasksResult =
        combine(taskRepository.getTasksStream(), _savedFilterType) { tasks, type ->
            filterTask(tasks, type)
        }
            .asResult()

    val uiState: StateFlow<TasksUiState> = combine(
        _isLoading, _taskLists, _filteredTasksResult
    ) { isLoading, taskLists, tasksResult ->
        when (tasksResult) {
            Result.Loading -> {
                TasksUiState(
                    isLoading = true,
                    taskLists = taskLists
                )
            }

            is Result.Error -> {
                TasksUiState(
                    isLoading = false,
                    taskLists = taskLists,
                    tasks = emptyList()
                )
            }

            is Result.Success -> {
                TasksUiState(
                    isLoading = false,
                    taskLists = taskLists,
                    tasks = tasksResult.data
                )
            }
        }
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = TasksUiState(isLoading = true)
        )

    fun createNewTask(
        title: String,
        description: String,
        isBookMarked: Boolean,
        date: LocalDate?,
        time: LocalTime?
    ) = viewModelScope.launch {
        taskRepository.createTask(
            title = title,
            description = description,
            isBookmarked = isBookMarked,
            date = date,
            time = time,
            taskListId = null
        )
    }

    fun completeTask(task: Task, completed: Boolean) = viewModelScope.launch {
        if (completed) {
            taskRepository.completeTask(task.id)
        } else {
            taskRepository.activeTask(task.id)
        }
    }

    fun updateBookmarked(task: Task, bookmarked: Boolean) = viewModelScope.launch {
        if (bookmarked) {
            taskRepository.addTaskBookmark(task.id)
        } else {
            taskRepository.removeTaskBookmark(task.id)
        }
    }

    private fun filterTask(tasks: List<Task>, filteringType: TaskFilterType): List<Task> {
        val tasksToShow = ArrayList<Task>()
        // We filter the tasks based on the requestType
        for (task in tasks) {
            when (filteringType) {
                TaskFilterType.ALL_TASKS -> if (task.isBookmarked) tasksToShow.add(task)
                TaskFilterType.ACTIVE_TASKS -> if (task.isBookmarked && !task.isCompleted) {
                    tasksToShow.add(task)
                }
            }
        }
        return tasksToShow
    }
}