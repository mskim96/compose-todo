package com.example.mono.feature.tasks.addEditTaskList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mono.core.data.repository.TaskListRepository
import com.example.mono.core.model.TaskList
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AddEditTaskListUiState(
    val items: List<TaskList> = emptyList()
)

@HiltViewModel
class AddEditTaskListViewModel @Inject constructor(
    private val taskListRepository: TaskListRepository
) : ViewModel() {

    val uiState = taskListRepository.getTaskLists()
        .map {
            AddEditTaskListUiState(it)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = AddEditTaskListUiState()
        )

    fun createTaskGroup(name: String) {
        viewModelScope.launch {
            taskListRepository.createTaskGroup(name)
        }
    }

    fun updateTaskGroup(taskList: TaskList) {
        viewModelScope.launch {
            taskListRepository.updateTaskGroup(taskList.id, taskList.name)
        }
    }

    fun deleteTaskGroup(taskList: TaskList) {
        viewModelScope.launch {
            taskListRepository.deleteTaskGroup(taskList.id)
        }
    }
}