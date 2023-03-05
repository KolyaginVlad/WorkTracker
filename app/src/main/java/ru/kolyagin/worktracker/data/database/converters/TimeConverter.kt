package ru.kolyagin.worktracker.data.database.converters

import androidx.room.TypeConverter
import ru.kolyagin.worktracker.domain.models.Time
import ru.kolyagin.worktracker.domain.models.Time.Companion.toMinutes

class TimeConverter {

    @TypeConverter
    fun toTime(time: Int): Time =
        Time.fromMinutes(time)

    @TypeConverter
    fun toMinutes(time: Time): Int =
        time.toMinutes()
}