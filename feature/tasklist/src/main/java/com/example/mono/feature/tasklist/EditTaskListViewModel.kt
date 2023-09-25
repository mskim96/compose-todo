package com.example.mono.feature.tasklist

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mono.core.data.repository.TaskListRepository
import com.example.mono.core.model.TaskList
import com.example.mono.feature.tasklist.navigation.EditTaskListsModifyTypeArgs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditTaskListViewModel @Inject constructor(
    private val taskListRepository: TaskListRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val modifyType = EditTaskListsModifyTypeArgs(savedStateHandle).modifyType

    val taskListUiState = taskListRepository.getTaskLists()
        .map(EditTaskListUiState::Success)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = EditTaskListUiState.Loading
        )

    fun createTaskList(title: String) {
        viewModelScope.launch {
            taskListRepository.createTaskList(title)
        }
    }

    fun updateTaskList(taskList: TaskList) {
        viewModelScope.launch {
            taskListRepository.updateTaskList(taskList.id, taskList.name)
        }
    }

    fun deleteTaskList(taskList: TaskList) {
        viewModelScope.launch {
            taskListRepository.deleteTaskList(taskList.id)
        }
    }
}