package ru.kolyagin.worktracker.domain.models

import ru.kolyagin.worktracker.domain.models.Time

data class WorkEvent(
    val id: Long,
    val timeStart: Time,
    val timeEnd: Time,
    val name: String,
    val isLunch:Boolean = false
)