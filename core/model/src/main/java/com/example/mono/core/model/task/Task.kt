package com.example.mono.core.model.task

import java.time.LocalDate
import java.time.LocalTime

/**
 * Immutable model class for a task.
 *
 * @property id id of the task.
 * @property taskListId task is associated with the list id.
 * @property title title of the task.
 * @property detail detail of the task.
 * @property date selected date of the task.
 * @property time selected time of the task.
 * @property isCompleted whether or not this task is completed.
 * @property isBookmarked whether or not this task is bookmarked.
 * @property subTasks subtasks of the task.
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

/**
 * Data class representing the parameters required to create a new task.
 *
 * @property title title of the task.
 * @property detail detail of the task.
 * @property date selected date of the task.
 * @property time selected time of the task.
 * @property isBookmarked whether or not this task is bookmarked.
 * @property taskListId task is associated with the list id.
 */
data class TaskCreationParams(
    val title: String = "",
    val detail: String = "",
    val date: LocalDate? = null,
    val time: LocalTime? = null,
    val isBookmarked: Boolean = false,
    val taskListId: String? = null
)