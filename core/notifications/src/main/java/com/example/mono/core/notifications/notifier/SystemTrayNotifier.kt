package com.example.mono.core.notifications.notifier

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.mono.core.common.datetime.dateTimeToMillis
import com.example.mono.core.model.task.Task
import com.example.mono.core.notifications.NotifyReceiver
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject
import javax.inject.Singleton

private const val TASK_ID = "taskId"
private const val TASK_TITLE = "title"
private const val TASK_DETAIL = "detail"

/**
 * Implementation of [Notifier] that displays notifications in the system tray.
 */
@Singleton
class SystemTrayNotifier @Inject constructor(
    @ApplicationContext private val context: Context
) : Notifier {
    override fun setTaskNotification(task: Task) = with(context) {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pendingIntent = taskAlarmPendingIntent(task)
        alarmManager.setTaskAlarm(task.date, task.time, pendingIntent)
    }
}

private fun Context.taskAlarmPendingIntent(task: Task) = PendingIntent.getBroadcast(
    this,
    task.id.hashCode(),
    Intent(this, NotifyReceiver::class.java).apply {
        putExtra(TASK_ID, task.id)
        putExtra(TASK_TITLE, task.title)
        putExtra(TASK_DETAIL, task.detail)
    },
    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
)

private fun AlarmManager.setTaskAlarm(
    date: LocalDate?,
    time: LocalTime?,
    pendingIntent: PendingIntent
) {
    if (date != null && time != null) {
        val dateTime = dateTimeToMillis(date, time)
        val clockInfo = AlarmManager.AlarmClockInfo(dateTime, pendingIntent)
        setAlarmClock(clockInfo, pendingIntent)
    }
}