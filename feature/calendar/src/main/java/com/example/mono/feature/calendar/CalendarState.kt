package com.example.mono.feature.calendar

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.MutatePriority
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollScope
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.gestures.snapping.SnapLayoutInfoProvider
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.Density
import com.example.mono.core.common.datetime.getMonthIndex
import com.example.mono.core.common.datetime.getNumberWeeks
import com.example.mono.core.model.calendar.Month
import com.example.mono.core.model.calendar.Week
import java.time.LocalDate
import java.time.YearMonth

@Composable
fun rememberCalendarState(
    calendarStartDate: LocalDate = defaultCalendarStartDate,
    calendarEndDate: LocalDate = defaultCalendarEndDate,
    firstVisibleDate: LocalDate = LocalDate.now()
) = remember(
    calendarStartDate,
    calendarEndDate,
    firstVisibleDate
) {
    CalendarState(
        calendarStartDate = calendarStartDate,
        calendarEndDate = calendarEndDate,
        firstVisibleDate = firstVisibleDate
    )
}

@Stable
class CalendarState(
    private val calendarStartDate: LocalDate,
    private val calendarEndDate: LocalDate,
    firstVisibleDate: LocalDate
) : ScrollableState {

    var monthIndexCount by mutableIntStateOf(0)

    val store = CalendarStore { offset ->
        updateMonthDate(
            startDate = calendarStartDate,
            offset = offset
        )
    }

    init {
        monthIndexCount = getMonthIndex(
            startMonth = calendarStartDate,
            targetMonth = calendarEndDate
        ) + 1
    }

    private fun updateMonthDate(startDate: LocalDate, offset: Int = 0): Month {
        val yearMonth = YearMonth.from(startDate).plusMonths(offset.toLong())
        val numberWeeks = yearMonth.getNumberWeeks()
        return Month(
            yearMonth = yearMonth,
            weeks = (0 until numberWeeks).map { week ->
                Week(
                    number = week,
                    yearMonth = yearMonth
                )
            }
        )
    }

    // ---------------------------------------------------
    // Related for scroll state.
    // ---------------------------------------------------
    val listState = LazyListState(
        firstVisibleItemIndex = getScrollIndex(firstVisibleDate) ?: 0
    )

    val firstVisibleMonth: Month by derivedStateOf {
        store[listState.firstVisibleItemIndex]
    }

    suspend fun animateScrollToMonth(month: LocalDate) {
        listState.animateScrollToItem(getScrollIndex(month) ?: return)
    }

    private fun getScrollIndex(date: LocalDate): Int? {
        if (date !in calendarStartDate..calendarEndDate) {
            return null
        }
        return getMonthIndex(calendarStartDate, date)
    }

    override val isScrollInProgress: Boolean
        get() = listState.isScrollInProgress

    override fun dispatchRawDelta(delta: Float) = listState.dispatchRawDelta(delta)

    override suspend fun scroll(
        scrollPriority: MutatePriority,
        block: suspend ScrollScope.() -> Unit
    ) = listState.scroll(scrollPriority, block)
}

object CalendarDefaults {

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    fun pagedFlingBehavior(state: LazyListState): FlingBehavior {
        val snappingLayout = remember(state) {
            val provider = SnapLayoutInfoProvider(state) { _, _, _ -> 0 }
            calendarSnapLayoutInfoProvider(provider)
        }
        return rememberSnapFlingBehavior(snapLayoutInfoProvider = snappingLayout)
    }
}

@OptIn(ExperimentalFoundationApi::class)
private fun calendarSnapLayoutInfoProvider(
    snapLayoutInfoProvider: SnapLayoutInfoProvider
): SnapLayoutInfoProvider =
    object : SnapLayoutInfoProvider by snapLayoutInfoProvider {
        override fun Density.calculateApproachOffset(initialVelocity: Float): Float {
            return 0f
        }
    }

internal val defaultCalendarStartDate = LocalDate.of(
    1970, 1, 1
)

internal val defaultCalendarEndDate = LocalDate.of(
    2100, 12, 31
)