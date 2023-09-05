package com.example.mono.feature.tasks.editGroup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mono.core.data.repository.TaskGroupRepository
import com.example.mono.core.model.TaskGroup
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AddEditTaskGroupUiState(
    val items: List<TaskGroup> = emptyList()
)

@HiltViewModel
class AddEditTaskGroupViewModel @Inject constructor(
    private val taskGroupRepository: TaskGroupRepository
) : ViewModel() {

    val uiState = taskGroupRepository.getTaskGroups()
        .map {
            AddEditTaskGroupUiState(it)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = AddEditTaskGroupUiState()
        )

    fun createTaskGroup(name: String) {
        viewModelScope.launch {
            taskGroupRepository.createTaskGroup(name)
        }
    }

    fun updateTaskGroup(taskGroup: TaskGroup) {
        viewModelScope.launch {
            taskGroupRepository.updateTaskGroup(taskGroup.id, taskGroup.name)
        }
    }

    fun deleteTaskGroup(taskGroup: TaskGroup) {
        viewModelScope.launch {
            taskGroupRepository.deleteTaskGroup(taskGroup.id)
        }
    }
}