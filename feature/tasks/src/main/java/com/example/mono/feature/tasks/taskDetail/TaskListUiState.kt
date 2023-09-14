package com.example.mono.feature.tasks.taskDetail

import com.example.mono.core.model.TaskList

sealed interface TaskListUiState {
    data class Success(val taskLists: List<TaskList>) : TaskListUiState
    data object Loading : TaskListUiState
}

val TaskListUiState.Success.canChangeTaskList: Boolean get() = taskLists.isNotEmpty()