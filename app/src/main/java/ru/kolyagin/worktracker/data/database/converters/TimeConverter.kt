package ru.kolyagin.worktracker.data.database.converters

import androidx.room.TypeConverter
import ru.kolyagin.worktracker.domain.models.Time
import ru.kolyagin.worktracker.domain.models.Time.Companion.toMinutes
import ru.kolyagin.worktracker.domain.models.TimeWithSeconds
import ru.kolyagin.worktracker.domain.models.TimeWithSeconds.Companion.toSeconds

class TimeConverter {

    @TypeConverter
    fun toTime(time: Int): Time =
        Time.fromMinutes(time)

    @TypeConverter
    fun toMinutes(time: Time): Int =
        time.toMinutes()

    @TypeConverter
    fun toTimeWithSeconds(time: Long): TimeWithSeconds =
        TimeWithSeconds.fromSeconds(time)

    @TypeConverter
    fun toSeconds(time: TimeWithSeconds): Long =
        time.toSeconds()
}