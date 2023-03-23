package ru.kolyagin.worktracker.ui.main

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import ru.kolyagin.worktracker.domain.models.DayWorkInfo
import ru.kolyagin.worktracker.domain.models.Time
import ru.kolyagin.worktracker.domain.models.Time.Companion.toTimeWithSeconds
import ru.kolyagin.worktracker.domain.models.TimeWithSeconds
import ru.kolyagin.worktracker.domain.repositories.ScheduleRepository
import ru.kolyagin.worktracker.utils.base.BaseViewModel
import ru.kolyagin.worktracker.utils.models.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.temporal.ChronoUnit
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val scheduleRepository: ScheduleRepository
) : BaseViewModel<MainScreenState, MainEvent>(MainScreenState()) {

    private var timerJob: Job? = null

    init {
        updateState {
            val listOfDays = DayOfWeek.values().toList()
            it.copy(days = listOfDays.map { day ->
                CardState.WorkStart(
                    day = day,
                    buttonActive = false,
                    buttonStartEarly = true,
                    events = persistentListOf(),
                    time = null
                )
            }.toPersistentList())
        }
    }

    fun init() {
        val listOfDays = DayOfWeek.values().toList()
        scheduleRepository.schedule().subscribe {
            timerJob?.cancel()
            timerJob = launchViewModelScope {
                var currentDay = LocalDate.now().dayOfWeek.ordinal
                val currentSchedule = it.getOrNull(currentDay)
                currentDay = currentDay.takeUnless { it == 6 } ?: 0

                val startWorkRanges =
                    currentSchedule?.let { schedule -> getListOfTimeRangesStartWork(schedule) }
                val workingRanges =
                    currentSchedule?.let { schedule -> getListOfTimeRangesWorking(schedule) }
                while (timerJob?.isCancelled != true) {
                    val currentTime = LocalTime.now()
                    val time = Time(currentTime.hour, currentTime.minute)
                    updateState { state ->
                        val currentState = getCurrentState(
                            startWorkRanges,
                            workingRanges,
                            listOfDays[currentDay],
                            time,
                            currentSchedule?.totalTime,
                            currentTime
                        )
                        val daysWithOutCurrent = (listOfDays.subList(
                            currentDay + 1, listOfDays.size
                        ) + listOfDays.subList(0, currentDay)).map { day ->
                            CardState.WorkStart(
                                day = day,
                                buttonActive = false,
                                buttonStartEarly = true,
                                events = persistentListOf(),
                                time = null
                            )
                        }
                        state.copy(
                            days = (listOf(currentState) + daysWithOutCurrent).toPersistentList()
                        )
                    }
                    delay(ChronoUnit.MILLIS.between(currentTime.plusSeconds(1), LocalTime.now()))
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
        totalTime: Time?,
        currentTime: LocalTime
    ) = when {
        startWorkRanges == null || workingRanges == null || totalTime == null -> {
            CardState.WorkStart(
                day = dayOfWeek,
                buttonActive = false,
                buttonStartEarly = true,
                events = persistentListOf(),
                time = null
            )
        }

        startWorkRanges.any { time in it } -> {
            val range = startWorkRanges.first { time in it }
            val timeBeforeWork = range.endInclusive.toTimeWithSeconds() - TimeWithSeconds(
                currentTime.hour,
                currentTime.minute,
                currentTime.second
            )
            CardState.WorkStart(
                day = dayOfWeek,
                buttonActive = true,
                buttonStartEarly = true,
                events = persistentListOf(),
                time = timeBeforeWork
            )
        }

        workingRanges.any { time in it } -> {
            CardState.Working(dayOfWeek) //check pause and events. check is work start
        }

        else -> {
            CardState.Results(dayOfWeek, totalTime)
        }
    }
}