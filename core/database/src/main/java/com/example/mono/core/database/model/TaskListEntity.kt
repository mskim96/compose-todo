package com.example.mono.core.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.mono.core.model.task.TaskList

/**
 * Defines a task list. It has 1:n relationship with [TaskEntity].
 */
@Entity(
    tableName = "task_lists"
)
data class TaskListEntity(
    @PrimaryKey val id: String,
    val name: String
)

fun TaskListEntity.asExternalModel() = TaskList(
    id = id,
    name = name
)