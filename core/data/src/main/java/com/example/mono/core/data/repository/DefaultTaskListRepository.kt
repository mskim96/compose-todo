package com.example.mono.core.data.repository

import com.example.mono.core.common.scope.Dispatcher
import com.example.mono.core.common.scope.MonoDispatchers.Default
import com.example.mono.core.data.model.asEntity
import com.example.mono.core.database.dao.TaskListDao
import com.example.mono.core.database.model.TaskListEntity
import com.example.mono.core.database.model.asExternalModel
import com.example.mono.core.model.task.TaskList
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject

class DefaultTaskListRepository @Inject constructor(
    private val taskListDao: TaskListDao,
    @Dispatcher(Default) private val dispatcher: CoroutineDispatcher
) : TaskListRepository {

    override fun getTaskLists(): Flow<List<TaskList>> =
        taskListDao.getTaskLists()
            .map { taskList -> taskList.map(TaskListEntity::asExternalModel) }

    override fun getTaskList(taskListId: String): Flow<TaskList> =
        taskListDao.getTaskList(taskListId).map(TaskListEntity::asExternalModel)

    override suspend fun getOneOffTaskList(taskListId: String): TaskList? =
        taskListDao.getOneOffTaskListEntity(taskListId)?.asExternalModel()

    override suspend fun createTaskList(name: String) {
        val groupId = withContext(dispatcher) {
            UUID.randomUUID().toString()
        }
        val taskList = TaskList(
            id = groupId,
            name = name
        )
        taskListDao.upsertTaskList(taskList.asEntity())
    }

    override suspend fun updateTaskList(taskListId: String, name: String) {
        val taskList = getOneOffTaskList(taskListId)
            ?.copy(name = name)
            ?: throw Exception("Task list (id $taskListId) not found.")
        taskListDao.upsertTaskList(taskList.asEntity())
    }

    override suspend fun deleteTaskList(taskListId: String) {
        taskListDao.deleteTaskList(taskListId)
    }
}