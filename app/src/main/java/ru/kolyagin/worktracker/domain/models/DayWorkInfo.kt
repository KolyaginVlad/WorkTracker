package ru.kolyagin.worktracker.domain.models

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import java.time.DayOfWeek

data class DayWorkInfo(
    val day: DayOfWeek,
    val periods: ImmutableList<WorkPeriod>,
    val isDinnerInclude: Boolean,
    val events: ImmutableList<WorkEvent> = persistentListOf()
) {
    val totalTime = (
            periods
                .map { it.timeEnd - it.timeStart }
                .takeIf { it.isNotEmpty() }
                ?.reduceRight { time, acc ->
                    acc + time
                } ?: Time.fromMinutes(if (isDinnerInclude) 60 else 0)
            ) - Time.fromMinutes(if (isDinnerInclude) 60 else 0)
    val timeWithOutConflux = ((calculateTotalTime(periods)
        ?: (Time.fromMinutes(if (isDinnerInclude) 60 else 0)))
                - Time.fromMinutes(if (isDinnerInclude) 60 else 0)).takeIf { it.toMinutes() > 0 }
        ?: Time.fromMinutes(0)
}


fun calculateTotalTime(intervals: ImmutableList<WorkPeriod>): Time? {
    if (intervals.isEmpty()) return null
    val intervalsSorted = intervals.sortedBy { it.timeStart }
    var totalTime = Time(0, 0)
    var currentStartTime = intervalsSorted[0].timeStart
    var currentEndTime = intervalsSorted[0].timeEnd
    for (i in 1 until intervalsSorted.size) {
        val interval = intervalsSorted[i]
        if (interval.timeStart <= currentEndTime) {
            currentEndTime = maxOf(currentEndTime, interval.timeEnd)
        } else {
            totalTime += currentEndTime - currentStartTime
            currentStartTime = interval.timeStart
            currentEndTime = interval.timeEnd
        }
    }
    totalTime += currentEndTime - currentStartTime
    return totalTime
}