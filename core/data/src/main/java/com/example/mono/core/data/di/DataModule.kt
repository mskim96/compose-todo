package com.example.mono.core.data.di

import com.example.mono.core.data.repository.DefaultTaskListRepository
import com.example.mono.core.data.repository.DefaultTaskRepository
import com.example.mono.core.data.repository.TaskListRepository
import com.example.mono.core.data.repository.TaskRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface DataModule {

    @Binds
    fun bindsTaskRepository(taskRepository: DefaultTaskRepository): TaskRepository

    @Binds
    fun bindsTaskGroupRepository(taskGroupRepository: DefaultTaskListRepository): TaskListRepository
}