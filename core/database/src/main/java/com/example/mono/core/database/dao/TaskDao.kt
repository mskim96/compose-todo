package com.example.mono.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.mono.core.database.model.TaskEntity
import kotlinx.coroutines.flow.Flow

/**
 * Dao for [TaskEntity] access.
 */
@Dao
interface TaskDao {

    /**
     * Observe a single task.
     *
     * @param taskId the task id.
     * @return the task with taskId.
     */
    @Query(
        value = """
            SELECT * FROM tasks 
            WHERE id = :taskId
        """
    )
    fun getTaskEntity(taskId: String): Flow<TaskEntity>

    /**
     * Observe list of tasks.
     *
     * @return All task entities.
     */
    @Query(value = "SELECT * FROM tasks")
    fun getTaskEntities(): Flow<List<TaskEntity>>

    /**
     * Select a task by id.
     *
     * @param taskId the task id.
     * @return the task with taskId.
     */
    @Query(
        value = """
            SELECT * FROM tasks 
            WHERE id = :taskId
        """
    )
    suspend fun getOnOffTaskEntity(taskId: String): TaskEntity?

    /**
     * Insert of update a task in the database. If a task already exists, replace it.
     *
     * @param task the task to be inserted or updated.
     */
    @Upsert
    suspend fun upsertTask(task: TaskEntity)


    /**
     * Update the complete status of a task.
     *
     * @param taskId the task id.
     * @param isCompleted change the task's state from completed to active, or vice versa.
     */
    @Query(
        value = """
            UPDATE tasks SET isCompleted = :isCompleted
            WHERE id = :taskId
        """
    )
    suspend fun updateCompleted(taskId: String, isCompleted: Boolean)

    /**
     * Update the bookmark status of a task.
     *
     * @param taskId the task id.
     * @param isBookmarked
     */
    @Query(
        value = """
            UPDATE tasks SET isBookmarked = :isBookmarked
            WHERE id = :taskId
        """
    )
    suspend fun updateBookmarked(taskId: String, isBookmarked: Boolean)

    /**
     * Delete a task by id.
     *
     * @param taskId the task id.
     * @return the number of tasks deleted. This should always be 1.
     */
    @Query(
        value = """
            DELETE FROM tasks 
            WHERE id = :taskId
        """
    )
    suspend fun deleteTask(taskId: String): Int

    /**
     * Delete all completed tasks from the table.
     *
     * @return the number of the tasks delete.
     */
    @Query(value = "DELETE FROM tasks WHERE isCompleted = 1")
    suspend fun deleteCompleted(): Int
}