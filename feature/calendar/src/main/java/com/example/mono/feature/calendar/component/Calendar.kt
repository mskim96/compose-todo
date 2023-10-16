package com.example.mono.feature.calendar.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import com.example.mono.core.model.calendar.Month
import com.example.mono.core.model.task.Task
import com.example.mono.feature.calendar.CalendarDefaults
import com.example.mono.feature.calendar.CalendarState
import com.example.mono.feature.calendar.CalendarUiState
import com.example.mono.feature.calendar.rememberCalendarState
import java.time.LocalDate

@Composable
fun Calendar(
    calendarUiState: CalendarUiState,
    onDateSelected: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
    calendarState: CalendarState = rememberCalendarState()
) {
    DisposableEffect(key1 = calendarState) {
        onDispose { calendarState.store.clear() }
    }

    LazyRow(
        modifier = modifier,
        state = calendarState.listState,
        flingBehavior = CalendarDefaults.pagedFlingBehavior(calendarState.listState)
    ) {
        items(
            count = calendarState.monthIndexCount,
            key = { offset -> "Month with $offset" }
        ) { offset ->
            val month = calendarState.store[offset]
            ItemsCalendarMonth(
                month = month,
                selectedDate = calendarUiState.selectedDate,
                taskIndicators = calendarUiState.taskIndicator,
                onDayClicked = onDateSelected,
                modifier = Modifier.fillParentMaxWidth()
            )
        }
    }
}

@Composable
private fun ItemsCalendarMonth(
    month: Month,
    selectedDate: LocalDate,
    taskIndicators: Map<LocalDate?, List<Task>>,
    onDayClicked: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier) {
        Row {
            DaysOfWeek(modifier = Modifier.fillMaxWidth())
        }
        month.weeks.forEach { week ->
            key("${week.yearMonth} / ${week.number}") {
                Week(
                    week = week,
                    selectedDate = selectedDate,
                    taskIndicators = taskIndicators,
                    onDayClicked = onDayClicked,
                    modifier = Modifier
                )
            }
        }
    }
}