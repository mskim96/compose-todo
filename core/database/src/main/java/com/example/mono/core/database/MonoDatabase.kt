package com.example.mono.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.mono.core.database.dao.SubTaskDao
import com.example.mono.core.database.dao.TaskDao
import com.example.mono.core.database.dao.TaskListDao
import com.example.mono.core.database.model.SubTaskEntity
import com.example.mono.core.database.model.TaskEntity
import com.example.mono.core.database.model.TaskListEntity
import com.example.mono.core.database.util.DateTimeConverter

/**
 * [MonoDatabase] is Room Database for App.
 *
 * TODO: ExportSchema should be true in production database.
 */
@Database(
    entities = [
        TaskEntity::class,
        SubTaskEntity::class,
        TaskListEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(DateTimeConverter::class)
abstract class MonoDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
    abstract fun subTaskDao(): SubTaskDao
    abstract fun taskGroupDao(): TaskListDao
}