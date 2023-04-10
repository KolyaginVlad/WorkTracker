package ru.kolyagin.worktracker.domain.repositories

import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.flow.Flow
import ru.kolyagin.worktracker.domain.models.DaySalaryRate
import ru.kolyagin.worktracker.domain.models.Time
import ru.kolyagin.worktracker.domain.models.TimeWithSeconds
import ru.kolyagin.worktracker.domain.models.WorkState
import java.time.DayOfWeek

interface PreferenceRepository {
    fun salary(): Flow<ImmutableList<DaySalaryRate>>

    suspend fun addsalary(
        day: DayOfWeek,
        rate: Long
    )

    suspend fun deleteSalary(
        day: DayOfWeek,
    )

    suspend fun setSalary(
        day: DayOfWeek, rate: Long
    )

    var currentWorkState: WorkState
    val timeOfCurrentStateSet: TimeWithSeconds
    var isDinnerEnableToday: Boolean
    var isMorningNotificationEnable: Boolean
    var morningNotificationOffset: Time
    var morningRange: ClosedRange<Time>
    var isDinnerNotificationEnable: Boolean
    var dinnerTimeInNotWorkingTime: Time
    var isStartWorkNotificationEnable: Boolean
    var timeBeforeStartWork: Time
    var isEndWorkNotificationEnable: Boolean
    var timeBeforeEndWork: Time
}