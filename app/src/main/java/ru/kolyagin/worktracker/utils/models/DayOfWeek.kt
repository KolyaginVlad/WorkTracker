package ru.kolyagin.worktracker.utils.models

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources

enum class DayOfWeek {
    Monday,
    Tuesday,
    Wednesday,
    Thursday,
    Friday,
    Saturday
}
@SuppressLint("DiscouragedApi")
fun DayOfWeek.toShortString(context: Context): String {
    val string = "short" + this.name
    val res: Resources = context.resources
    return res.getString(res.getIdentifier(string, "string", context.packageName))
}