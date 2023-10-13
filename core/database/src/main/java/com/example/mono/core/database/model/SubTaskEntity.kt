package com.example.mono.core.database.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.mono.core.model.task.SubTask

/**
 * Internal model used to represent a subtask stored locally in a Room Database.
 */
@Entity(
    tableName = "sub_tasks",
    foreignKeys = [
        ForeignKey(
            entity = TaskEntity::class,
            parentColumns = ["id"],
            childColumns = ["taskId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class SubTaskEntity(
    @PrimaryKey val id: String,
    val title: String,
    val isCompleted: Boolean,
    val taskId: String
)

fun SubTaskEntity.asExternalModel() = SubTask(
    id = id,
    title = title,
    isCompleted = isCompleted,
    taskId = taskId
)