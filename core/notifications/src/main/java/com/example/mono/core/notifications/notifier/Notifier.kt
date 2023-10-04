package com.example.mono.core.notifications.notifier

import com.example.mono.core.model.Task

interface Notifier {
    fun setTaskNotification(task: Task)
}