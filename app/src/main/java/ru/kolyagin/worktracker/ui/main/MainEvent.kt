package ru.kolyagin.worktracker.ui.main

import ru.kolyagin.worktracker.utils.base.Event

sealed class MainEvent: Event() {
    object OpenSettings: MainEvent()
    object AddTimeStart:MainEvent()
    object AddTimeEnd:MainEvent()

}