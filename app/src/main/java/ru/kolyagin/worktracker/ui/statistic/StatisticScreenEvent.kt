package ru.kolyagin.worktracker.ui.statistic

import ru.kolyagin.worktracker.utils.base.Event

sealed class StatisticScreenEvent: Event() {

    class ShowRangePicker(val start: Long, val end: Long) : StatisticScreenEvent()
}