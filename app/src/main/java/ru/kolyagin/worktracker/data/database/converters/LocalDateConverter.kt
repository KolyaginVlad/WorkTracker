package ru.kolyagin.worktracker.data.database.converters
import androidx.room.TypeConverter
import java.time.LocalDate

class LocalDateConverter {

    @TypeConverter
    fun fromDays(value: Long?): LocalDate? {
        return value?.let { LocalDate.ofEpochDay(it) }
    }

    @TypeConverter
    fun dateToDays(date: LocalDate?): Long? {
        return date?.toEpochDay()
    }
}