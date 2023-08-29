package com.example.mono.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.mono.core.database.model.TaskEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for the task table.
 *
 * TODO: Create method for filtered tasks flow. eg) get only bookmarked, ...
 */
@Dao
interface TaskDao {

    /**
     * Observe list of tasks.
     *
     * @return the task with taskId.
     */
    @Query("SELECT * FROM task")
    fun observeAll(): Flow<List<TaskEntity>>

    /**
     * Observe a single task.
     *
     * @param taskId the task id.
     * @return the task with taskId.
     */
    @Query("SELECT * FROM task WHERE id = :taskId")
    fun observeById(taskId: String): Flow<TaskEntity>

    /**
     * Select a task by id.
     *
     * @param taskId the task id.
     * @return the task with taskId.
     */
    @Query("SELECT * FROM task WHERE id = :taskId")
    suspend fun getById(taskId: String): TaskEntity?

    /**
     * Insert of update a task in the database. If a task already exists, replace it.
     *
     * @param task the task to be inserted or updated.
     */
    @Upsert
    suspend fun upsert(task: TaskEntity)

    /**
     * Update the complete status of a task.
     */
    @Query("UPDATE task SET isCompleted = :completed WHERE id = :taskId")
    suspend fun updateCompleted(taskId: String, completed: Boolean)

    /**
     * Update the bookmark status of a task.
     */
    @Query("UPDATE task SET isBookmarked = :bookmarked WHERE id = :taskId")
    suspend fun updateBookmarked(taskId: String, bookmarked: Boolean)

    /**
     * Delete a task by id.
     *
     * @return the number of tasks deleted. This should always be 1.
     */
    @Query("DELETE FROM task WHERE id = :taskId")
    suspend fun deleteById(taskId: String): Int

    /**
     * Delete all completed tasks from the table.
     *
     * @return the number of the tasks delete.
     */
    @Query("DELETE FROM task WHERE isCompleted = 1")
    suspend fun deleteCompleted(): Int
}