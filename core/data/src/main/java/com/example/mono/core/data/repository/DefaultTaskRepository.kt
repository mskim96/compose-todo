package com.example.mono.core.data.repository

import com.example.mono.core.common.scope.Dispatcher
import com.example.mono.core.common.scope.MonoDispatchers.Default
import com.example.mono.core.data.model.asEntity
import com.example.mono.core.database.dao.TaskDao
import com.example.mono.core.database.model.TaskEntity
import com.example.mono.core.database.model.asExternalModel
import com.example.mono.core.model.Task
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.LocalTime
import java.util.UUID
import javax.inject.Inject

class DefaultTaskRepository @Inject constructor(
    private val localDatasource: TaskDao,
    @Dispatcher(Default) private val dispatcher: CoroutineDispatcher
) : TaskRepository {

    override fun getTasksStream(): Flow<List<Task>> = localDatasource.observeAll()
        .map { tasks ->
            withContext(dispatcher) {
                tasks.map(TaskEntity::asExternalModel)
            }
        }

    override fun getTaskStream(taskId: String): Flow<Task> =
        localDatasource.observeById(taskId).map(TaskEntity::asExternalModel)

    override suspend fun getTask(taskId: String): Task? =
        localDatasource.getById(taskId)?.asExternalModel()

    override suspend fun createTask(
        title: String,
        description: String,
        isBookmarked: Boolean,
        date: LocalDate?,
        time: LocalTime?,
        groupId: String
    ): String {
        val taskId = withContext(dispatcher) {
            UUID.randomUUID().toString()
        }
        val task = Task(
            id = taskId,
            title = title,
            description = description,
            isCompleted = false,
            isBookmarked = isBookmarked,
            date = date,
            time = time,
            groupId = groupId
        )
        localDatasource.upsert(task.asEntity())
        return taskId
    }

    override suspend fun updateTask(
        taskId: String,
        title: String,
        description: String,
        isCompleted: Boolean,
        isBookmarked: Boolean,
        date: LocalDate?,
        time: LocalTime?
    ) {
        val task = getTask(taskId)?.copy(
            title = title,
            description = description,
            isCompleted = isCompleted,
            isBookmarked = isBookmarked,
            date = date,
            time = time
        ) ?: throw Exception("Task (id $taskId) not found.")

        localDatasource.upsert(task.asEntity())
    }

    override suspend fun completeTask(taskId: String) {
        localDatasource.updateCompleted(taskId = taskId, completed = true)
    }

    override suspend fun activeTask(taskId: String) {
        localDatasource.updateCompleted(taskId = taskId, completed = false)
    }

    override suspend fun addTaskBookmark(taskId: String) {
        localDatasource.updateBookmarked(taskId = taskId, bookmarked = true)
    }

    override suspend fun removeTaskBookmark(taskId: String) {
        localDatasource.updateBookmarked(taskId = taskId, bookmarked = false)
    }

    override suspend fun clearCompletedTask() {
        localDatasource.deleteCompleted()
    }

    override suspend fun deleteTask(taskId: String) {
        localDatasource.deleteById(taskId)
    }
}