package com.example.mono.core.data.repository

import com.example.mono.core.common.scope.Dispatcher
import com.example.mono.core.common.scope.MonoDispatchers
import com.example.mono.core.data.model.asEntity
import com.example.mono.core.database.dao.SubTaskDao
import com.example.mono.core.database.model.SubTaskEntity
import com.example.mono.core.database.model.asExternalModel
import com.example.mono.core.model.SubTask
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject

class DefaultSubTaskRepository @Inject constructor(
    private val subTaskDao: SubTaskDao,
    @Dispatcher(MonoDispatchers.Default) private val dispatcher: CoroutineDispatcher
) : SubTaskRepository {

    override fun getSubTasksStream(): Flow<List<SubTask>> =
        subTaskDao.getSubTasks().map { it.map(SubTaskEntity::asExternalModel) }

    override fun getSubTasksStream(filteredTaskId: String): Flow<List<SubTask>> =
        subTaskDao.getSubTasks(filteredTaskId).map { it.map(SubTaskEntity::asExternalModel) }

    override fun getSubTaskStream(subTaskId: String): Flow<SubTask> =
        subTaskDao.getSubTask(subTaskId).map(SubTaskEntity::asExternalModel)

    override suspend fun getSubTask(subTaskId: String): SubTask? =
        subTaskDao.getOnOffSubTask(subTaskId)?.asExternalModel()

    override suspend fun createSubTask(taskId: String) {
        val subTaskId = withContext(dispatcher) {
            UUID.randomUUID().toString()
        }
        val subTask = SubTask(
            id = subTaskId,
            taskId = taskId
        )
        subTaskDao.upsertSubTask(subTask.asEntity())
    }

    override suspend fun updateSubTask(subTaskId: String, title: String) {
        val subTask = getSubTask(subTaskId)?.copy(title = title)
            ?: throw Exception("SubTask (id $subTaskId) not found.")
        subTaskDao.upsertSubTask(subTask.asEntity())
    }

    override suspend fun updateCompletedSubTask(subTaskId: String, completed: Boolean) {
        subTaskDao.updateCompleted(subTaskId = subTaskId, completed = completed)
    }

    override suspend fun deleteSubTask(subTaskId: String) {
        subTaskDao.deleteSubTasks(setOf(subTaskId))
    }
}