package com.example.mono.core.data.repository

import com.example.mono.core.model.TaskGroup
import kotlinx.coroutines.flow.Flow

interface TaskGroupRepository {

    fun getTaskGroups(): Flow<List<TaskGroup>>

    fun getTaskGroup(groupId: String): Flow<TaskGroup>

    suspend fun getOneOffTaskGroup(groupId: String): TaskGroup?

    suspend fun createTaskGroup(name: String)

    suspend fun updateTaskGroup(groupId: String, name: String)

    suspend fun deleteTaskGroup(groupId: String)
}