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
import java.time.DayOfWeek
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
                val currentDay = LocalDate.now().dayOfWeek.ordinal
                schedule = it.getOrNull(LocalDate.now().dayOfWeek.ordinal)

                val startWorkRanges =
                    schedule?.let { schedule -> getListOfTimeRangesStartWork(schedule) }
                val workingRanges =
                    schedule?.let { schedule -> getListOfTimeRangesWorking(schedule) }
                while (timerJob?.isCancelled != true) {
                    val currentTime = LocalTime.now()
                    val time = Time(currentTime.hour, currentTime.minute)
                    val currentState = getCurrentState(
                        startWorkRanges,
                        workingRanges,
                        time,
                        listOfDays[currentDay],
                        currentTime,
                        schedule?.totalTime
                    )
                    updateState { state ->
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
                    delay(ChronoUnit.MILLIS.between(LocalTime.now(), currentTime.plusSeconds(1)))
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
        launchViewModelScope {
            when (preferenceRepository.currentWorkState) {
                WorkState.Pause -> {
                    updateTimeOfPause()
                }

                WorkState.Dinner -> {
                    updateTimeOfPauseAfterDinner()
                }

                WorkState.Working -> {
                    workStatisticRepository.addWorkTime(
                        LocalDate.now(),
                        LocalTime.now()
                            .toTimeWithSeconds() - preferenceRepository.timeOfCurrentStateSet
                    )
                }

                else -> {}
            }
            preferenceRepository.currentWorkState = WorkState.Worked
            updateCardState()
        }
    }

    fun onClickStartPause() {
        launchViewModelScope {
            workStatisticRepository.addWorkTime(
                LocalDate.now(),
                LocalTime.now().toTimeWithSeconds() - preferenceRepository.timeOfCurrentStateSet
            )
            preferenceRepository.currentWorkState = WorkState.Pause
            updateCardState()
        }
    }

    fun onClickEndPause() {
        updateTimeOfPause()
        preferenceRepository.currentWorkState = WorkState.Working
        updateCardState()
    }

    fun onClickGoToDinner() {
        launchViewModelScope {
            workStatisticRepository.addWorkTime(
                LocalDate.now(),
                LocalTime.now().toTimeWithSeconds() - preferenceRepository.timeOfCurrentStateSet
            )
            preferenceRepository.currentWorkState = WorkState.Dinner
            preferenceRepository.isDinnerEnableToday = false
            updateCardState()
        }
    }

    fun onClickReturnFromDinner() {
        updateTimeOfPauseAfterDinner()
        preferenceRepository.currentWorkState = WorkState.Working
        updateCardState()
    }

    private fun updateTimeOfPause() {
        val pauseStart = preferenceRepository.timeOfCurrentStateSet
        val pauseStop = LocalTime.now().toTimeWithSeconds()
        calculateAndSavePauses(pauseStart, pauseStop)
    }

    private fun updateTimeOfPauseAfterDinner() {
        //FIXME Заменить на реальное время обеда из карточки
        val dinner = TimeWithSeconds(13, 0, 0) - TimeWithSeconds(12, 0, 0)
        var pauseStart = preferenceRepository.timeOfCurrentStateSet
        val pauseStop = LocalTime.now().toTimeWithSeconds()
        if (dinner >= pauseStop - pauseStart)
            return
        pauseStart += dinner
        calculateAndSavePauses(pauseStart, pauseStop)
    }

    private fun calculateAndSavePauses(
        pauseStart: TimeWithSeconds,
        pauseStop: TimeWithSeconds
    ) = launchViewModelScope {
        val listOfPausesWithoutDinner = listOf(
            TimeWithSeconds(13, 0, 0)..TimeWithSeconds(13, 10, 0),
            TimeWithSeconds(15, 0, 0)..TimeWithSeconds(15, 10, 0),
        ) //FIXME заменить на реальные паузы без обеда
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
            val unplanned = (pauseStop - pauseStart) - plannedTime
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

    private fun updateCardState() = launchViewModelScope {
        val currentDay = LocalDate.now().dayOfWeek.ordinal.takeUnless { it == 6 } ?: 0
        val startWorkRanges = schedule?.let { schedule -> getListOfTimeRangesStartWork(schedule) }
        val workingRanges = schedule?.let { schedule -> getListOfTimeRangesWorking(schedule) }
        val currentTime = LocalTime.now()
        val time = Time(currentTime.hour, currentTime.minute)
        val listOfDays = DayOfWeek.values().toList()
        val currentState = getCurrentState(
            startWorkRanges,
            workingRanges,
            time,
            listOfDays[currentDay],
            currentTime,
            schedule?.totalTime
        )
        updateState { state ->
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

    private suspend fun getCurrentState(
        startWorkRanges: List<ClosedRange<Time>>?,
        workingRanges: List<ClosedRange<Time>>?,
        time: Time,
        currentDayOfWeek: DayOfWeek,
        currentTime: LocalTime,
        totalTime: Time?
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
            getStateForStartWork(startWorkRanges, currentTime, time, currentDayOfWeek, totalTime)
        }

        workingRanges.any { time in it } -> {
            getStateForWorking(workingRanges, currentTime, time, currentDayOfWeek, totalTime)
        }

        else -> {
            getStateForEndWork(currentDayOfWeek, currentTime, totalTime)
        }
    }

    private suspend fun getStateForStartWork(
        startWorkRanges: List<ClosedRange<Time>>,
        currentTime: LocalTime,
        time: Time,
        currentDayOfWeek: DayOfWeek,
        totalTime: Time
    ) = when (preferenceRepository.currentWorkState) {
        WorkState.NotWorking, WorkState.Worked -> {
            val range = startWorkRanges.first { time in it }
            val timeBeforeWork =
                range.endInclusive.toTimeWithSeconds() - currentTime.toTimeWithSeconds()
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
            val statistic = workStatisticRepository.getStatistic(LocalDate.now())
            if (totalTime.toTimeWithSeconds() < statistic.workTime) {
                CardState.Working(
                    currentDayOfWeek,
                    persistentListOf(),
                    statistic.workTime + currentTime.toTimeWithSeconds() - preferenceRepository.timeOfCurrentStateSet,
                )
            } else {
                CardState.Working(
                    currentDayOfWeek,
                    persistentListOf(),
                    statistic.workTime + currentTime.toTimeWithSeconds() - preferenceRepository.timeOfCurrentStateSet - totalTime.toTimeWithSeconds(),
                    true
                )
            }
        }

        WorkState.Dinner -> {
            CardState.Dinnering(
                currentDayOfWeek,
                persistentListOf(),
                currentTime.toTimeWithSeconds() - preferenceRepository.timeOfCurrentStateSet
            )
        }

        WorkState.Pause -> {
            CardState.Pause(
                currentDayOfWeek,
                persistentListOf(),
                currentTime.toTimeWithSeconds() - preferenceRepository.timeOfCurrentStateSet
            )
        }
    }

    private suspend fun getStateForWorking(
        workingRanges: List<ClosedRange<Time>>,
        currentTime: LocalTime,
        time: Time,
        currentDayOfWeek: DayOfWeek,
        totalTime: Time
    ) = when (preferenceRepository.currentWorkState) {
        WorkState.NotWorking -> {
            val range = workingRanges.first { time in it }
            val timeLate = currentTime.toTimeWithSeconds() - range.start.toTimeWithSeconds()
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
            val statistic = workStatisticRepository.getStatistic(LocalDate.now())
            if (totalTime.toTimeWithSeconds() < statistic.workTime) {
                val timeWork =
                    statistic.workTime + currentTime.toTimeWithSeconds() - preferenceRepository.timeOfCurrentStateSet
                CardState.Working(
                    day = currentDayOfWeek,
                    events = persistentListOf(),
                    time = timeWork,
                )
            } else {
                CardState.Working(
                    currentDayOfWeek,
                    persistentListOf(),
                    statistic.workTime + currentTime.toTimeWithSeconds() - preferenceRepository.timeOfCurrentStateSet - totalTime.toTimeWithSeconds(),
                    true
                )
            }

        }

        WorkState.Worked -> {
            val rangeIndex = workingRanges.indexOfFirst { time in it }
            if (rangeIndex < workingRanges.size - 1) {
                val statistic = workStatisticRepository.getStatistic(LocalDate.now())
                CardState.Results(
                    day = currentDayOfWeek,
                    statistic = statistic,
                    events = persistentListOf()
                )
            } else {
                val range = workingRanges[rangeIndex + 1]
                val timeLate = currentTime.toTimeWithSeconds() - range.start.toTimeWithSeconds()
                CardState.WorkStart(
                    day = currentDayOfWeek,
                    buttonActive = true,
                    buttonStartEarly = false,
                    events = persistentListOf(),
                    time = timeLate,
                    late = true
                )
            }
        }

        WorkState.Dinner -> {
            CardState.Dinnering(
                currentDayOfWeek,
                persistentListOf(),
                currentTime.toTimeWithSeconds() - preferenceRepository.timeOfCurrentStateSet
            )
        }

        WorkState.Pause -> {
            CardState.Pause(
                currentDayOfWeek,
                persistentListOf(),
                currentTime.toTimeWithSeconds() - preferenceRepository.timeOfCurrentStateSet
            )
        }
    }

    private suspend fun getStateForEndWork(
        currentDayOfWeek: DayOfWeek,
        currentTime: LocalTime,
        totalTime: Time
    ) =
        when (preferenceRepository.currentWorkState) {
            WorkState.NotWorking, WorkState.Worked -> {
                val statistic = workStatisticRepository.getStatistic(LocalDate.now())
                CardState.Results(
                    day = currentDayOfWeek,
                    statistic = statistic,
                    events = persistentListOf()
                )
            }

            WorkState.Working -> {
                val statistic = workStatisticRepository.getStatistic(LocalDate.now())
                if (totalTime.toTimeWithSeconds() < statistic.workTime) {
                    CardState.Working(
                        currentDayOfWeek,
                        persistentListOf(),
                        statistic.workTime + currentTime.toTimeWithSeconds() - preferenceRepository.timeOfCurrentStateSet,
                    )
                } else {
                    CardState.Working(
                        currentDayOfWeek,
                        persistentListOf(),
                        statistic.workTime + currentTime.toTimeWithSeconds() - preferenceRepository.timeOfCurrentStateSet - totalTime.toTimeWithSeconds(),
                        true
                    )
                }
            }

            WorkState.Dinner -> {
                CardState.Dinnering(
                    currentDayOfWeek,
                    persistentListOf(),
                    currentTime.toTimeWithSeconds() - preferenceRepository.timeOfCurrentStateSet
                )
            }

            WorkState.Pause -> {
                CardState.Pause(
                    currentDayOfWeek,
                    persistentListOf(),
                    currentTime.toTimeWithSeconds() - preferenceRepository.timeOfCurrentStateSet
                )
            }
        }
}