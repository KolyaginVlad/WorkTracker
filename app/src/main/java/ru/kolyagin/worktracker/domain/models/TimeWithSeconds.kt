package ru.kolyagin.worktracker.domain.models

import java.time.LocalTime

class TimeWithSeconds(val hours: Int, val minutes: Int, val seconds: Int) :
    Comparable<TimeWithSeconds> {

    operator fun minus(other: TimeWithSeconds): TimeWithSeconds {
        return fromSeconds(this.toSeconds() - other.toSeconds())
    }

    operator fun plus(other: TimeWithSeconds): TimeWithSeconds {
        return fromSeconds(this.toSeconds() + other.toSeconds())
    }

    override operator fun compareTo(other: TimeWithSeconds): Int {
        val res = this.toSeconds() - other.toSeconds()
        return if (res == 0L) 0 else if (res < 0) -1 else 1
    }

    override fun toString(): String {
        return "$hours:${"%02d".format(minutes)}:${"%02d".format(seconds)}"
    }

    override fun equals(other: Any?): Boolean {
        if (other == null) return false
        if (this === other) return true
        if (other !is TimeWithSeconds) return false
        return hours == other.hours && minutes == other.minutes && seconds == other.seconds
    }

    override fun hashCode(): Int {
        var result = hours
        result = 31 * result + minutes
        result = 31 * result + seconds
        return result
    }


    companion object {
        fun fromSeconds(seconds: Long): TimeWithSeconds {
            val hours = seconds / (60 * 60)
            val min = seconds % (60 * 60)
            val minutes = min / 60
            val seconds = min % 60
            return TimeWithSeconds(hours.toInt(), minutes.toInt(), seconds.toInt())
        }

        fun LocalTime.toTimeWithSeconds() =
            TimeWithSeconds(hour, minute, second)

        fun TimeWithSeconds.toSeconds(): Long =
            (hours * 60 * 60 + minutes * 60 + seconds).toLong()

        fun TimeWithSeconds.toTime() = Time(hours, minutes)
    }
}