package com.example.mono.core.data.notification

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.net.toUri
import com.example.mono.core.common.R
import com.example.mono.core.data.repository.TaskRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TARGET_ACTIVITY_NAME = "com.example.mono.MonoActivity"
private const val TASK_NOTIFICATION_REQUEST_CODE = 0
private const val TASK_NOTIFICATION_CHANNEL_ID = ""
private const val TASK_NOTIFICATION_GROUP = "TASK_NOTIFICATIONS"
private const val DEEP_LINK_SCHEME_AND_HOST = "mono://task_detail_route"

/**
 * Receiver for default task notifications.
 */
@AndroidEntryPoint
class NotifyReceiver : BroadcastReceiver() {

    @Inject
    lateinit var taskRepository: TaskRepository

    override fun onReceive(context: Context, intent: Intent?) = context.run {
        // Check permission of notification.
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        // task id for create deep link.
        val taskId = intent?.extras!!.getString("taskId")!!
        // title for notification title.
        val title = intent.extras!!.getString("title")!!
        // detail for notification text.
        val detail = intent.extras!!.getString("detail")!!

        val taskNotification = createTaskNotification {
            setSmallIcon(
                R.drawable.ic_mono_notification
            )
                .setContentTitle(title)
                .setContentText(detail)
                .setContentIntent(taskPendingIntent(taskId))
                .setGroup(TASK_NOTIFICATION_GROUP)
                .setAutoCancel(true)
        }
        val notificationManager = NotificationManagerCompat.from(this)
        CoroutineScope(Dispatchers.IO).launch {
            taskRepository.completeNotification(taskId)
        }
        notificationManager.notify(taskId.hashCode(), taskNotification)
    }
}

/**
 * Create a notification for task.
 */
private fun Context.createTaskNotification(
    block: NotificationCompat.Builder.() -> Unit
): Notification {
    ensureNotificationChannelExists()
    return NotificationCompat.Builder(
        this,
        TASK_NOTIFICATION_CHANNEL_ID
    )
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .apply(block)
        .build()
}

/**
 * Ensure that a notification channel is present if applicable.
 */
private fun Context.ensureNotificationChannelExists() {
    val channel = NotificationChannel(
        TASK_NOTIFICATION_CHANNEL_ID,
        "Task for Todos",
        NotificationManager.IMPORTANCE_HIGH
    ).apply {
        description = "Todo"
        lockscreenVisibility = NotificationCompat.VISIBILITY_PRIVATE
        enableVibration(true)
    }
    NotificationManagerCompat.from(this).createNotificationChannel(channel)
}

private fun Context.taskPendingIntent(taskId: String) = PendingIntent.getActivity(
    this,
    TASK_NOTIFICATION_REQUEST_CODE,
    Intent().apply {
        action = Intent.ACTION_VIEW
        data = taskId.taskDeepLinkUri()
        component = ComponentName(
            packageName,
            TARGET_ACTIVITY_NAME
        )
    },
    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
)

private fun String.taskDeepLinkUri() = "${DEEP_LINK_SCHEME_AND_HOST}/$this".toUri()