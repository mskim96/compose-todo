package com.example.mono.core.model.task

/**
 * Immutable model class for a subtask.
 *
 * @param id id of the subtask.
 * @param title title of the task.
 * @param isCompleted whether or not this task is completed.
 * @param taskId parent task id of the sub task.
 */
data class SubTask(
    val id: String,
    val title: String = "",
    val isCompleted: Boolean = false,
    val taskId: String,
)