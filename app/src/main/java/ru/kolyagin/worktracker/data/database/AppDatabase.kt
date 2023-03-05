package ru.kolyagin.worktracker.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.kolyagin.worktracker.data.database.converters.TimeConverter
import ru.kolyagin.worktracker.data.database.dao.ScheduleDao
import ru.kolyagin.worktracker.data.database.entities.DayScheduleEntity
import ru.kolyagin.worktracker.data.database.entities.WorkPeriodEntity

@Database(
    entities = [
        DayScheduleEntity::class,
        WorkPeriodEntity::class
    ], version = 1, exportSchema = false
)
@TypeConverters(TimeConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getScheduleDao(): ScheduleDao
}