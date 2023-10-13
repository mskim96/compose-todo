package com.example.mono.core.data.repository

import com.example.mono.core.model.task.Task
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.LocalTime

data class TaskQuery(
    val filterTaskListIds: Set<String>? = null,
    val filterBookmark: Boolean? = null
)

interface TaskRepository {

    fun getTasksStream(
        query: TaskQuery = TaskQuery(
            filterTaskListIds = null,
            filterBookmark = null
        )
    ): Flow<List<Task>>

    /**
     * Gets the tasks as a stream.
     *
     * @return all tasks as a stream.
     */
    fun getTasksStream(): Flow<List<Task>>

    /**
     * Gets data for a specific task as a stream.
     *
     * @param taskId the task id.
     * @return the task with taskId as a stream.
     */
    fun getTaskStream(taskId: String): Flow<Task>

    /**
     * Gets data for a specific task using a One shot.
     *
     * @param taskId the task id.
     * @return [Task] or null.
     */
    suspend fun getTask(taskId: String): Task?

    /**
     * Create the new task.
     *
     * @param title title of the task.
     * @param detail description of the task.
     * @param isBookmarked The bookmarked status of the task.
     * @param date date of the task.
     * @param time time of the task.
     * @param taskListId taskListId of the task.
     *
     * @return the task of id.
     */
    suspend fun createTask(
        title: String,
        detail: String,
        isBookmarked: Boolean,
        date: LocalDate?,
        time: LocalTime?,
        taskListId: String?
    ): String

    /**
     * Update the task.
     *
     * @param taskId the task id.
     * @param title title of the task.
     * @param detail description of the task.
     * @param date date of the task.
     * @param time time of the task.
     */
    suspend fun updateTask(
        taskId: String,
        title: String,
        detail: String,
        date: LocalDate?,
        time: LocalTime?,
        color: Long,
        taskListId: String?
    )

    /**
     * Update the complete status or active the task.
     *
     * @param taskId the task id.
     * @param completed completed or active status of the task.
     */
    suspend fun updateCompleteTask(taskId: String, completed: Boolean)

    /**
     * Add a bookmark or remove bookmark to the task.
     *
     * @param taskId the task id.
     */
    suspend fun updateTaskBookmark(taskId: String, bookmarked: Boolean)

    /**
     * Delete all complete tasks.
     */
    suspend fun clearCompletedTask()

    suspend fun clearCompletedTask(
        query: TaskQuery = TaskQuery(
            filterTaskListIds = null,
            filterBookmark = null
        )
    )

    /**
     * Delete a specific task.
     *
     * @param taskId the task id.
     */
    suspend fun deleteTask(taskId: String)
}