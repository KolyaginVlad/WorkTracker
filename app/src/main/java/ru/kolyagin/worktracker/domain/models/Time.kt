package ru.kolyagin.worktracker.domain.models

class Time(val hours: Int, val minutes: Int) : Comparable<Time> {

    operator fun minus(other: Time): Time {
        return fromMinutes(this.toMinutes() - other.toMinutes())
    }

    operator fun plus(other: Time): Time {
        return fromMinutes(this.toMinutes() + other.toMinutes())
    }

    override operator fun compareTo(other: Time): Int {
        return this.toMinutes() - other.toMinutes()
    }

    override fun toString(): String {
        return "$hours:${"%02d".format(minutes)}"
    }

    companion object {
        fun fromMinutes(minutes: Int): Time {
            val hours = minutes / 60
            val min = minutes % 60
            return Time(hours, min)
        }

        fun Time.toMinutes(): Int =
            hours * 60 + minutes

        fun Time.toTimeWithSeconds(seconds: Int = 0) = TimeWithSeconds(hours, minutes, seconds)
    }

}