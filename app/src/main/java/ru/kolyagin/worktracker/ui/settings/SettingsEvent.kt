package ru.kolyagin.worktracker.ui.settings

import ru.kolyagin.worktracker.domain.models.Time
import ru.kolyagin.worktracker.utils.base.Event

sealed class SettingsEvent: Event() {
    class ShowTimePicker(val time: Time) : SettingsEvent()
}