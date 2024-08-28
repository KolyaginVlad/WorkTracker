package ru.kolyagin.worktracker.ui.statistic

import ru.kolyagin.worktracker.domain.models.TimeWithSeconds
import ru.kolyagin.worktracker.utils.base.State
import java.time.LocalDate

data class StatisticScreenState(
    val isLoading: Boolean = true,
    val workTime: TimeWithSeconds = TimeWithSeconds.ZERO,
    val plannedPauseTime: TimeWithSeconds = TimeWithSeconds.ZERO,
    val unplannedPauseTime: TimeWithSeconds = TimeWithSeconds.ZERO,
    val startDate: LocalDate = LocalDate.now(),
    val endDate: LocalDate = LocalDate.now(),
): State()