package com.example.mono.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.mono.core.database.model.SubTaskEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SubTaskDao {

    @Query(
        value = """
            SELECT * FROM sub_tasks
            WHERE id = :subTaskId
        """
    )
    fun getSubTask(subTaskId: String): Flow<SubTaskEntity>

    @Query(
        value = """
            SELECT * FROM sub_tasks
            WHERE taskId = :taskId
        """
    )
    fun getSubTasks(taskId: String): Flow<List<SubTaskEntity>>

    @Query(value = "SELECT * FROM sub_tasks")
    fun getSubTasks(): Flow<List<SubTaskEntity>>

    @Query(
        value = """
            SELECT * FROM sub_tasks
            WHERE id = :subTaskId
        """
    )
    fun getOnOffSubTask(subTaskId: String): SubTaskEntity?

    @Upsert
    suspend fun upsertSubTask(subTaskEntity: SubTaskEntity)

    @Upsert
    suspend fun upsertSubTasks(subTaskEntities: List<SubTaskEntity>)

    @Query(
        value = """
            UPDATE sub_tasks SET isCompleted = :completed
            WHERE id = :subTaskId
        """
    )
    suspend fun updateCompleted(subTaskId: String, completed: Boolean)

    @Query(
        value = """
            DELETE FROM sub_tasks
            WHERE id in (:taskIds)
        """
    )
    suspend fun deleteSubTasks(taskIds: Set<String>)
}