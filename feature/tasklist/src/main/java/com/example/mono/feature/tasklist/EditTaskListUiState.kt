package com.example.mono.feature.tasklist

import com.example.mono.core.model.task.TaskList

/**
 * A sealed hierarchy describing he state of the edit task lists.
 */
sealed interface EditTaskListUiState {
    data object Loading : EditTaskListUiState

    data class Success(
        val taskLists: List<TaskList> = emptyList()
    ) : EditTaskListUiState
}