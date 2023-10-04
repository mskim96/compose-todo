package com.example.mono.core.notifications

import com.example.mono.core.notifications.notifier.Notifier
import com.example.mono.core.notifications.notifier.SystemTrayNotifier
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