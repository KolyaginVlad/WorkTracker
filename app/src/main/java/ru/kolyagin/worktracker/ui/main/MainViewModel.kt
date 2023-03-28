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
import ru.kolyagin.worktracker.domain.models.TimeWithSeconds.Companion.toSeconds
import ru.kolyagin.worktracker.domain.models.TimeWithSeconds.Companion.toTimeWithSeconds
import ru.kolyagin.worktracker.domain.models.WorkState
import ru.kolyagin.worktracker.domain.repositories.PreferenceRepository
import ru.kolyagin.worktracker.domain.repositories.ScheduleRepository
import ru.kolyagin.worktracker.domain.repositories.WorkStatisticRepository
import ru.kolyagin.worktracker.utils.base.BaseViewModel
import ru.kolyagin.worktracker.utils.models.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.temporal.ChronoUnit
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val scheduleRepository: ScheduleRepository,
    private val preferenceRepository: PreferenceRepository,
    private val workStatisticRepository: WorkStatisticRepository
) : BaseViewModel<MainScreenState, MainEvent>(MainScreenState()) {

    private var timerJob: Job? = null
    private var schedule: DayWorkInfo? = null

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
                val currentDay = LocalDate.now().dayOfWeek.ordinal.takeUnless { it == 6 } ?: 0
                schedule = it.getOrNull(LocalDate.now().dayOfWeek.ordinal)

                val startWorkRanges =
                    schedule?.let { schedule -> getListOfTimeRangesStartWork(schedule) }
                val workingRanges =
                    schedule?.let { schedule -> getListOfTimeRangesWorking(schedule) }
                while (timerJob?.isCancelled != true) {
                    val currentTime = LocalTime.now()
                    val time = Time(currentTime.hour, currentTime.minute)
                    updateState { state ->
                        val currentState = getCurrentState(
                            startWorkRanges,
                            workingRanges,
                            time,
                            listOfDays[currentDay],
                            schedule?.totalTime, //TODO убрать и заменить внутри функции на выборку статистики из репозитория
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
        preferenceRepository.currentWorkState = WorkState.Working
        updateCardState()
    }

    fun onClickFinishWork() {
        when (preferenceRepository.currentWorkState) {
            WorkState.Pause -> {
                updateTimeOfPause()
            }

            WorkState.Working -> {
                workStatisticRepository.addWorkTime(
                    LocalDate.now(),
                    LocalTime.now().toTimeWithSeconds() - preferenceRepository.timeOfCurrentStateSet
                )
            }

            else -> {}
        }
        preferenceRepository.currentWorkState = WorkState.NotWorking
        updateCardState()
    }

    fun onClickStartPause() {
        preferenceRepository.currentWorkState = WorkState.Pause
        updateCardState()
    }

    fun onClickEndPause() {
        updateTimeOfPause()
        preferenceRepository.currentWorkState = WorkState.Working
        updateCardState()
    }

    fun onClickGoToDinner() {
        preferenceRepository.currentWorkState = WorkState.Dinner
        updateCardState()
        //TODO Записать, что на обед ушли и больше нельзя
    }

    fun onClickReturnFromDinner() {
        preferenceRepository.currentWorkState = WorkState.Working
        updateCardState()
        //TODO Подсчитать не ушли ли мы на незапланированную\запланированную паузу во время обеда
    }

    private fun updateTimeOfPause() {
        val listOfPausesWithoutDinner = listOf(
            TimeWithSeconds(13, 0, 0)..TimeWithSeconds(13, 10, 0),
            TimeWithSeconds(15, 0, 0)..TimeWithSeconds(15, 10, 0),
        ) //FIXME заменить на реальные паузы без обеда
        val pauseStart = preferenceRepository.timeOfCurrentStateSet
        val pauseStop = LocalTime.now().toTimeWithSeconds()
        val pause = pauseStart..pauseStop
        listOfPausesWithoutDinner.foldRight(TimeWithSeconds.fromSeconds(0)) { elem, plannedTime ->
            when {
                elem.start in pause && elem.endInclusive in pause -> {
                    plannedTime + (elem.endInclusive - elem.start)
                }

                elem.start in pause -> {
                    plannedTime + pauseStop - elem.start
                }

                elem.endInclusive in pause -> {
                    plannedTime + elem.endInclusive - pauseStart
                }

                else -> {
                    plannedTime
                }
            }
        }.let { plannedTime ->
            workStatisticRepository.addPlannedPauseTime(LocalDate.now(), plannedTime)
            val unplanned = pauseStop - pauseStart - plannedTime
            if (unplanned.toSeconds() > 0)
                workStatisticRepository.addUnplannedPauseTime(LocalDate.now(), unplanned)
        }
    }


    private fun getListOfTimeRangesStartWork(currentSchedule: DayWorkInfo): List<ClosedRange<Time>>? {
        val startWorkTime = currentSchedule.periods.minByOrNull { it.timeStart }?.timeStart
        val startTimes = currentSchedule.periods.map { it.timeStart }.drop(1)
        val endTimes = currentSchedule.periods.map { it.timeEnd }
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
        return currentSchedule.periods.map { it.timeStart..it.timeEnd }.takeIf { it.isNotEmpty() }
    }

    private fun updateCardState() {
        val currentDay = LocalDate.now().dayOfWeek.ordinal.takeUnless { it == 6 } ?: 0
        val startWorkRanges = schedule?.let { schedule -> getListOfTimeRangesStartWork(schedule) }
        val workingRanges = schedule?.let { schedule -> getListOfTimeRangesWorking(schedule) }
        val currentTime = LocalTime.now()
        val time = Time(currentTime.hour, currentTime.minute)
        val listOfDays = DayOfWeek.values().toList()
        updateState { state ->
            val currentState = getCurrentState(
                startWorkRanges,
                workingRanges,
                time,
                listOfDays[currentDay],
                schedule?.totalTime, //TODO убрать и заменить внутри функции на выборку статистики из репозитория
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
    }

    private fun getCurrentState(
        startWorkRanges: List<ClosedRange<Time>>?,
        workingRanges: List<ClosedRange<Time>>?,
        time: Time,
        currentDayOfWeek: DayOfWeek,
        totalTime: Time?,
        currentTime: LocalTime
    ) = when {
        startWorkRanges == null || workingRanges == null || totalTime == null -> {
            CardState.WorkStart(
                day = currentDayOfWeek,
                buttonActive = false,
                buttonStartEarly = true,
                events = persistentListOf(),
                time = null
            )
        }

        startWorkRanges.any { time in it } -> {
            getStateForStartWork(startWorkRanges, currentTime, time, currentDayOfWeek)
        }

        workingRanges.any { time in it } -> {
            getStateForWorking(workingRanges, currentTime, time, currentDayOfWeek)
        }

        else -> {
            getStateForEndWork(currentDayOfWeek, totalTime)
        }
    }

    private fun getStateForStartWork(
        startWorkRanges: List<ClosedRange<Time>>,
        currentTime: LocalTime,
        time: Time,
        currentDayOfWeek: DayOfWeek
    ) = when (preferenceRepository.currentWorkState) {
        WorkState.NotWorking -> {
            val range = startWorkRanges.first { time in it }
            val timeBeforeWork = range.endInclusive.toTimeWithSeconds() - TimeWithSeconds(
                currentTime.hour, currentTime.minute, currentTime.second
            )
            CardState.WorkStart(
                day = currentDayOfWeek,
                buttonActive = true,
                buttonStartEarly = true,
                events = persistentListOf(),
                late = false,
                time = timeBeforeWork
            )
        }

        WorkState.Working -> {
            CardState.Working(
                currentDayOfWeek, persistentListOf(), TimeWithSeconds.fromSeconds(0)
            )
        }

        WorkState.Dinner -> {
            CardState.Dinnering(
                currentDayOfWeek, persistentListOf(), TimeWithSeconds.fromSeconds(0)
            ) //TODO Приписать логику
        }

        WorkState.Pause -> {
            CardState.Pause(
                currentDayOfWeek, persistentListOf(), TimeWithSeconds.fromSeconds(0)
            ) //TODO Приписать логику
        }
    }

    private fun getStateForWorking(
        workingRanges: List<ClosedRange<Time>>,
        currentTime: LocalTime,
        time: Time,
        currentDayOfWeek: DayOfWeek
    ) = when (preferenceRepository.currentWorkState) {
        WorkState.NotWorking -> {
            val range = workingRanges.first { time in it }
            val timeLate = TimeWithSeconds(
                currentTime.hour, currentTime.minute, currentTime.second
            ) - range.start.toTimeWithSeconds()
            CardState.WorkStart(
                day = currentDayOfWeek,
                buttonActive = true,
                buttonStartEarly = false,
                events = persistentListOf(),
                time = timeLate,
                late = true
            )
        }

        WorkState.Working -> {
            val range = workingRanges.first { time in it }
            val timeWork = TimeWithSeconds(
                currentTime.hour, currentTime.minute, currentTime.second
            ) - range.start.toTimeWithSeconds()
            CardState.Working(
                day = currentDayOfWeek,
                events = persistentListOf(),
                time = timeWork,
            )
        }

        WorkState.Dinner -> {
            CardState.Dinnering(
                currentDayOfWeek, persistentListOf(), TimeWithSeconds.fromSeconds(0)
            ) //TODO Приписать логику
        }

        WorkState.Pause -> {
            CardState.Pause(
                currentDayOfWeek, persistentListOf(), TimeWithSeconds.fromSeconds(0)
            ) //TODO Приписать логику
        }
    }

    private fun getStateForEndWork(currentDayOfWeek: DayOfWeek, totalTime: Time) =
        when (preferenceRepository.currentWorkState) {
            WorkState.NotWorking -> {
                CardState.Results(currentDayOfWeek, totalTime)
            }

            WorkState.Working -> {
                CardState.Working(
                    day = currentDayOfWeek,
                    events = persistentListOf(),
                    time = totalTime.toTimeWithSeconds(),
                    overwork = true
                )
                // переработка
            }

            WorkState.Dinner -> {
                CardState.Dinnering(
                    currentDayOfWeek, persistentListOf(), TimeWithSeconds.fromSeconds(0)
                ) //TODO Приписать логику
            }

            WorkState.Pause -> {
                CardState.Pause(
                    currentDayOfWeek, persistentListOf(), TimeWithSeconds.fromSeconds(0)
                ) //TODO Приписать логику
            }
        }
}