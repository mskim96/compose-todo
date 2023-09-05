package com.example.mono.core.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.mono.core.model.TaskGroup

@Entity(
    tableName = "task_group"
)
data class TaskGroupEntity(
    @PrimaryKey val id: String,
    val name: String
)

fun TaskGroupEntity.asExternalModel() = TaskGroup(
    id = id,
    name = name
)