package com.example.mono.core.common.datetime

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneOffset

fun dateTimeToMillis(date: LocalDate, time: LocalTime): Long {
    val japanZoneOffset = ZoneOffset.ofHours(9)
    return LocalDateTime.of(date, time).toInstant(japanZoneOffset).toEpochMilli()
}