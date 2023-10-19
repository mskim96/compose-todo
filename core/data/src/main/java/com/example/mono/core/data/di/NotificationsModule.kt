package com.example.mono.core.data.di

import com.example.mono.core.data.notification.Notifier
import com.example.mono.core.data.notification.SystemTrayNotifier
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface NotificationsModule {

    @Binds
    fun bindsNotifier(notifier: SystemTrayNotifier): Notifier
}