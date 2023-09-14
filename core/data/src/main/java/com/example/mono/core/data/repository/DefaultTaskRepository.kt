package com.example.mono.core.data.repository

import com.example.mono.core.common.scope.Dispatcher
import com.example.mono.core.common.scope.MonoDispatchers.Default
import com.example.mono.core.data.model.asEntity
import com.example.mono.core.database.dao.TaskDao
import com.example.mono.core.database.model.PopulatedTaskEntity
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
    private val taskDao: TaskDao,
    @Dispatcher(Default) private val dispatcher: CoroutineDispatcher
) : TaskRepository {

    override fun getTasksStream(): Flow<List<Task>> = taskDao.getTasks()
        .map { tasks ->
            withContext(dispatcher) {
                tasks.map(PopulatedTaskEntity::asExternalModel)
            }
        }

    override fun getTaskStream(taskId: String): Flow<Task> =
        taskDao.getTask(taskId).map(PopulatedTaskEntity::asExternalModel)

    override suspend fun getTask(taskId: String): Task? =
        taskDao.getOnOffTask(taskId)?.asExternalModel()

    override suspend fun createTask(
        title: String,
        detail: String,
        isBookmarked: Boolean,
        date: LocalDate?,
        time: LocalTime?,
        taskListId: String?
    ): String {
        val taskId = withContext(dispatcher) {
            UUID.randomUUID().toString()
        }
        val task = Task(
            id = taskId,
            title = title,
            detail = detail,
            date = date,
            time = time,
            isCompleted = false,
            isBookmarked = isBookmarked,
            taskListId = taskListId
        )
        taskDao.upsertTask(task.asEntity())
        return taskId
    }

    override suspend fun updateTask(
        taskId: String,
        title: String,
        detail: String,
        date: LocalDate?,
        time: LocalTime?,
        taskListId: String?,
    ) {
        val task = getTask(taskId)?.copy(
            title = title,
            detail = detail,
            date = date,
            time = time,
            taskListId = taskListId
        ) ?: throw Exception("Task (id $taskId) not found.")

        taskDao.upsertTask(task.asEntity())
    }

    override suspend fun updateCompleteTask(taskId: String, completed: Boolean) {
        taskDao.updateCompleted(taskId = taskId, isCompleted = completed)
    }

    override suspend fun updateTaskBookmark(taskId: String, bookmarked: Boolean) {
        taskDao.setTaskBookmarked(taskId = taskId, bookmarked = bookmarked)
    }

    override suspend fun clearCompletedTask() {
        taskDao.deleteCompleted()
    }

    override suspend fun deleteTask(taskId: String) {
        taskDao.deleteTask(taskId)
    }
}