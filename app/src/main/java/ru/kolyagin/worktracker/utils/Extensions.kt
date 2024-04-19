package ru.kolyagin.worktracker.utils

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId

inline fun <T : Any, F : Any> Flow<List<T>>.mapIterable(
    crossinline transform: (value: T) -> F
): Flow<List<F>> =
    this.map { it.map(transform) }

fun LocalTime.toSeconds() =
    hour * 3600L + minute * 60L + second

fun Long.toLocalDate(): LocalDate =
    LocalDateTime.ofInstant(
        Instant.ofEpochMilli(
            this
        ), ZoneId.systemDefault()
    ).toLocalDate()