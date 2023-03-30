package ru.kolyagin.worktracker.ui.main

import ru.kolyagin.worktracker.domain.models.Time
import ru.kolyagin.worktracker.utils.base.Event

sealed class MainEvent : Event() {
    object OpenSettings : MainEvent()
    object AddEventTime : MainEvent()
    class ChangeEventTime(val timeStart: Time, val timeEnd: Time) : MainEvent()

}