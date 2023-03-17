package ru.kolyagin.worktracker.ui.notifications.alarmManager

import android.annotation.SuppressLint
import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dagger.hilt.android.AndroidEntryPoint
import ru.kolyagin.worktracker.R
import ru.kolyagin.worktracker.ui.MainActivity
import ru.kolyagin.worktracker.ui.notifications.Notification
import ru.kolyagin.worktracker.ui.notifications.NotificationsManager
import ru.kolyagin.worktracker.utils.Constants
import javax.inject.Inject

@SuppressLint("UnspecifiedImmutableFlag")
@AndroidEntryPoint
class NotificationReceiver @Inject constructor(
) : BroadcastReceiver() {
    @Inject
    lateinit var notification: Notification

    @Inject
    lateinit var notificationManager: NotificationsManager

    override fun onReceive(context: Context, intent: Intent) {
        notificationManager.rescheduleNotifications(
            intent.getIntExtra(
                Constants.NOTIFICATION_CODE,
                Constants.INVALID_CODE
            )
        )
        val notificationIntent =
            Intent(context.applicationContext, MainActivity::class.java).apply {
                flags = (Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            }

        val pendingIntent = PendingIntent.getActivity(
            context.applicationContext, 0,
            notificationIntent, PendingIntent.FLAG_IMMUTABLE
        )
        notification.sendNotification(
            description = context.applicationContext.getString(
                intent.getIntExtra(
                    Constants.DESCRIPTION,
                    R.string.morning_start_work_description
                )
            ),
            pendingIntent = pendingIntent
        )
    }

}