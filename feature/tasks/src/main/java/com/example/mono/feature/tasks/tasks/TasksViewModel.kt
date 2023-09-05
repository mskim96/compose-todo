package com.example.mono.feature.tasks.tasks

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mono.core.data.repository.TaskGroupRepository
import com.example.mono.core.data.repository.TaskRepository
import com.example.mono.core.model.Task
import com.example.mono.feature.tasks.R
import com.example.mono.feature.tasks.navigation.BuiltInTaskGroups
import com.example.mono.feature.tasks.navigation.TASKS_GROUP_ID_ARGS
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject


@HiltViewModel
class TasksViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
    private val taskGroupRepository: TaskGroupRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val builtInTaskGroups = BuiltInTaskGroups.values().asList()

    val taskGroupId = savedStateHandle.getStateFlow(TASKS_GROUP_ID_ARGS, BuiltInTaskGroups.ALL.groupId)
    val taskGroups = taskGroupRepository.getTaskGroups()

    val uiState: StateFlow<TasksUiState> = taskRepository.getTasksStream()
        .map { tasks ->
            if (tasks.isNotEmpty()) {
                val (completedTasks, activeTasks) = tasks.partition(Task::isCompleted)
                TasksUiState.Tasks(
                    activeTasks = filteredGroupTasks(activeTasks, taskGroupId.value),
                    completedTasks = filteredGroupTasks(completedTasks, taskGroupId.value)
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

    fun createNewTask(
        title: String,
        description: String,
        isBookMarked: Boolean,
        date: LocalDate?,
        time: LocalTime?,
        groupId: String
    ) = viewModelScope.launch {
        taskRepository.createTask(
            title = title,
            description = description,
            isBookmarked = isBookMarked,
            date = date,
            time = time,
            groupId = groupId
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

    private fun filteredGroupTasks(tasks: List<Task>, groupId: String): List<Task> {
        return when(groupId){
            BuiltInTaskGroups.ALL.groupId -> tasks
            BuiltInTaskGroups.BOOKMARKS.groupId -> tasks.filter { it.isBookmarked }
            BuiltInTaskGroups.REMINDERS.groupId -> emptyList()
            else -> tasks.filter { it.groupId == groupId }
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