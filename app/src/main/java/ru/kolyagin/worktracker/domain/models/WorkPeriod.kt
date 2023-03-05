package ru.kolyagin.worktracker.domain.models

data class WorkPeriod(
    val id: Long,
    val timeStart: Time,
    val timeEnd: Time
)
