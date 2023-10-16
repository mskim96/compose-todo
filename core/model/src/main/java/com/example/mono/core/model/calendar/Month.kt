package com.example.mono.core.model.calendar

import java.time.YearMonth

/**
 * Wrapper class for Month.
 *
 * @property yearMonth the month.
 * @property weeks weeks of the month.
 */
data class Month(
    val yearMonth: YearMonth,
    val weeks: List<Week>
)
