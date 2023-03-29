package ru.kolyagin.worktracker.domain.repositories

import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.flow.Flow
import ru.kolyagin.worktracker.domain.models.DayWorkInfo
import ru.kolyagin.worktracker.domain.models.WorkPeriod
import java.time.DayOfWeek

interface ScheduleRepository {
    fun schedule(): Flow<ImmutableList<DayWorkInfo>>

    suspend fun addPeriod(
        dayOfWeek: DayOfWeek
    )

    suspend fun deletePeriod(
        workPeriod: WorkPeriod
    )

    suspend fun setTime(
        workPeriod: WorkPeriod,
        dayOfWeek: DayOfWeek
    )

    suspend fun setDinner(
        dayOfWeek: DayOfWeek,
        isDinnerInclude: Boolean
    )
}