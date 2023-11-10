package com.example.mono.core.data.repository

import com.example.mono.core.common.datetime.dateTimeToMillis
import com.example.mono.core.common.scope.Dispatcher
import com.example.mono.core.common.scope.MonoDispatchers.Default
import com.example.mono.core.data.model.asEntity
import com.example.mono.core.data.notification.Notifier
import com.example.mono.core.database.dao.TaskDao
import com.example.mono.core.database.model.PopulatedTaskEntity
import com.example.mono.core.database.model.asExternalModel
import com.example.mono.core.model.task.Task
import com.example.mono.core.model.task.TaskColorPalette
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
    private val notifier: Notifier,
    @Dispatcher(Default) private val dispatcher: CoroutineDispatcher
) : TaskRepository {
    override fun getTasksStream(query: TaskQuery): Flow<List<Task>> = taskDao.getTasks(
        useFilterTaskListIds = query.filterTaskListIds != null,
        filterTaskListIds = query.filterTaskListIds ?: emptySet(),
        useFilterBookmark = query.filterBookmark ?: false
    )
        .map { it.map(PopulatedTaskEntity::asExternalModel) }

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
            color = TaskColorPalette.Burgundy.color,
            isCompleted = false,
            isBookmarked = isBookmarked,
            isPendingNotification = checkPendingNotification(date, time),
            taskListId = taskListId
        )
        notifier.setTaskNotification(
            taskId, title, detail, date, time, task.isPendingNotification
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
        color: Long,
        taskListId: String?,
        attachments: List<String>,
        recorders: List<String>
    ) {
        val task = getTask(taskId)?.copy(
            title = title,
            detail = detail,
            date = date,
            time = time,
            color = color,
            isPendingNotification = checkPendingNotification(date, time),
            taskListId = taskListId,
            attachments = attachments,
            recorders = recorders
        ) ?: throw Exception("Task (id $taskId) not found.")
        notifier.setTaskNotification(
            taskId, title, detail, date, time, task.isPendingNotification
        )
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

    override suspend fun clearCompletedTask(query: TaskQuery) {
        taskDao.deleteCompleted(
            useFilterTaskListIds = query.filterTaskListIds != null,
            filterTaskListIds = query.filterTaskListIds ?: emptySet(),
            useFilterBookmark = query.filterBookmark ?: false
        )
    }

    override suspend fun deleteTask(taskId: String) {
        taskDao.deleteTask(taskId)
    }

    override suspend fun completeNotification(taskId: String) {
        val task = getTask(taskId)?.copy(
            isPendingNotification = false
        ) ?: throw Exception("Task (id $taskId) not found.")
        taskDao.upsertTask(task.asEntity())
    }

    override suspend fun updateAttachments(taskId: String, attachments: List<String>) {
        val task = getTask(taskId)?.copy(
            attachments = attachments
        ) ?: throw Exception("Task (id $taskId) not found.")
        taskDao.upsertTask(task.asEntity())
    }

    override suspend fun updateRecord(taskId: String, recordUri: List<String>) {
        val task = getTask(taskId)?.copy(
            recorders = recordUri
        ) ?: throw Exception("Task (id $taskId) not found.")
        taskDao.upsertTask(task.asEntity())
    }

    private fun checkPendingNotification(date: LocalDate?, time: LocalTime?): Boolean {
        return if (date != null && time != null) {
            val currentTime = dateTimeToMillis(LocalDate.now(), LocalTime.now())
            val dateTime = dateTimeToMillis(date, time)
            currentTime < dateTime
        } else {
            false
        }
    }
}