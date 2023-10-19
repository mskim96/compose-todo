package com.example.mono.core.data.notification

import com.example.mono.core.model.task.Task
import java.time.LocalDate
import java.time.LocalTime

interface Notifier {
    fun setTaskNotification(
        taskId: String,
        title: String,
        detail: String,
        date: LocalDate?,
        time: LocalTime?,
        isPendingNotification: Boolean
    ): Boolean
}