package ru.kolyagin.worktracker.ui.main

import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import ru.kolyagin.worktracker.domain.models.Time
import ru.kolyagin.worktracker.domain.models.TimeWithSeconds
import ru.kolyagin.worktracker.ui.models.DayStartEvent
import ru.kolyagin.worktracker.utils.base.State
import ru.kolyagin.worktracker.utils.models.DayOfWeek

data class MainScreenState(
    val days: PersistentList<CardState> = persistentListOf()
): State()

sealed class CardState(open val day: DayOfWeek) {

    /**
     * Состояние перед началом рабочего периода
     */
    data class WorkStart(override val day: DayOfWeek,
        val buttonActive: Boolean,
        val buttonStartEarly:Boolean,
        val events: PersistentList<DayStartEvent>,
        val time: TimeWithSeconds? = null
    ) :
        CardState(day)

    /**
     * Состояние в конце рабочего периода
     */
    data class WorkEnd(override val day: DayOfWeek) : CardState(day)

    /**
     * Состояние во время работы
     */
    data class Working(override val day: DayOfWeek) : CardState(day)

    /**
     * Состояние во время рабочей паузы
     */
    data class Pause(override val day: DayOfWeek) : CardState(day)

    /**
     * Состояние во время обеда в рабочий период
     */
    data class Dinnering(override val day: DayOfWeek) : CardState(day)

    /**
     * Состояние после завершения последнего периода работы
     */
    data class Results(override val day: DayOfWeek, val time: Time) : CardState(day)
}