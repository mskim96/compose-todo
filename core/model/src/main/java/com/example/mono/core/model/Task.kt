package com.example.mono.core.model

import java.time.LocalDate
import java.time.LocalTime

/**
 * Immutable model class for a task.
 *
 * @param id id of the task.
 * @param taskListId task is associated with the list id.
 * @param title title of the task.
 * @param detail detail of the task.
 * @param date selected date of the task.
 * @param time selected time of the task.
 * @param isCompleted whether or not this task is completed.
 * @param isBookmarked whether or not this task is bookmarked.
 * @param subTasks subtasks of the task.
 */
data class Task(
    val id: String,
    val taskListId: String? = null,
    val title: String,
    val detail: String,
    val date: LocalDate? = null,
    val time: LocalTime? = null,
    val isCompleted: Boolean = false,
    val isBookmarked: Boolean = false,
    val subTasks: List<SubTask> = emptyList()
)