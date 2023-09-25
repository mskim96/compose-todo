package com.example.mono.feature.bookmarks

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mono.core.common.datetime.toFormattedDate
import com.example.mono.core.data.repository.TaskQuery
import com.example.mono.core.data.repository.TaskRepository
import com.example.mono.core.model.Task
import com.example.mono.core.model.TaskCreationParams
import com.example.mono.core.model.TaskSortingType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

data class BookmarkTasksUiState(
    val activeTasks: List<Task> = emptyList(),
    val completedTasks: List<Task> = emptyList(),
    val activeTasksByDate: Map<String, List<Task>> = emptyMap(),
    val selectedSortType: TaskSortingType = TaskSortingType.NONE,
    val isAscending: Boolean = false,
    val isLoading: Boolean = false
) {
    val empty: Boolean
        get() = activeTasks.isEmpty() && completedTasks.isEmpty() && activeTasksByDate.isEmpty()
}

@HiltViewModel
class BookmarksViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _savedSortType =
        savedStateHandle.getStateFlow(TASKS_SORTING_SAVED_STATE_KEY, TaskSortingType.NONE)

    private val _isAscending = MutableStateFlow(false)
    private val _isLoading = MutableStateFlow(false)

    val bookmarksUiState: StateFlow<BookmarkTasksUiState> = combine(
        _isLoading, _savedSortType, _isAscending, taskRepository.getTasksStream(
            query = TaskQuery(filterBookmark = true)
        )
    ) { isLoading, sortType, isAscending, tasks ->
        val (activeTasks, completedTasks) = tasks.partition { !it.isCompleted }
        when (sortType) {
            TaskSortingType.NONE -> {
                val orderedTask = if (isAscending) {
                    activeTasks.reversed()
                } else {
                    activeTasks
                }
                BookmarkTasksUiState(
                    isLoading = false,
                    activeTasks = orderedTask,
                    completedTasks = completedTasks,
                    isAscending = isAscending,
                    selectedSortType = sortType
                )
            }

            TaskSortingType.DATE -> {
                val tasksWithDate = activeTasks.groupBy { it.date }
                val sortedTasksWithDate = tasksWithDate.entries.sortedWith(
                    compareBy { entry ->
                        val key = entry.key
                        if (key != null) {
                            if (isAscending) key else LocalDate.MAX.minusDays(key.toEpochDay())
                        } else {
                            LocalDate.MAX
                        }
                    }
                ).associate {
                    val formattedDate = it.key?.toFormattedDate() ?: "No date"
                    formattedDate to it.value
                }
                BookmarkTasksUiState(
                    isLoading = false,
                    activeTasksByDate = sortedTasksWithDate,
                    completedTasks = completedTasks,
                    isAscending = isAscending,
                    selectedSortType = sortType
                )
            }
        }
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = BookmarkTasksUiState(isLoading = true)
        )

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

    fun updateOrdering(ordered: Boolean) {
        _isAscending.value = ordered
    }

    fun setSortedType(type: TaskSortingType) {
        savedStateHandle[TASKS_SORTING_SAVED_STATE_KEY] = type
    }

    fun clearCompletedTasks() {
        viewModelScope.launch {
            taskRepository.clearCompletedTask(
                query = TaskQuery(
                    filterBookmark = true
                )
            )
        }
    }
}

const val TASKS_SORTING_SAVED_STATE_KEY = "TASKS_SORTING_SAVED_STATE_KEY"