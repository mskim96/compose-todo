package com.example.mono.feature.tasks.taskList

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mono.core.common.datetime.toFormattedDate
import com.example.mono.core.common.result.Result
import com.example.mono.core.data.repository.TaskListRepository
import com.example.mono.core.data.repository.TaskRepository
import com.example.mono.core.model.Task
import com.example.mono.core.model.TaskSortingType
import com.example.mono.feature.tasks.navigation.TaskListIdArgs
import com.example.mono.feature.tasks.taskDetail.TaskListUiState
import com.example.mono.feature.tasks.tasks.TASKS_SORTING_SAVED_STATE_KEY
import com.example.mono.feature.tasks.tasks.TasksUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject

@HiltViewModel
class TaskListViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
    taskListRepository: TaskListRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    val taskListId = TaskListIdArgs(savedStateHandle).taskListId
    val selectedTaskList = taskListRepository.getTaskList(taskListId)

    private val _savedSortType =
        savedStateHandle.getStateFlow(TASKS_SORTING_SAVED_STATE_KEY, TaskSortingType.NONE)

    private val _isAscending = MutableStateFlow(false)
    private val _isLoading = MutableStateFlow(false)

    val taskListsUiState = taskListRepository.getTaskLists()
        .map(TaskListUiState::Success)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = TaskListUiState.Loading
        )

    val tasksUiState: StateFlow<TasksUiState> = combine(
        _isLoading, _savedSortType, _isAscending, taskRepository.getTasksStream()
    ) { isLoading, sortType, isAscending, tasks ->
        val (activeTasks, completedTasks) = tasks.filter { it.taskListId == taskListId }
            .partition { !it.isCompleted }
        when (sortType) {
            TaskSortingType.NONE -> {
                val orderedTask = if (isAscending) {
                    activeTasks.reversed()
                } else {
                    activeTasks
                }
                TasksUiState(
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
                TasksUiState(
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
            detail = description,
            isBookmarked = isBookMarked,
            date = date,
            time = time,
            taskListId = taskListId
        )
    }

    fun completeTask(task: Task, completed: Boolean) = viewModelScope.launch {
        taskRepository.updateCompleteTask(task.id, completed)
    }

    fun updateBookmarked(task: Task, bookmarked: Boolean) = viewModelScope.launch {
        taskRepository.updateTaskBookmark(task.id, bookmarked)
    }

    fun updateOrdering(ordered: Boolean) {
        _isAscending.value = ordered
    }

    fun setSortedType(type: TaskSortingType) {
        savedStateHandle[TASKS_SORTING_SAVED_STATE_KEY] = type
    }
}