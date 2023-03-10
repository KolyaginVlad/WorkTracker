package ru.kolyagin.worktracker.ui.notifications

interface NotificationsManager {

    fun scheduleMorningNotification()

    fun scheduleDinnerNotification()

    fun schedulePreWorkNotification()

    fun rescheduleNotifications()

    fun scheduleEveningNotification()

    fun scheduleFinWorkNotification()

}