package ru.kolyagin.worktracker.domain.repositories

import ru.kolyagin.worktracker.domain.models.Time
import ru.kolyagin.worktracker.domain.models.TimeWithSeconds
import ru.kolyagin.worktracker.domain.models.WorkState

interface PreferenceRepository {
    var currentWorkState: WorkState
    val timeOfCurrentStateSet: TimeWithSeconds
    var isDinnerEnableToday: Boolean
    var isMorningNotificationEnable: Boolean
    var morningNotificationOffset: Time
    var morningRange: ClosedRange<Time>
    var isDinnerNotificationEnable: Boolean
    var dinnerTimeInNotWorkingTime: Time
    var isStartWorkNotificationEnable: Boolean
    var timeBeforeStartWork: Time
    var isEndWorkNotificationEnable: Boolean
    var timeBeforeEndWork: Time
}