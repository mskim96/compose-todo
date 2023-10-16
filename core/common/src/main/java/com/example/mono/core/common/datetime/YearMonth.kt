package com.example.mono.core.common.datetime

import java.time.LocalDate
import java.time.Period
import java.time.YearMonth
import java.time.temporal.WeekFields

fun YearMonth.getNumberWeeks(weekFields: WeekFields = WeekFields.ISO): Int {
    val firstWeekNumber = this.atDay(1)[weekFields.weekOfMonth()]
    val lastWeekNumber = this.atEndOfMonth()[weekFields.weekOfMonth()]
    return lastWeekNumber - firstWeekNumber + 2
}

fun getMonthIndex(startMonth: LocalDate, targetMonth: LocalDate): Int {
    return Period.between(startMonth, targetMonth).toTotalMonths().toInt()
}