package com.example.mono.core.model

import java.time.LocalDate
import java.time.LocalTime

/**
 * Immutable model class for a task.
 *
 * @param id id of the task.
 * @param title title of the task.
 * @param description description of the task.
 * @param isCompleted whether or not this task is completed.
 * @param date selected date of the task.
 * @param time selected time of the task.
 */
data class Task(
    val id: String,
    val title: String,
    val description: String,
    val isCompleted: Boolean = false,
    val isBookmarked: Boolean = false,
    val date: LocalDate? = null,
    val time: LocalTime? = null,
)
