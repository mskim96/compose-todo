package com.example.mono.core.data.repository

import com.example.mono.core.common.scope.Dispatcher
import com.example.mono.core.common.scope.MonoDispatchers.Default
import com.example.mono.core.data.model.asEntity
import com.example.mono.core.database.dao.TaskGroupDao
import com.example.mono.core.database.model.TaskGroupEntity
import com.example.mono.core.database.model.asExternalModel
import com.example.mono.core.model.TaskGroup
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject

class DefaultTaskGroupRepository @Inject constructor(
    private val localDatasource: TaskGroupDao,
    @Dispatcher(Default) private val dispatcher: CoroutineDispatcher
) : TaskGroupRepository {
    override fun getTaskGroups(): Flow<List<TaskGroup>> =
        localDatasource.getTaskGroups().map { taskGroup ->
            taskGroup.map(TaskGroupEntity::asExternalModel)
        }

    override fun getTaskGroup(groupId: String): Flow<TaskGroup> =
        localDatasource.getTaskGroup(groupId).map(TaskGroupEntity::asExternalModel)

    override suspend fun getOneOffTaskGroup(groupId: String): TaskGroup? =
        localDatasource.getOneOffTaskGroup(groupId)?.asExternalModel()

    override suspend fun createTaskGroup(name: String) {
        val groupId = withContext(dispatcher) {
            UUID.randomUUID().toString()
        }
        val taskGroup = TaskGroup(
            id = groupId,
            name = name
        )
        localDatasource.upsert(taskGroup.asEntity())
    }

    override suspend fun updateTaskGroup(groupId: String, name: String) {
        val taskGroup = getOneOffTaskGroup(groupId)
            ?.copy(name = name) ?: throw Exception("Task group (id $groupId) not found.")
        localDatasource.upsert(taskGroup.asEntity())
    }

    override suspend fun deleteTaskGroup(groupId: String) {
        localDatasource.deleteTaskGroup(groupId)
    }
}