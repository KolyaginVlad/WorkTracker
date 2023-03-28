package ru.kolyagin.worktracker.domain.repositories

import ru.kolyagin.worktracker.domain.models.TimeWithSeconds
import java.time.LocalDate

interface WorkStatisticRepository {
    fun addWorkTime(date: LocalDate, time: TimeWithSeconds)

    fun addPlannedPauseTime(date: LocalDate, time: TimeWithSeconds)

    fun addUnplannedPauseTime(date: LocalDate, time: TimeWithSeconds)
}