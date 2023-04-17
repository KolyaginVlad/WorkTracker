package ru.kolyagin.worktracker.domain.models

import ru.kolyagin.worktracker.data.database.entities.DaySalaryRateEntity
import java.time.DayOfWeek

data class DaySalaryRate(
    val id: Long,
    val day: DayOfWeek,
    val rate: Long
)

fun DaySalaryRate.mapToEntity() = DaySalaryRateEntity(
    id = 0,
    day = day.ordinal,
    rate = rate
)