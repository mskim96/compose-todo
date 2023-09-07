package com.example.mono.core.data.repository

import com.example.mono.core.model.TaskList
import kotlinx.coroutines.flow.Flow

interface TaskListRepository {

    fun getTaskLists(): Flow<List<TaskList>>

    fun getTaskList(taskListId: String): Flow<TaskList>

    suspend fun getOneOffTaskGroup(taskListId: String): TaskList?

    suspend fun createTaskGroup(name: String)

    suspend fun updateTaskGroup(taskListId: String, name: String)

    suspend fun deleteTaskGroup(taskListId: String)
}