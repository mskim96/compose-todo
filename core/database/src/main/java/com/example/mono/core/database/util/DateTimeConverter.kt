package com.example.mono.core.database.util

import androidx.room.TypeConverter
import java.time.LocalDate
import java.time.LocalTime

class DateTimeConverter {
    @TypeConverter
    fun toLocalDate(value: String?) = value?.let { LocalDate.parse(value) }

    @TypeConverter
    fun fromLocalDate(date: LocalDate?) = date?.toString()

    @TypeConverter
    fun toLocalTime(value: String?) = value?.let { LocalTime.parse(value) }

    @TypeConverter
    fun fromLocalTime(time: LocalTime?) = time?.toString()
}