package com.example.mono.core.common.datetime

import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

/**
 * An extension function that convert a [LocalTime] to formatted date string.
 *
 * ```
 * eg. AM 10:55, PM 08:25
 * ```
 */
fun LocalTime.toFormattedTime(): String {
    val timeFormatter = DateTimeFormatter.ofPattern(DEFAULT_TIME_FORMAT)
        .withLocale(Locale.getDefault())
    return this.format(timeFormatter)
}

const val DEFAULT_TIME_FORMAT = "a h:mm"