package ru.kolyagin.worktracker.utils.log

import ru.kolyagin.worktracker.utils.analytics.AnalyticsEvent


interface Logger {

    fun error(throwable: Throwable)

    fun error(message: String)

    fun debug(message: String)

    fun info(message: String)

    fun event(
        event: AnalyticsEvent
    )
}