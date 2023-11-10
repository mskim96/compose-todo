package com.example.mono.feature.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mono.core.data.repository.TaskRepository
import com.example.mono.feature.detail.navgation.TaskIdArgs
import com.example.mono.feature.detail.navgation.attachmentArgs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AttachmentsViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val taskId = TaskIdArgs(savedStateHandle).taskId

    private val selectedAttachmentArg = savedStateHandle.getStateFlow(attachmentArgs, "")
    private val _selectedAttachment = MutableStateFlow(selectedAttachmentArg.value.replace("&", "/"))
    val selectedAttachment = _selectedAttachment.asStateFlow()

    private val _attachments = MutableStateFlow(listOf<String>())
    val attachments = _attachments.asStateFlow()

    init {
        loadTask(taskId)
    }

    fun deleteAttachment(attachment: String) {
        _attachments.value = _attachments.value.filterNot { it == attachment }
        viewModelScope.launch {
            taskRepository.updateAttachments(taskId, attachments.value)
        }
    }

    fun updateSelectedAttachment(index: Int) {
        viewModelScope.launch {
            _selectedAttachment.value = _attachments.value[index]
        }
    }

    private fun loadTask(taskId: String) {
        viewModelScope.launch {
            val currentTask = taskRepository.getTask(taskId)
            if (currentTask != null) {
                _attachments.value = currentTask.attachments
            }
        }
    }
}