package ru.kolyagin.worktracker.ui.utils

import androidx.annotation.StringRes
import ru.kolyagin.worktracker.R
import ru.kolyagin.worktracker.utils.models.DayOfWeek

@StringRes
fun DayOfWeek.toStringId(): Int =
    when (this) {
        DayOfWeek.Monday -> R.string.monday
        DayOfWeek.Tuesday -> R.string.tuesday
        DayOfWeek.Wednesday -> R.string.wednesday
        DayOfWeek.Thursday -> R.string.thursday
        DayOfWeek.Friday -> R.string.friday
        DayOfWeek.Saturday -> R.string.saturday
    }


@StringRes
fun DayOfWeek.toShortStringId(): Int =
    when (this) {
        DayOfWeek.Monday -> R.string.shortMonday
        DayOfWeek.Tuesday -> R.string.shortTuesday
        DayOfWeek.Wednesday -> R.string.shortWednesday
        DayOfWeek.Thursday -> R.string.shortThursday
        DayOfWeek.Friday -> R.string.shortFriday
        DayOfWeek.Saturday -> R.string.shortSaturday
    }