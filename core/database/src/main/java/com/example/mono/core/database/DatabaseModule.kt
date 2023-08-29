package com.example.mono.core.database

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun providesMonoDatabase(
        @ApplicationContext context: Context
    ): MonoDatabase = Room.databaseBuilder(
        context = context,
        klass = MonoDatabase::class.java,
        name = "mono-database"
    ).build()
}