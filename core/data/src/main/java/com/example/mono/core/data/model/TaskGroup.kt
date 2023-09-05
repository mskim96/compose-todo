package com.example.mono.core.data.model

import com.example.mono.core.database.model.TaskGroupEntity
import com.example.mono.core.model.TaskGroup

fun TaskGroup.asEntity() = TaskGroupEntity(
    id = id,
    name = name
)
