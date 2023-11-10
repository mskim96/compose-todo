package com.example.mono.core.data.model

import com.example.mono.core.database.model.TaskEntity
import com.example.mono.core.model.task.Task

/**
 * Converting the [Task] external model to a [TaskEntity].
 */
fun Task.asEntity() = TaskEntity(
    id = id,
    taskListId = taskListId,
    title = title,
    detail = detail,
    date = date,
    time = time,
    color = color,
    isCompleted = isCompleted,
    isBookmarked = isBookmarked,
    isPendingNotification = isPendingNotification,
    attachments = attachments,
    recorders = recorders
)