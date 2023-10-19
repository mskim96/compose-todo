package com.example.mono.core.data.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.mono.core.common.datetime.dateTimeToMillis
import com.example.mono.core.model.task.Task
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
    override fun setTaskNotification(
        taskId: String,
        title: String,
        detail: String,
        date: LocalDate?,
        time: LocalTime?,
        isPendingNotification: Boolean
    ): Boolean = with(context) {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pendingIntent = taskAlarmPendingIntent(taskId, title, detail)
        if (date != null && time != null && isPendingNotification) {
            alarmManager.setTaskAlarm(date, time, pendingIntent)
            true
        } else {
            alarmManager.cancel(pendingIntent)
            false
        }
    }
}

private fun Context.taskAlarmPendingIntent(
    taskId: String,
    title: String,
    detail: String
) = PendingIntent.getBroadcast(
    this,
    taskId.hashCode(),
    Intent(this, NotifyReceiver::class.java).apply {
        putExtra(TASK_ID, taskId)
        putExtra(TASK_TITLE, title)
        putExtra(TASK_DETAIL, detail)
    },
    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
)

private fun AlarmManager.setTaskAlarm(
    date: LocalDate,
    time: LocalTime,
    pendingIntent: PendingIntent
) {
    val currentTime = dateTimeToMillis(LocalDate.now(), LocalTime.now())
    val dateTime = dateTimeToMillis(date, time)
    if(currentTime < dateTime) {
        val clockInfo = AlarmManager.AlarmClockInfo(dateTime, pendingIntent)
        setAlarmClock(clockInfo, pendingIntent)
    }
}