package ru.kolyagin.worktracker.ui.main

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import ru.kolyagin.worktracker.domain.models.DayWorkInfo
import ru.kolyagin.worktracker.domain.models.Time
import ru.kolyagin.worktracker.domain.repositories.ScheduleRepository
import ru.kolyagin.worktracker.utils.base.BaseViewModel
import ru.kolyagin.worktracker.utils.models.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val scheduleRepository: ScheduleRepository
) : BaseViewModel<MainScreenState, MainEvent>(MainScreenState()) {

    private var timerJob: Job? = null

    init {
        scheduleRepository.schedule().subscribe {
            timerJob?.cancel()
            timerJob = launchViewModelScope {
                var currentDay = LocalDate.now().dayOfWeek.ordinal
                val currentSchedule = it.getOrNull(currentDay)
                currentDay = currentDay.takeUnless { it == 6 } ?: 0
                val listOfDays = DayOfWeek.values().toList()
                val startWorkRanges =
                    currentSchedule?.let { schedule -> getListOfTimeRangesStartWork(schedule) }
                val workingRanges =
                    currentSchedule?.let { schedule -> getListOfTimeRangesWorking(schedule) }
                while (true) {
                    val currentTime = LocalTime.now()
                    val time = Time(currentTime.hour, currentTime.minute)
                    updateState { state ->
                        val currentState = getCurrentState(
                            startWorkRanges,
                            workingRanges,
                            listOfDays[currentDay],
                            time,
                            currentSchedule?.totalTime
                        )
                        val daysWithOutCurrent = (listOfDays.subList(
                            currentDay + 1, listOfDays.size
                        ) + listOfDays.subList(0, currentDay)).map { day ->
                            CardState.WorkStart(day)
                        }
                        state.copy(
                            days = (listOf(currentState) + daysWithOutCurrent).toPersistentList()
                        )
                    }
                    delay(1000)
                }
            }
        }
    }

    fun onClickOpenSettings() {
        trySendEvent(MainEvent.OpenSettings)
    }

    fun onClickStartWork() {
        //TODO обработать
//        updateState {
//            CardState.Working
//        }
    }

    fun onClickFinishWork() {
        //TODO обработать
//        updateState {
//            CardState.Results(Time(0, 0)) // or CardState.WorkNotStarted
//        }
    }

    fun onClickStartPause() {
        //TODO обработать
//        updateState {
//            CardState.Pause
//        }
    }

    fun onClickEndPause() {
        //TODO обработать
//        updateState {
//            CardState.Working // or CardState.WorkNotStarted or CardState.Results
//        }
    }

    fun onClickGoToDinner() {
        //TODO обработать
//        updateState {
//            CardState.Dinnering
//        }
    }

    fun onClickReturnFromDinner() {
        //TODO обработать
//        updateState {
//            CardState.Working // or CardState.WorkNotStarted or CardState.Results
//        }
    }


    private fun getListOfTimeRangesStartWork(currentSchedule: DayWorkInfo): List<ClosedRange<Time>>? {
        val startWorkTime = currentSchedule
            .periods
            .minByOrNull { it.timeStart }
            ?.timeStart
        val startTimes = currentSchedule
            .periods
            .map { it.timeStart }
            .drop(1)
        val endTimes = currentSchedule
            .periods
            .map { it.timeEnd }
        val periods = startTimes.mapIndexed { index, time ->
            endTimes[index]..time
        }
        return startWorkTime?.let {
            periods + listOf(
                Time.fromMinutes(0)..startWorkTime
            )
        }
    }

    private fun getListOfTimeRangesWorking(currentSchedule: DayWorkInfo): List<ClosedRange<Time>>? {
        return currentSchedule
            .periods
            .map { it.timeStart..it.timeEnd }
            .takeIf { it.isNotEmpty() }
    }

    private fun getCurrentState(
        startWorkRanges: List<ClosedRange<Time>>?,
        workingRanges: List<ClosedRange<Time>>?,
        dayOfWeek: DayOfWeek,
        time: Time,
        totalTime: Time?
    ) = when {
        startWorkRanges == null || workingRanges == null || totalTime == null -> {
            CardState.WorkStart(dayOfWeek) //false active
        }

        startWorkRanges.any { time in it } -> {
            CardState.WorkStart(dayOfWeek) //true active
        }

        workingRanges.any { time in it } -> {
            CardState.Working(dayOfWeek) //check pause and events. check is work start
        }

        else -> {
            CardState.Results(dayOfWeek, totalTime)
        }
    }
}