package com.example.mono.core.data.repository

import com.example.mono.core.model.SubTask
import kotlinx.coroutines.flow.Flow

interface SubTaskRepository {

    fun getSubTasksStream(): Flow<List<SubTask>>

    fun getSubTasksStream(filteredTaskId: String): Flow<List<SubTask>>

    fun getSubTaskStream(subTaskId: String): Flow<SubTask>

    suspend fun getSubTask(subTaskId: String): SubTask?

    suspend fun createSubTask(taskId: String)

    suspend fun updateSubTask(subTaskId: String, title: String)

    suspend fun updateCompletedSubTask(subTaskId: String, completed: Boolean)

    suspend fun deleteSubTask(subTaskId: String)
}