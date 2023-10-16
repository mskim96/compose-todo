package com.example.mono.feature.calendar.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.mono.core.model.calendar.Week
import com.example.mono.core.model.task.Task
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters

@Composable
fun DaysOfWeek(modifier: Modifier = Modifier) {
    val dayOfWeeks = DayOfWeek.values()
    val startDayIndex = 6
    Row(modifier) {
        for (i in dayOfWeeks.indices) {
            val dayIndex = (startDayIndex + i) % dayOfWeeks.size
            val day = dayOfWeeks[dayIndex]
            DayOfWeekHeading(day = day.name.take(1), modifier = Modifier.weight(1f))
        }
    }
}

@Composable
fun Week(
    week: Week,
    selectedDate: LocalDate,
    taskIndicators: Map<LocalDate?, List<Task>>,
    onDayClicked: (LocalDate) -> Unit,
    modifier: Modifier
) {
    val beginningWeek = week.yearMonth.atDay(1).plusWeeks(week.number.toLong())
    var currentDay = beginningWeek.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY))
    val today = LocalDate.now()

    Box {
        Row(modifier = modifier) {
            for (i in 0..6) {
                if (currentDay.month == week.yearMonth.month) {
                    Day(
                        day = currentDay,
                        isToday = currentDay == today,
                        isSelected = currentDay == selectedDate,
                        taskIndicator = taskIndicators[currentDay].orEmpty(),
                        onDayClicked = onDayClicked,
                        modifier = Modifier.weight(1f)
                    )
                } else {
                    Box(modifier = Modifier.weight(1f))
                }
                currentDay = currentDay.plusDays(1)
            }
        }
    }
}