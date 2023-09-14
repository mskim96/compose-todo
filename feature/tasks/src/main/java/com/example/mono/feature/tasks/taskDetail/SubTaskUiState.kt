package com.example.mono.feature.tasks.taskDetail

import com.example.mono.core.model.SubTask

sealed interface SubTaskUiState {
    data class Success(val subTasks: List<SubTask>) : SubTaskUiState
    data object Loading : SubTaskUiState
}