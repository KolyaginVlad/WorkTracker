package ru.kolyagin.worktracker.ui.notificationSettings

import ru.kolyagin.worktracker.domain.models.Time
import ru.kolyagin.worktracker.utils.base.Event

sealed class NotificationSettingsEvent : Event() {
    class ShowTimePicker(
        val time: Time,
        val onTimePick: (Time) -> Unit
    ) : NotificationSettingsEvent()
}