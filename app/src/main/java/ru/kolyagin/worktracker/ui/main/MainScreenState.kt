package ru.kolyagin.worktracker.ui.main

import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import ru.kolyagin.worktracker.domain.models.TimeWithSeconds
import ru.kolyagin.worktracker.domain.models.WorkEvent
import ru.kolyagin.worktracker.domain.models.WorkStatistic
import ru.kolyagin.worktracker.utils.base.State
import java.time.DayOfWeek

data class MainScreenState(
    val days: PersistentList<CardState> = persistentListOf()
) : State()

sealed class CardState(open val day: DayOfWeek) {

    /**
     * Состояние перед началом рабочего периода
     */
    data class WorkStart(
        override val day: DayOfWeek,
        val buttonActive: Boolean,
        val buttonStartEarly: Boolean,
        val events: PersistentList<WorkEvent>,
        val time: TimeWithSeconds? = null,
        val late: Boolean = false
    ) : CardState(day)

    /**
     * Состояние во время работы
     */
    data class Working(
        override val day: DayOfWeek,
        val events: PersistentList<WorkEvent>,
        val time: TimeWithSeconds,
        val isDinnerEnable: Boolean = true,
        val overwork: Boolean = false
    ) : CardState(day)

    /**
     * Состояние во время рабочей паузы
     */
    data class Pause(
        override val day: DayOfWeek,
        val events: PersistentList<WorkEvent>,
        val time: TimeWithSeconds
    ) : CardState(day)

    /**
     * Состояние во время обеда в рабочий период
     */
    data class Dinnering(
        override val day: DayOfWeek,
        val events: PersistentList<WorkEvent>,
        val time: TimeWithSeconds
    ) : CardState(day)

    /**
     * Состояние после завершения последнего периода работы
     */
    data class Results(
        override val day: DayOfWeek,
        val statistic: WorkStatistic,
        val events: PersistentList<WorkEvent>,
    ) : CardState(day)
}