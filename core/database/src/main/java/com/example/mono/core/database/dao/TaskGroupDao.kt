package com.example.mono.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.mono.core.database.model.TaskGroupEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskGroupDao {

    @Query("SELECT * FROM task_group")
    fun getTaskGroups(): Flow<List<TaskGroupEntity>>

    @Query("SELECT * FROM task_group WHERE id = :groupId")
    fun getTaskGroup(groupId: String): Flow<TaskGroupEntity>

    @Query("SELECT * FROM task_group WHERE id = :groupId")
    suspend fun getOneOffTaskGroup(groupId: String): TaskGroupEntity?

    @Upsert
    suspend fun upsert(taskGroupEntity: TaskGroupEntity)

    @Query("DELETE FROM task_group WHERE id = :groupId")
    suspend fun deleteTaskGroup(groupId: String)
}