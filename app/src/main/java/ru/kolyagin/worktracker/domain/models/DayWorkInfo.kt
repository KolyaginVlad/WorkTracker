package ru.kolyagin.worktracker.domain.models

import kotlinx.collections.immutable.ImmutableList
import java.time.DayOfWeek

data class DayWorkInfo(
    val day: DayOfWeek,
    val periods: ImmutableList<WorkPeriod>,
    val isDinnerInclude: Boolean
) {
    val totalTime = (
            periods
                .map { it.timeEnd - it.timeStart }
                .takeIf { it.isNotEmpty() }
                ?.reduceRight { time, acc ->
                    acc + time
                } ?: Time.fromMinutes(if (isDinnerInclude) 60 else 0)
            ) - Time.fromMinutes(if (isDinnerInclude) 60 else 0)
}
