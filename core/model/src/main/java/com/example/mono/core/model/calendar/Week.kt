package com.example.mono.core.model.calendar

import java.time.YearMonth

/**
 * Wrapper class for Week.
 *
 * @property number the week number.
 * @property yearMonth week of the month.
 */
data class Week(
    val number: Int,
    val yearMonth: YearMonth
)
