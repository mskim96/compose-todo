package com.example.mono.core.notifications.notifier

import com.example.mono.core.model.task.Task

interface Notifier {
    fun setTaskNotification(task: Task)
}