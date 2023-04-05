package ru.kolyagin.worktracker.utils

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalTime

inline fun <T : Any, F : Any> Flow<List<T>>.mapIterable(
    crossinline transform: (value: T) -> F
): Flow<List<F>> =
    this.map { it.map(transform) }

fun LocalTime.toSeconds() =
    hour * 3600L + minute * 60L + second