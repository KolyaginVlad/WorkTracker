package ru.kolyagin.worktracker.ui.main

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import ru.kolyagin.worktracker.domain.models.DayWorkInfo
import ru.kolyagin.worktracker.domain.models.Time
import ru.kolyagin.worktracker.domain.models.Time.Companion.toTimeWithSeconds
import ru.kolyagin.worktracker.domain.models.TimeWithSeconds
import ru.kolyagin.worktracker.domain.models.TimeWithSeconds.Companion.toSeconds
import ru.kolyagin.worktracker.domain.models.TimeWithSeconds.Companion.toTimeWithSeconds
import ru.kolyagin.worktracker.domain.models.WorkEvent
import ru.kolyagin.worktracker.domain.models.WorkState
import ru.kolyagin.worktracker.domain.repositories.PreferenceRepository
import ru.kolyagin.worktracker.domain.repositories.ScheduleRepository
import ru.kolyagin.worktracker.domain.repositories.WorkStatisticRepository
import ru.kolyagin.worktracker.ui.settings.models.PeriodPart
import ru.kolyagin.worktracker.utils.Constants
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
    private var events: Map<Int, List<WorkEvent>> = mapOf()

    private var selectedDayOfWeek: Int? = null
    private var selectedWorkEvent: WorkEvent? = null
    private var addingEvent: WorkEvent = WorkEvent(
        id = 0,
        timeStart = Time(13, 0),
        timeEnd = Time(14, 0),
        name = Constants.BREAK,
        isDinner = false
    )

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

    init {
        val listOfDays = DayOfWeek.values().toList()
        scheduleRepository.schedule().subscribe {
            timerJob?.cancel()
            timerJob = launchViewModelScope {
                val currentDay = LocalDate.now().dayOfWeek.ordinal
                schedule = it.getOrNull(LocalDate.now().dayOfWeek.ordinal)
                events = it.associate { Pair(it.day.ordinal, it.events) }
                val startWorkRanges =
                    schedule?.let { schedule ->
                        getListOfTimeRangesStartWork(schedule)
                            ?.map { it.start.toTimeWithSeconds()..it.endInclusive.toTimeWithSeconds() }
                    }?.sortedBy { it.start }
                val workingRanges =
                    schedule?.let { schedule ->
                        getListOfTimeRangesWorking(schedule)
                            ?.map { it.start.toTimeWithSeconds()..it.endInclusive.toTimeWithSeconds() }
                    }?.sortedBy { it.start }
                val currentEvents = events[currentDay]?.toPersistentList() ?: persistentListOf()

                do {
                    val isDinnerEnable = schedule?.isDinnerInclude == true &&
                            preferenceRepository.isDinnerEnableToday
                    val currentTime = LocalTime.now()
                    val time = currentTime.toTimeWithSeconds()
                    val currentState = getCurrentState(
                        startWorkRanges,
                        workingRanges,
                        time,
                        listOfDays[currentDay],
                        currentTime,
                        schedule?.totalTime,
                        isDinnerEnable,
                        currentEvents
                    )
                    val daysWithOutCurrent = (listOfDays.subList(
                        currentDay + 1, listOfDays.size
                    ) + listOfDays.subList(0, currentDay)).map { day ->
                        val dayEvent =
                            events[day.ordinal]?.toPersistentList() ?: persistentListOf()
                        CardState.WorkStart(
                            day = day,
                            buttonActive = false,
                            buttonStartEarly = true,
                            events = dayEvent,
                            time = null
                        )
                    }
                    updateState { state ->
                        state.copy(
                            days = (listOf(currentState) + daysWithOutCurrent).toPersistentList()
                        )
                    }
                    delay(ChronoUnit.MILLIS.between(LocalTime.now(), currentTime.plusSeconds(1)))
                } while (isActive)
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
        val dinner =
            (events[LocalDate.now().dayOfWeek.ordinal]?.toPersistentList() ?: persistentListOf())
                .find { it.isDinner }
                ?.let { it.timeEnd.toTimeWithSeconds() - it.timeStart.toTimeWithSeconds() }
                ?: TimeWithSeconds(1,0,0)
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
        val listOfPausesWithoutDinner =
            (events[LocalDate.now().dayOfWeek.ordinal]?.toPersistentList() ?: persistentListOf())
                .filter { !it.isDinner }
                .map { it.timeStart.toTimeWithSeconds()..it.timeEnd.toTimeWithSeconds() }
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

    fun onClickDeleteEvent(workEvent: WorkEvent, day: Int) {
        launchViewModelScope {
            if (workEvent.isDinner) {
                scheduleRepository.setDinner(day, false)
            }
            scheduleRepository.deleteWorkEvent(workEvent)
        }
    }

    fun onClickEvent(day: Int, workEvent: WorkEvent) {
        selectedDayOfWeek = day
        selectedWorkEvent = workEvent
        trySendEvent(MainEvent.ChangeEventTime(workEvent.timeStart, workEvent.timeEnd))
    }

    fun onClickAddEvent(day: Int) {
        selectedDayOfWeek = day
        trySendEvent(MainEvent.AddEventTime)
    }

    fun onTimePicked(time: Time, periodPart: PeriodPart) {
        selectedDayOfWeek?.let { dayOfWeek ->
            launchViewModelScope {
                addingEvent.let {
                    if (periodPart == PeriodPart.START) {
                        addingEvent = it.copy(timeStart = time)
                    } else {
                        addingEvent = it.copy(timeEnd = time)
                        if (addingEvent.timeStart >= addingEvent.timeEnd) {
                            addingEvent = it.copy(
                                timeStart = addingEvent.timeEnd, timeEnd = addingEvent.timeStart
                            )
                        }
                        scheduleRepository.addWorkEvent(dayOfWeek, addingEvent)
                    }
                }
            }
        }
    }

    fun onTimeChanging(time: Time, periodPart: PeriodPart) {
        selectedDayOfWeek?.let { dayOfWeek ->
            launchViewModelScope {
                var newEvent: WorkEvent = addingEvent
                selectedWorkEvent?.let {
                    newEvent = if (periodPart == PeriodPart.START) {
                        it.copy(timeStart = time)
                    } else {
                        it.copy(timeEnd = time)
                    }
                    if (newEvent.timeStart >= newEvent.timeEnd) {
                        newEvent =
                            it.copy(timeStart = newEvent.timeEnd, timeEnd = newEvent.timeStart)
                    }
                    scheduleRepository.setWorkEventTime(newEvent, dayOfWeek)
                }
                selectedWorkEvent = newEvent
            }
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
        val currentDay = LocalDate.now().dayOfWeek.ordinal
        val startWorkRanges =
            schedule?.let { schedule ->
                getListOfTimeRangesStartWork(schedule)
                    ?.map { it.start.toTimeWithSeconds()..it.endInclusive.toTimeWithSeconds() }
            }?.sortedBy { it.start }
        val workingRanges = schedule?.let { schedule ->
            getListOfTimeRangesWorking(schedule)
                ?.map { it.start.toTimeWithSeconds()..it.endInclusive.toTimeWithSeconds() }
        }?.sortedBy { it.start }
        val currentTime = LocalTime.now()
        val time = currentTime.toTimeWithSeconds()
        val listOfDays = DayOfWeek.values().toList()
        val isDinnerEnable = schedule?.isDinnerInclude == true &&
                preferenceRepository.isDinnerEnableToday
        val currentState = getCurrentState(
            startWorkRanges,
            workingRanges,
            time,
            listOfDays[currentDay],
            currentTime,
            schedule?.totalTime,
            isDinnerEnable,
            events[currentDay]?.toPersistentList() ?: persistentListOf()
        )
        val daysWithOutCurrent = (listOfDays.subList(
            currentDay + 1, listOfDays.size
        ) + listOfDays.subList(0, currentDay)).map { day ->

            val dayEvent = events[day.ordinal]?.toPersistentList() ?: persistentListOf()
            CardState.WorkStart(
                day = day,
                buttonActive = false,
                buttonStartEarly = true,
                events = dayEvent,
                time = null
            )
        }
        updateState { state ->
            state.copy(
                days = (listOf(currentState) + daysWithOutCurrent).toPersistentList()
            )
        }
    }

    private suspend fun getCurrentState(
        startWorkRanges: List<ClosedRange<TimeWithSeconds>>?,
        workingRanges: List<ClosedRange<TimeWithSeconds>>?,
        time: TimeWithSeconds,
        currentDayOfWeek: DayOfWeek,
        currentTime: LocalTime,
        totalTime: Time?,
        isDinnerEnable: Boolean,
        workEvents: PersistentList<WorkEvent>
    ) = when {
        startWorkRanges == null || workingRanges == null || totalTime == null -> {
            CardState.WorkStart(
                day = currentDayOfWeek,
                buttonActive = false,
                buttonStartEarly = true,
                events = workEvents,
                time = null
            )
        }

        startWorkRanges.any { time in it } -> {
            if (preferenceRepository.currentWorkState == WorkState.Worked) {
                preferenceRepository.currentWorkState = WorkState.NotWorking
            }
            getStateForStartWork(
                startWorkRanges,
                currentTime,
                time,
                currentDayOfWeek,
                totalTime,
                isDinnerEnable,
                workEvents
            )
        }

        workingRanges.any { time in it } -> {
            getStateForWorking(
                workingRanges,
                currentTime,
                time,
                currentDayOfWeek,
                totalTime,
                isDinnerEnable,
                workEvents
            )
        }

        else -> {
            getStateForEndWork(currentDayOfWeek, currentTime, totalTime, isDinnerEnable, workEvents)
        }
    }

    private suspend fun getStateForStartWork(
        startWorkRanges: List<ClosedRange<TimeWithSeconds>>,
        currentTime: LocalTime,
        time: TimeWithSeconds,
        currentDayOfWeek: DayOfWeek,
        totalTime: Time,
        isDinnerEnable: Boolean,
        workEvents: PersistentList<WorkEvent>
    ) = when (preferenceRepository.currentWorkState) {
        WorkState.NotWorking, WorkState.Worked -> {
            val range = startWorkRanges.first { time in it }
            val timeBeforeWork =
                range.endInclusive - currentTime.toTimeWithSeconds()
            CardState.WorkStart(
                day = currentDayOfWeek,
                buttonActive = true,
                buttonStartEarly = true,
                events = workEvents,
                late = false,
                time = timeBeforeWork
            )
        }

        WorkState.Working -> {
            val statistic = workStatisticRepository.getStatistic(LocalDate.now())
            if (totalTime.toTimeWithSeconds() > statistic.workTime) {
                CardState.Working(
                    currentDayOfWeek,
                    workEvents,
                    statistic.workTime + currentTime.toTimeWithSeconds() - preferenceRepository.timeOfCurrentStateSet,
                    isDinnerEnable,
                )
            } else {
                CardState.Working(
                    currentDayOfWeek,
                    workEvents,
                    statistic.workTime + currentTime.toTimeWithSeconds() - preferenceRepository.timeOfCurrentStateSet - totalTime.toTimeWithSeconds(),
                    isDinnerEnable,
                    true
                )
            }
        }

        WorkState.Dinner -> {
            CardState.Dinnering(
                currentDayOfWeek,
                workEvents,
                currentTime.toTimeWithSeconds() - preferenceRepository.timeOfCurrentStateSet
            )
        }

        WorkState.Pause -> {
            CardState.Pause(
                currentDayOfWeek,
                workEvents,
                currentTime.toTimeWithSeconds() - preferenceRepository.timeOfCurrentStateSet
            )
        }
    }

    private suspend fun getStateForWorking(
        workingRanges: List<ClosedRange<TimeWithSeconds>>,
        currentTime: LocalTime,
        time: TimeWithSeconds,
        currentDayOfWeek: DayOfWeek,
        totalTime: Time,
        isDinnerEnable: Boolean,
        workEvents: PersistentList<WorkEvent>
    ) = when (preferenceRepository.currentWorkState) {
        WorkState.NotWorking -> {
            val range = workingRanges.first { time in it }
            val timeLate = currentTime.toTimeWithSeconds() - range.start
            CardState.WorkStart(
                day = currentDayOfWeek,
                buttonActive = true,
                buttonStartEarly = false,
                events = workEvents,
                time = timeLate,
                late = true
            )
        }

        WorkState.Working -> {
            val statistic = workStatisticRepository.getStatistic(LocalDate.now())
            if (totalTime.toTimeWithSeconds() > statistic.workTime) {
                val timeWork =
                    statistic.workTime + currentTime.toTimeWithSeconds() - preferenceRepository.timeOfCurrentStateSet
                CardState.Working(
                    day = currentDayOfWeek,
                    events = workEvents,
                    time = timeWork,
                    isDinnerEnable = isDinnerEnable,
                )
            } else {
                CardState.Working(
                    currentDayOfWeek,
                    workEvents,
                    statistic.workTime + currentTime.toTimeWithSeconds() - preferenceRepository.timeOfCurrentStateSet - totalTime.toTimeWithSeconds(),
                    isDinnerEnable,
                    true
                )
            }

        }

        WorkState.Worked -> {
            val rangeIndex = workingRanges.indexOfFirst { time in it }
            if (rangeIndex == workingRanges.size - 1) {
                val statistic = workStatisticRepository.getStatistic(LocalDate.now())
                CardState.Results(
                    day = currentDayOfWeek,
                    statistic = statistic,
                    events = workEvents
                )
            } else {
                val range = workingRanges[rangeIndex + 1]
                val time = range.start - currentTime.toTimeWithSeconds()
                CardState.WorkStart(
                    day = currentDayOfWeek,
                    buttonActive = true,
                    buttonStartEarly = false,
                    events = workEvents,
                    time = time,
                )
            }
        }

        WorkState.Dinner -> {
            CardState.Dinnering(
                currentDayOfWeek,
                workEvents,
                currentTime.toTimeWithSeconds() - preferenceRepository.timeOfCurrentStateSet
            )
        }

        WorkState.Pause -> {
            CardState.Pause(
                currentDayOfWeek,
                workEvents,
                currentTime.toTimeWithSeconds() - preferenceRepository.timeOfCurrentStateSet
            )
        }
    }

    private suspend fun getStateForEndWork(
        currentDayOfWeek: DayOfWeek,
        currentTime: LocalTime,
        totalTime: Time,
        isDinnerEnable: Boolean,
        workEvents: PersistentList<WorkEvent>
    ) =
        when (preferenceRepository.currentWorkState) {
            WorkState.NotWorking, WorkState.Worked -> {
                val statistic = workStatisticRepository.getStatistic(LocalDate.now())
                CardState.Results(
                    day = currentDayOfWeek,
                    statistic = statistic,
                    events = workEvents
                )
            }

            WorkState.Working -> {
                val statistic = workStatisticRepository.getStatistic(LocalDate.now())
                if (totalTime.toTimeWithSeconds() > statistic.workTime) {
                    CardState.Working(
                        currentDayOfWeek,
                        workEvents,
                        statistic.workTime + currentTime.toTimeWithSeconds() - preferenceRepository.timeOfCurrentStateSet,
                        isDinnerEnable,
                    )
                } else {
                    CardState.Working(
                        currentDayOfWeek,
                        workEvents,
                        statistic.workTime + currentTime.toTimeWithSeconds() - preferenceRepository.timeOfCurrentStateSet - totalTime.toTimeWithSeconds(),
                        isDinnerEnable,
                        true
                    )
                }
            }

            WorkState.Dinner -> {
                CardState.Dinnering(
                    currentDayOfWeek,
                    workEvents,
                    currentTime.toTimeWithSeconds() - preferenceRepository.timeOfCurrentStateSet
                )
            }

            WorkState.Pause -> {
                CardState.Pause(
                    currentDayOfWeek,
                    workEvents,
                    currentTime.toTimeWithSeconds() - preferenceRepository.timeOfCurrentStateSet
                )
            }
        }
}