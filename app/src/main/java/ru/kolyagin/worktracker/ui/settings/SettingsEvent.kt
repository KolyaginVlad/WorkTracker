package ru.kolyagin.worktracker.ui.settings

import ru.kolyagin.worktracker.domain.models.Time
import ru.kolyagin.worktracker.ui.notificationSettings.NotificationSettingsEvent
import ru.kolyagin.worktracker.utils.base.Event
import java.time.DayOfWeek

sealed class SettingsEvent : Event() {
    class ShowTimePicker(val time: Time) : SettingsEvent()


    object AddSalary : SettingsEvent()

    class SetSalary(
        val day: DayOfWeek,
    ) : SettingsEvent()

}