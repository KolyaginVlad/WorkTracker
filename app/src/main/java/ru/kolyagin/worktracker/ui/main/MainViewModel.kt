package ru.kolyagin.worktracker.ui.main

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import ru.kolyagin.worktracker.domain.models.DayWorkInfo
import ru.kolyagin.worktracker.domain.models.Time
import ru.kolyagin.worktracker.domain.models.Time.Companion.toTimeWithSeconds
import ru.kolyagin.worktracker.domain.models.TimeWithSeconds
import ru.kolyagin.worktracker.domain.models.WorkEvent
import ru.kolyagin.worktracker.domain.models.WorkState
import ru.kolyagin.worktracker.domain.repositories.PreferenceRepository
import ru.kolyagin.worktracker.domain.repositories.ScheduleRepository
import ru.kolyagin.worktracker.utils.base.BaseViewModel
import ru.kolyagin.worktracker.utils.models.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.temporal.ChronoUnit
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val scheduleRepository: ScheduleRepository,
    private val preferenceRepository: PreferenceRepository
) : BaseViewModel<MainScreenState, MainEvent>(MainScreenState()) {
    private var timerJob: Job? = null
    private var schedule: DayWorkInfo? = null
    private var events: Map<Int, List<WorkEvent>> = mapOf()

    private var selectedDayOfWeek: Int? = null
    private var selectedWorkPeriod: WorkEvent? = null
    private var addingEvent: WorkEvent = WorkEvent(
        id = 0,
        timeStart = Time(13, 0),
        timeEnd = Time(14, 0),
        name = "перерыв",
        isDinner = false
    )
    private var enteringTimeStart: Boolean = true


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

    fun init() {/* launchViewModelScope {
             scheduleRepository.addWorkEvent(
                 -1,
                 WorkEvent(
                     id = 0,
                     timeStart = Time(0, 10),
                     timeEnd = Time(10, 1),
                     name = "HIHIHIIHIHIHHI",
                     isDinner = false
                 )
             )
             scheduleRepository.addWorkEvent(
                 1,
                 WorkEvent(
                     id = 0,
                     timeStart = Time(24, 10),
                     timeEnd = Time(10, 1),
                     name = "LALALALALA",
                     isDinner = false
                 )
             )

         }//Test*/
        val listOfDays = DayOfWeek.values().toList()
        scheduleRepository.schedule().subscribe {
            timerJob?.cancel()
            timerJob = launchViewModelScope {
                val currentDay = LocalDate.now().dayOfWeek.ordinal.takeUnless { it == 6 } ?: 0
                schedule = it.getOrNull(LocalDate.now().dayOfWeek.ordinal)
                events = it.associate { Pair(it.day.ordinal, it.events) }
                println(events)
                val startWorkRanges =
                    schedule?.let { schedule -> getListOfTimeRangesStartWork(schedule) }
                val workingRanges =
                    schedule?.let { schedule -> getListOfTimeRangesWorking(schedule) }
                val currentEvents = events[currentDay]?.toPersistentList() ?: persistentListOf()

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
                            currentTime,
                            currentEvents
                        )
                        listOf(1, 1, 1) + listOf(1, 1)
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
                        state.copy(
                            days = (listOf(currentState) + daysWithOutCurrent).toPersistentList()
                        )
                    }
                    delay(
                        ChronoUnit.MILLIS.between(
                            currentTime.plusSeconds(1), LocalTime.now()
                        )
                    )
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
        //TODO Посмотреть насколько раньше\позднее начали работать чем запланировано

    }

    fun onClickFinishWork() {
        preferenceRepository.currentWorkState = WorkState.NotWorking
        updateCardState()
        //TODO Посмотреть насколько раньше\позднее закончили работать чем запланировано
    }

    fun onClickStartPause() {
        preferenceRepository.currentWorkState = WorkState.Pause
        updateCardState()
        //TODO Посмотреть запланированная ли это пауза
    }

    fun onClickEndPause() {
        preferenceRepository.currentWorkState = WorkState.Working
        updateCardState()
        //TODO Подсчитать сколько времени в запланированной паузе, а сколько нет
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

    fun onClickDeleteEvent(workEvent: WorkEvent) {
        launchViewModelScope {
            scheduleRepository.deleteWorkEvent(workEvent)
        }
    }

    fun onClickAddEvent(day: Int) {
        selectedDayOfWeek = day
        trySendEvent(MainEvent.AddTimeStart)
    }

    fun onTimePicked(time: Time) {
        selectedDayOfWeek?.let { dayOfWeek ->
            launchViewModelScope {
                addingEvent.let {
                    if (enteringTimeStart) {
                        addingEvent = it.copy(timeStart = time)
                        trySendEvent(MainEvent.AddTimeEnd)
                    } else {
                        addingEvent = it.copy(timeEnd = time)
                        scheduleRepository.addWorkEvent(dayOfWeek,it)
                    }
                }
                enteringTimeStart = !enteringTimeStart
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

    private fun updateCardState() {
        val currentDay = LocalDate.now().dayOfWeek.ordinal.takeUnless { it == 6 } ?: 0
        val startWorkRanges = schedule?.let { schedule -> getListOfTimeRangesStartWork(schedule) }
        val workingRanges = schedule?.let { schedule -> getListOfTimeRangesWorking(schedule) }
        val currentTime = LocalTime.now()
        val time = Time(currentTime.hour, currentTime.minute)
        val listOfDays = DayOfWeek.values().toList()
        val currentEvents = events[currentDay]?.toPersistentList() ?: persistentListOf()
        updateState { state ->
            val currentState = getCurrentState(
                startWorkRanges,
                workingRanges,
                time,
                listOfDays[currentDay],
                schedule?.totalTime, //TODO убрать и заменить внутри функции на выборку статистики из репозитория
                currentTime,
                currentEvents
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
        currentTime: LocalTime,
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
            getStateForStartWork(startWorkRanges, currentTime, time, currentDayOfWeek, workEvents)
        }

        workingRanges.any { time in it } -> {
            getStateForWorking(workingRanges, currentTime, time, currentDayOfWeek, workEvents)
        }

        else -> {
            getStateForEndWork(currentDayOfWeek, totalTime, workEvents)
        }
    }

    private fun getStateForStartWork(
        startWorkRanges: List<ClosedRange<Time>>,
        currentTime: LocalTime,
        time: Time,
        currentDayOfWeek: DayOfWeek,
        workEvents: PersistentList<WorkEvent>
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
                events = workEvents,
                late = false,
                time = timeBeforeWork
            )
        }

        WorkState.Working -> {
            CardState.Working(
                currentDayOfWeek, workEvents, TimeWithSeconds.fromSeconds(0)
            )
        }

        WorkState.Dinner -> {
            CardState.Dinnering(
                currentDayOfWeek, workEvents, TimeWithSeconds.fromSeconds(0)
            ) //TODO Приписать логику
        }

        WorkState.Pause -> {
            CardState.Pause(
                currentDayOfWeek, workEvents, TimeWithSeconds.fromSeconds(0)
            ) //TODO Приписать логику
        }
    }

    private fun getStateForWorking(
        workingRanges: List<ClosedRange<Time>>,
        currentTime: LocalTime,
        time: Time,
        currentDayOfWeek: DayOfWeek,
        workEvents: PersistentList<WorkEvent>
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
                events = workEvents,
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
                events = workEvents,
                time = timeWork,
            )
        }

        WorkState.Dinner -> {
            CardState.Dinnering(
                currentDayOfWeek, workEvents, TimeWithSeconds.fromSeconds(0)
            ) //TODO Приписать логику
        }

        WorkState.Pause -> {
            CardState.Pause(
                currentDayOfWeek, workEvents, TimeWithSeconds.fromSeconds(0)
            ) //TODO Приписать логику
        }
    }

    private fun getStateForEndWork(
        currentDayOfWeek: DayOfWeek, totalTime: Time, workEvents: PersistentList<WorkEvent>
    ) = when (preferenceRepository.currentWorkState) {
        WorkState.NotWorking -> {
            CardState.Results(currentDayOfWeek, totalTime)
        }

        WorkState.Working -> {
            CardState.Working(
                day = currentDayOfWeek,
                events = workEvents,
                time = totalTime.toTimeWithSeconds(),
                overwork = true
            )
            // переработка
        }

        WorkState.Dinner -> {
            CardState.Dinnering(
                currentDayOfWeek, workEvents, TimeWithSeconds.fromSeconds(0)
            ) //TODO Приписать логику
        }

        WorkState.Pause -> {
            CardState.Pause(
                currentDayOfWeek, workEvents, TimeWithSeconds.fromSeconds(0)
            ) //TODO Приписать логику
        }
    }
}