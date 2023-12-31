package com.example.mono.core.database

import com.example.mono.core.database.dao.SubTaskDao
import com.example.mono.core.database.dao.TaskDao
import com.example.mono.core.database.dao.TaskListDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object DaosModule {

    @Provides
    fun providesTaskDao(
        database: MonoDatabase
    ): TaskDao = database.taskDao()

    @Provides
    fun providesSubTaskDao(
        database: MonoDatabase
    ): SubTaskDao = database.subTaskDao()

    @Provides
    fun providesTaskGroupDao(
        database: MonoDatabase
    ): TaskListDao = database.taskGroupDao()
}