package ru.kolyagin.worktracker.ui.utils

import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.fragment.app.FragmentManager
import ru.kolyagin.worktracker.R
import java.time.DayOfWeek

@StringRes
fun DayOfWeek.toStringId(): Int =
    when (this) {
        DayOfWeek.MONDAY -> R.string.monday
        DayOfWeek.TUESDAY -> R.string.tuesday
        DayOfWeek.WEDNESDAY -> R.string.wednesday
        DayOfWeek.THURSDAY -> R.string.thursday
        DayOfWeek.FRIDAY -> R.string.friday
        DayOfWeek.SATURDAY -> R.string.saturday
        DayOfWeek.SUNDAY -> R.string.sunday
    }


@StringRes
fun DayOfWeek.toShortStringId(): Int =
    when (this) {
        DayOfWeek.MONDAY -> R.string.shortMonday
        DayOfWeek.TUESDAY -> R.string.shortTuesday
        DayOfWeek.WEDNESDAY -> R.string.shortWednesday
        DayOfWeek.THURSDAY -> R.string.shortThursday
        DayOfWeek.FRIDAY -> R.string.shortFriday
        DayOfWeek.SATURDAY -> R.string.shortSaturday
        DayOfWeek.SUNDAY -> R.string.shortSunday
    }

fun max(dp1: Dp, dp: Dp) =
    if (dp1 > dp) dp1 else dp

@Composable
fun rememberFragmentManager(): FragmentManager {
    val context = LocalContext.current
    return remember(context){ (context as AppCompatActivity).supportFragmentManager }
}