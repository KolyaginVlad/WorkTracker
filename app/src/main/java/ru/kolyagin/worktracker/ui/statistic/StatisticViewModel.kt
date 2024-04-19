package ru.kolyagin.worktracker.ui.statistic

import dagger.hilt.android.lifecycle.HiltViewModel
import ru.kolyagin.worktracker.domain.models.TimeWithSeconds
import ru.kolyagin.worktracker.domain.repositories.WorkStatisticRepository
import ru.kolyagin.worktracker.utils.base.BaseViewModel
import ru.kolyagin.worktracker.utils.log.Logger
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneOffset
import javax.inject.Inject

@HiltViewModel
class StatisticViewModel @Inject constructor(
    logger: Logger,
    private val workStatisticRepository: WorkStatisticRepository,
) : BaseViewModel<StatisticScreenState, StatisticScreenEvent>(StatisticScreenState(), logger) {

    init {
        launchViewModelScope {
            workStatisticRepository.getStatistic(LocalDate.now(), LocalDate.now()).let { statistic ->
                updateState {
                    it.copy(
                        isLoading = false,
                        workTime = statistic?.workTime ?: TimeWithSeconds.ZERO,
                        plannedPauseTime = statistic?.plannedPauseTime ?: TimeWithSeconds.ZERO,
                        unplannedPauseTime = statistic?.unplannedPauseTime ?: TimeWithSeconds.ZERO,
                    )
                }
            }
        }
    }

    fun onShowDatePicker() {
        trySendEvent(
            StatisticScreenEvent.ShowRangePicker(
                currentState.startDate.atTime(LocalTime.MIN).toEpochSecond(
                    ZoneOffset.UTC
                ).times(1000), currentState.endDate.atTime(LocalTime.MIN).toEpochSecond(
                    ZoneOffset.UTC
                ).times(1000)
            )
        )
    }

    fun onSelectRange(start: LocalDate, end: LocalDate) {
        updateState {
            it.copy(startDate = start, endDate = end, isLoading = true)
        }
        launchViewModelScope {
            workStatisticRepository.getStatistic(start, end).let { statistic ->
                updateState {
                    it.copy(
                        isLoading = false,
                        workTime = statistic?.workTime ?: TimeWithSeconds.ZERO,
                        plannedPauseTime = statistic?.plannedPauseTime ?: TimeWithSeconds.ZERO,
                        unplannedPauseTime = statistic?.unplannedPauseTime ?: TimeWithSeconds.ZERO,
                    )
                }
            }
        }
    }
}