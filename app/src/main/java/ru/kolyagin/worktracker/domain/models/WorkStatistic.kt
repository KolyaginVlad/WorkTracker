package ru.kolyagin.worktracker.domain.models

data class WorkStatistic(
    val workTime: TimeWithSeconds,
    val plannedPauseTime: TimeWithSeconds,
    val unplannedPauseTime: TimeWithSeconds
)
