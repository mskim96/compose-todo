package com.example.mono.core.data.repository

import com.example.mono.core.model.task.TaskList
import kotlinx.coroutines.flow.Flow

interface TaskListRepository {

    fun getTaskLists(): Flow<List<TaskList>>

    fun getTaskList(taskListId: String): Flow<TaskList>

    suspend fun getOneOffTaskList(taskListId: String): TaskList?

    suspend fun createTaskList(name: String)

    suspend fun updateTaskList(taskListId: String, name: String)

    suspend fun deleteTaskList(taskListId: String)
}