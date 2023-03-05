package ru.kolyagin.worktracker.utils

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

inline fun <T : Any, F : Any> Flow<List<T>>.mapIterable(
    crossinline transform: (value: T) -> F
): Flow<List<F>> =
    this.map { it.map(transform) }