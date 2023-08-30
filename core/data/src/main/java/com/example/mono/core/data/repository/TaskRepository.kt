package com.example.mono.core.data.repository

import com.example.mono.core.model.Task
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.LocalTime

interface TaskRepository {

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
     * @param description description of the task.
     * @param isBookmarked The bookmarked status of the task.
     * @param date date of the task.
     * @param time time of the task.
     *
     * @return the task of id.
     */
    suspend fun createTask(
        title: String,
        description: String,
        isBookmarked: Boolean,
        date: LocalDate?,
        time: LocalTime?
    ): String

    /**
     * Update the task.
     *
     * @param taskId the task id.
     * @param title title of the task.
     * @param description description of the task.
     * @param isCompleted The completion status of the task.
     * @param isBookmarked The bookmarked status of the task.
     * @param date date of the task.
     * @param time time of the task.
     */
    suspend fun updateTask(
        taskId: String,
        title: String,
        description: String,
        isCompleted: Boolean,
        isBookmarked: Boolean,
        date: LocalDate?,
        time: LocalTime?
    )

    /**
     * Update the complete status of the task.
     *
     * @param taskId the task id.
     */
    suspend fun completeTask(taskId: String)

    /**
     * Update the active status of the task.
     *
     * @param taskId the task id.
     */
    suspend fun activeTask(taskId: String)

    /**
     * Add a bookmark to the task.
     *
     * @param taskId the task id.
     */
    suspend fun addTaskBookmark(taskId: String)

    /**
     * Remove the bookmark from the task.
     *
     * @param taskId the task id.
     */
    suspend fun removeTaskBookmark(taskId: String)

    /**
     * Delete all complete tasks.
     */
    suspend fun clearCompletedTask()

    /**
     * Delete a specific task.
     *
     * @param taskId the task id.
     */
    suspend fun deleteTask(taskId: String)
}