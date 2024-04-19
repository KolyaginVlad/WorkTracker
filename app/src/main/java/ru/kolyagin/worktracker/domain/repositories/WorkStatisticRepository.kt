package ru.kolyagin.worktracker.domain.repositories

import kotlinx.coroutines.flow.Flow
import ru.kolyagin.worktracker.domain.models.TimeWithSeconds
import ru.kolyagin.worktracker.domain.models.WorkStatistic
import java.time.LocalDate

interface WorkStatisticRepository {
    suspend fun addWorkTime(date: LocalDate, time: TimeWithSeconds)

    suspend fun addPlannedPauseTime(date: LocalDate, time: TimeWithSeconds)

    suspend fun addUnplannedPauseTime(date: LocalDate, time: TimeWithSeconds)

    suspend fun getStatistic(date: LocalDate): WorkStatistic

    suspend fun getStatistic(dateStart: LocalDate, dateEnd: LocalDate): WorkStatistic?

    fun getStatisticFlow(date: LocalDate): Flow<WorkStatistic?>
}