package com.example.mono.core.database.model

import androidx.room.Embedded
import androidx.room.Relation
import com.example.mono.core.model.task.Task

/**
 * External data layer representation of a fully populated Task.
 */
data class PopulatedTaskEntity(
    @Embedded
    val task: TaskEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "taskId"
    )
    val subTasks: List<SubTaskEntity>
)

fun PopulatedTaskEntity.asExternalModel() = Task(
    id = task.id,
    taskListId = task.taskListId,
    title = task.title,
    detail = task.detail,
    date = task.date,
    time = task.time,
    isCompleted = task.isCompleted,
    isBookmarked = task.isBookmarked,
    subTasks = subTasks.map(SubTaskEntity::asExternalModel)
)