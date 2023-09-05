package com.example.mono.core.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.mono.core.model.Task
import java.time.LocalDate
import java.time.LocalTime

/**
 * Internal model used to represent a task stored locally in a Room Database.
 */
@Entity(
    tableName = "task"
)
data class TaskEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val isCompleted: Boolean,
    val isBookmarked: Boolean,
    val date: LocalDate?,
    val time: LocalTime?,
    val groupId: String
)

/**
 * Mapping the [TaskEntity] to a external [Task] model.
 */
fun TaskEntity.asExternalModel() = Task(
    id = id,
    title = title,
    description = description,
    isCompleted = isCompleted,
    isBookmarked = isBookmarked,
    date = date,
    time = time,
    groupId = groupId
)