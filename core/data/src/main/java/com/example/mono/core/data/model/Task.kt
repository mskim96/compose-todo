package com.example.mono.core.data.model

import com.example.mono.core.database.model.TaskEntity
import com.example.mono.core.model.Task

/**
 * Converting the [Task] external model to a [TaskEntity].
 */
fun Task.asEntity() = TaskEntity(
    id = id,
    title = title,
    description = description,
    isCompleted = isCompleted,
    isBookmarked = isBookmarked,
    date = date,
    time = time,
    groupId = groupId
)