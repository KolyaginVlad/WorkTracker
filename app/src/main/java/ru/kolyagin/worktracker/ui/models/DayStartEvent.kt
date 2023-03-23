package ru.kolyagin.worktracker.ui.models

import ru.kolyagin.worktracker.domain.models.Time

data class DayStartEvent(
    val id: Long,
    val timeStart: Time,
    val timeEnd: Time,
    val name: String
)