package com.example.mono.core.data.di

import com.example.mono.core.data.repository.DefaultTaskGroupRepository
import com.example.mono.core.data.repository.DefaultTaskRepository
import com.example.mono.core.data.repository.TaskGroupRepository
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
    fun bindsTaskGroupRepository(taskGroupRepository: DefaultTaskGroupRepository): TaskGroupRepository
}