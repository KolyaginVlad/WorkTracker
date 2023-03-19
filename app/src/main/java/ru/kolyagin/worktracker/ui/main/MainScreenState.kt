package ru.kolyagin.worktracker.ui.main

import ru.kolyagin.worktracker.domain.models.Time
import ru.kolyagin.worktracker.utils.base.State

sealed class MainScreenState : State() {
    /**
     * Состояние для инициализации экрана
     */
    object Init: MainScreenState()

    /**
     * Состояние перед началом рабочего периода
     */
    object WorkStart : MainScreenState()

    /**
     * Состояние в конце рабочего периода
     */
    object WorkEnd : MainScreenState()

    /**
     * Состояние во время работы
     */
    object Working : MainScreenState()

    /**
     * Состояние во время рабочей паузы
     */
    object Pause : MainScreenState()

    /**
     * Состояние во время обеда в рабочий период
     */
    object Dinnering : MainScreenState()

    /**
     * Состояние после завершения последнего периода работы
     */
    data class Results(val time: Time) : MainScreenState()
}