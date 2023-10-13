package com.example.mono.feature.tasklist

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mono.core.common.datetime.toFormattedDate
import com.example.mono.core.data.repository.TaskListRepository
import com.example.mono.core.data.repository.TaskQuery
import com.example.mono.core.data.repository.TaskRepository
import com.example.mono.core.model.task.Task
import com.example.mono.core.model.task.TaskCreationParams
import com.example.mono.core.model.task.TaskList
import com.example.mono.core.model.task.TaskSortingType
import com.example.mono.feature.tasklist.navigation.TaskListIdArgs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

data class TaskListUiState(
    val activeTasks: List<Task> = emptyList(),
    val completedTasks: List<Task> = emptyList(),
    val activeTasksByDate: Map<String, List<Task>> = emptyMap(),
    val selectedTaskList: TaskList? = null,
    val selectedSortType: TaskSortingType = TaskSortingType.NONE,
    val isAscending: Boolean = false,
    val isLoading: Boolean = false
) {
    val empty: Boolean
        get() = activeTasks.isEmpty() && completedTasks.isEmpty() && activeTasksByDate.isEmpty()
}

@HiltViewModel
class TaskListViewModel @Inject constructor(
    taskListRepository: TaskListRepository,
    private val taskRepository: TaskRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val taskListId = TaskListIdArgs(savedStateHandle).taskListId
    private val _selectedTaskList = taskListRepository.getTaskList(taskListId)
        .catch {
            // TODO: An exception occurs when deleting a TaskList in EditTaskList,
            //  and while the exact reason remains unclear, a temporary workaround has been implemented.
        }

    private val _savedSortType =
        savedStateHandle.getStateFlow(TASKS_SORTING_SAVED_STATE_KEY, TaskSortingType.NONE)

    private val _isAscending = MutableStateFlow(false)
    private val _isLoading = MutableStateFlow(false)

    val taskListUiState: StateFlow<TaskListUiState> = combine(
        _isLoading, _savedSortType, _isAscending, _selectedTaskList, taskRepository.getTasksStream(
            query = TaskQuery(filterTaskListIds = setOf(taskListId))
        )
    ) { isLoading, sortType, isAscending, selectedTaskList, tasks ->
        val (activeTasks, completedTasks) = tasks.partition { !it.isCompleted }
        when (sortType) {
            TaskSortingType.NONE -> {
                val orderedTask = if (isAscending) {
                    activeTasks.reversed()
                } else {
                    activeTasks
                }
                TaskListUiState(
                    isLoading = false,
                    activeTasks = orderedTask,
                    completedTasks = completedTasks,
                    isAscending = isAscending,
                    selectedTaskList = selectedTaskList,
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
                TaskListUiState(
                    isLoading = false,
                    activeTasksByDate = sortedTasksWithDate,
                    completedTasks = completedTasks,
                    isAscending = isAscending,
                    selectedSortType = sortType,
                    selectedTaskList = selectedTaskList
                )
            }
        }
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = TaskListUiState(isLoading = true)
        )

    fun createNewTask(taskCreationParams: TaskCreationParams) = viewModelScope.launch {
        taskRepository.createTask(
            title = taskCreationParams.title,
            detail = taskCreationParams.detail,
            isBookmarked = taskCreationParams.isBookmarked,
            date = taskCreationParams.date,
            time = taskCreationParams.time,
            taskListId = taskListId
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
                    filterTaskListIds = setOf(taskListId)
                )
            )
        }
    }
}

const val TASKS_SORTING_SAVED_STATE_KEY = "TASKS_SORTING_SAVED_STATE_KEY"