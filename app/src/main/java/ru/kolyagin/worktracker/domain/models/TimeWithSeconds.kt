package ru.kolyagin.worktracker.domain.models

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

    companion object {
        fun fromSeconds(seconds: Long): TimeWithSeconds {
            val hours = seconds / (60 * 60)
            val min = seconds % (60 * 60)
            val minutes = min / 60
            val seconds = min % 60
            return TimeWithSeconds(hours.toInt(), minutes.toInt(), seconds.toInt())
        }

        fun TimeWithSeconds.toSeconds(): Long =
            (hours * 60 * 60 + minutes * 60 + seconds).toLong()

        fun TimeWithSeconds.toTime() = Time(hours, minutes)
    }
}