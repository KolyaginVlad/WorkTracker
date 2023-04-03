package ru.kolyagin.worktracker.ui.settings

import dagger.hilt.android.lifecycle.HiltViewModel
import ru.kolyagin.worktracker.domain.models.Time
import ru.kolyagin.worktracker.domain.models.WorkEvent
import ru.kolyagin.worktracker.domain.models.WorkPeriod
import ru.kolyagin.worktracker.domain.repositories.ScheduleRepository
import ru.kolyagin.worktracker.ui.settings.models.PeriodPart
import ru.kolyagin.worktracker.utils.Constants
import ru.kolyagin.worktracker.utils.Constants.DINNER
import ru.kolyagin.worktracker.utils.base.BaseViewModel
import java.time.DayOfWeek
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val scheduleRepository: ScheduleRepository
) : BaseViewModel<SettingsScreenState, SettingsEvent>(SettingsScreenState()) {

    private var selectedDayOfWeek: DayOfWeek? = null

    private var selectedWorkPeriod: WorkPeriod? = null

    private var selectedPeriodPart: PeriodPart? = null

    init {
        scheduleRepository.schedule().subscribe { list ->
            updateState {
                it.copy(listOfWorkPeriods = list)
            }
        }
    }

    fun onClickPeriod(dayOfWeek: DayOfWeek, workPeriod: WorkPeriod, periodPart: PeriodPart) {
        selectedDayOfWeek = dayOfWeek
        selectedWorkPeriod = workPeriod
        selectedPeriodPart = periodPart
        val time = if (periodPart == PeriodPart.START) {
            workPeriod.timeStart
        } else {
            workPeriod.timeEnd
        }
        trySendEvent(SettingsEvent.ShowTimePicker(time))
    }

    fun onDeletePeriod(workPeriod: WorkPeriod) {
        launchViewModelScope {
            scheduleRepository.deletePeriod(workPeriod)
        }
    }

    fun onAddPeriod(dayOfWeek: DayOfWeek) {
        launchViewModelScope {
            scheduleRepository.addPeriod(dayOfWeek)
        }
    }

    fun onDinnerChange(dayOfWeek: DayOfWeek, isDinnerInclude: Boolean) {
        launchViewModelScope {
            val dinner = WorkEvent(
                id = 0,
                timeStart = Time(Constants.DINNER_HOURS, Constants.DINNER_MINUTES),
                timeEnd = Time(Constants.DINNER_HOURS + 1, Constants.DINNER_MINUTES),
                name = DINNER,
                isDinner = true
            )
            scheduleRepository.setDinner(dayOfWeek.ordinal, isDinnerInclude)
            if (isDinnerInclude) {
                scheduleRepository.addWorkEvent(dayOfWeek.ordinal, dinner)
            } else {
                scheduleRepository.deleteDinner(dayOfWeek.ordinal)
            }
        }
    }


    fun onTimePicked(time: Time) {
        selectedDayOfWeek?.let { dayOfWeek ->
            launchViewModelScope {
                selectedWorkPeriod?.let {
                    val newPeriod = if (selectedPeriodPart == PeriodPart.START) {
                        it.copy(timeStart = time)
                    } else {
                        it.copy(timeEnd = time)
                    }
                    scheduleRepository.setTime(newPeriod, dayOfWeek)
                }
                selectedDayOfWeek = null
                selectedPeriodPart = null
                selectedWorkPeriod = null
            }
        }
    }

}