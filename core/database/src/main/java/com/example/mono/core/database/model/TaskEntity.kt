package com.example.mono.core.database.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.mono.core.model.task.Task
import java.time.LocalDate
import java.time.LocalTime

/**
 * Internal model used to represent a task stored locally in a Room Database.
 */
@Entity(
    tableName = "tasks",
    foreignKeys = [
        ForeignKey(
            entity = TaskListEntity::class,
            parentColumns = ["id"],
            childColumns = ["taskListId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["taskListId"])
    ]
)
data class TaskEntity(
    @PrimaryKey val id: String,
    val title: String,
    val detail: String,
    val date: LocalDate?,
    val time: LocalTime?,
    val color: Long,
    val isCompleted: Boolean,
    val isBookmarked: Boolean,
    val isPendingNotification: Boolean,
    val taskListId: String?
)

fun TaskEntity.asExternalModel() = Task(
    id = id,
    title = title,
    detail = detail,
    date = date,
    time = time,
    color = color,
    isCompleted = isCompleted,
    isBookmarked = isBookmarked,
    taskListId = taskListId,
)