package com.example.mono.core.database.util

import androidx.room.TypeConverter
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.time.LocalDate
import java.time.LocalTime

class Converters {
    @TypeConverter
    fun toLocalDate(value: String?) = value?.let { LocalDate.parse(value) }

    @TypeConverter
    fun fromLocalDate(date: LocalDate?) = date?.toString()

    @TypeConverter
    fun toLocalTime(value: String?) = value?.let { LocalTime.parse(value) }

    @TypeConverter
    fun fromLocalTime(time: LocalTime?) = time?.toString()

    @TypeConverter
    fun fromRecorder(recorders: List<String>) = Json.encodeToString(recorders)

    @TypeConverter
    fun toRecorder(value: String?) = value?.let { Json.decodeFromString<List<String>>(value) }
}