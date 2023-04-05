package ru.kolyagin.worktracker.domain.repositories

import ru.kolyagin.worktracker.domain.models.TimeWithSeconds
import ru.kolyagin.worktracker.domain.models.WorkStatistic
import java.time.LocalDate

interface WorkStatisticRepository {
    suspend fun addWorkTime(date: LocalDate, time: TimeWithSeconds)

    suspend fun addPlannedPauseTime(date: LocalDate, time: TimeWithSeconds)

    suspend fun addUnplannedPauseTime(date: LocalDate, time: TimeWithSeconds)

    suspend fun getStatistic(date: LocalDate): WorkStatistic
}