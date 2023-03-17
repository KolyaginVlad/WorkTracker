package ru.kolyagin.worktracker.ui.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class TimeChangeBroadcastReceiver : BroadcastReceiver(){

    @Inject
    lateinit var notificationsManager: NotificationsManager

    override fun onReceive(context: Context?, intent: Intent?) {
        val action = intent?.action
        if (
            action.equals(Intent.ACTION_TIME_CHANGED) ||
            action.equals(Intent.ACTION_TIMEZONE_CHANGED) ||
            action.equals(Intent.ACTION_BOOT_COMPLETED)
        ) {
            notificationsManager.rescheduleNotifications()
        }
    }


}