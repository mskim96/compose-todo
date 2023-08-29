package com.example.mono.core.common.datetime

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.time.temporal.ChronoUnit
import java.util.Locale

/**
 * An extension function that convert a [LocalDate] to formatted date string.
 *
 * ```
 * eg. Aug 29, 2023..
 * ```
 */
fun LocalDate.toFormattedDate(): String {
    val dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
        .withLocale(Locale.getDefault())
    return this.format(dateFormatter)
}

/**
 * An extension function that converts a [LocalDate] to milli seconds.
 */
fun LocalDate.toDateMillis(): Long {
    val epochDays = ChronoUnit.DAYS.between(LocalDate.of(1970, 1, 1), this)
    return epochDays * 24 * 60 * 60 * 1000
}

/**
 * An extension function that convert milli seconds to [LocalDate]
 */
fun Long?.toLocalDate(): LocalDate {
    val selectedDateInstant = this?.let { Instant.ofEpochMilli(it) }
    return selectedDateInstant?.atZone(ZoneId.systemDefault())?.toLocalDate() ?: LocalDate.now()
}