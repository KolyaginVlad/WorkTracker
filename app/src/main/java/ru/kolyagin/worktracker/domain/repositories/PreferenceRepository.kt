package ru.kolyagin.worktracker.domain.repositories

import ru.kolyagin.worktracker.domain.models.TimeWithSeconds
import ru.kolyagin.worktracker.domain.models.WorkState

interface PreferenceRepository {
    var currentWorkState: WorkState
    val timeOfCurrentStateSet: TimeWithSeconds
}