package ru.kolyagin.worktracker.ui.notifications

import android.app.PendingIntent

interface Notification {
    fun sendNotification(
        description: String?,
        pendingIntent: PendingIntent?
    )
}