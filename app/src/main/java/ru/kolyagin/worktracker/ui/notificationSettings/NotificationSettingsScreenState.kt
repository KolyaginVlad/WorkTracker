package ru.kolyagin.worktracker.ui.notificationSettings

import ru.kolyagin.worktracker.domain.models.Time
import ru.kolyagin.worktracker.utils.base.State

data class NotificationSettingsScreenState(
    val isMorningNotificationEnable: Boolean = true,
    val morningNotificationRange: ClosedRange<Time> = Time(0, 0)..Time(0, 0),
    val morningOffset: Time = Time(0, 0),
    val isDinnerNotificationEnable: Boolean = true,
    val dinnerTime: Time = Time(0, 0),
    val isStartWorkNotificationEnable: Boolean = true,
    val startWorkOffset: Time = Time(0, 0),
    val isEndWorkNotificationEnable: Boolean = true,
    val endWorkOffset: Time = Time(0, 0),
) : State()