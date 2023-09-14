package com.example.mono.core.data.model

import com.example.mono.core.database.model.SubTaskEntity
import com.example.mono.core.model.SubTask

fun SubTask.asEntity() = SubTaskEntity(
    id = id,
    title = title,
    isCompleted = isCompleted,
    taskId = taskId
)