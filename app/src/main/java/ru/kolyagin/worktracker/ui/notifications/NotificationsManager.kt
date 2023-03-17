package ru.kolyagin.worktracker.ui.notifications

interface NotificationsManager {

    fun scheduleMorningNotification()

    fun scheduleDinnerNotification()

    fun schedulePreWorkNotification()

    fun rescheduleNotifications()

    fun rescheduleNotifications(code: Int)

    fun scheduleEveningNotification()

    fun scheduleFinWorkNotification()

}