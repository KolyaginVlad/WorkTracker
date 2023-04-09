package ru.kolyagin.worktracker.ui.settings

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import ru.kolyagin.worktracker.domain.models.DayWorkInfo
import ru.kolyagin.worktracker.domain.models.Time
import ru.kolyagin.worktracker.utils.base.State

data class SettingsScreenState(
    val listOfWorkPeriods: ImmutableList<DayWorkInfo> = persistentListOf()
) : State() {
    val totalTime = listOfWorkPeriods
        .map { it.timeWithOutConflux }
        .takeIf { it.isNotEmpty() }
        ?.reduceRight { time, acc ->
            acc + time
        } ?: Time.fromMinutes(0)
}