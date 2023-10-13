package com.example.mono.core.data.model

import com.example.mono.core.database.model.TaskListEntity
import com.example.mono.core.model.task.TaskList

/**
 * Converting the [TaskList] external model to a [TaskListEntity].
 */
fun TaskList.asEntity() = TaskListEntity(
    id = id,
    name = name
)