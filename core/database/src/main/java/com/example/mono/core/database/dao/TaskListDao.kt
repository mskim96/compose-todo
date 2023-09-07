package com.example.mono.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.mono.core.database.model.TaskListEntity
import kotlinx.coroutines.flow.Flow

/**
 * Dao for [TaskListEntity] access.
 */
@Dao
interface TaskListDao {

    /**
     * Observe list of task list entity.
     *
     * @return Task list entities.
     */
    @Query(value = "SELECT * FROM task_lists")
    fun getTaskLists(): Flow<List<TaskListEntity>>

    /**
     * Observe a single task list.
     *
     * @param taskListId the task list id.
     * @return the task list with task list id.
     */
    @Query(
        value = """
            SELECT * FROM task_lists
            WHERE id = :taskListId
        """
    )
    fun getTaskList(taskListId: String): Flow<TaskListEntity>

    /**
     * Select a task list entity by id.
     *
     * @param taskListId the task list id.
     * @return the task list with task list id.
     */
    @Query(value = "SELECT * FROM task_lists WHERE id = :taskListId")
    suspend fun getOneOffTaskListEntity(taskListId: String): TaskListEntity?

    /**
     * Insert or update a task list in the database. If a task list already exists, replace it.
     *
     * @param taskListEntity the task list to be inserted or updated.
     */
    @Upsert
    suspend fun upsertTaskList(taskListEntity: TaskListEntity)

    /**
     * Delete a task list by id.
     *
     * @param taskListId the task list id.
     */
    @Query(value = "DELETE FROM task_lists WHERE id = :taskListId")
    suspend fun deleteTaskList(taskListId: String)
}