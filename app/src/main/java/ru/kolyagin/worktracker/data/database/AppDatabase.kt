package ru.kolyagin.worktracker.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.kolyagin.worktracker.data.database.converters.LocalDateConverter
import ru.kolyagin.worktracker.data.database.converters.TimeConverter
import ru.kolyagin.worktracker.data.database.dao.ScheduleDao
import ru.kolyagin.worktracker.data.database.dao.WorkStatisticDao
import ru.kolyagin.worktracker.data.database.entities.DayScheduleEntity
import ru.kolyagin.worktracker.data.database.entities.WorkEventEntity
import ru.kolyagin.worktracker.data.database.entities.WorkPeriodEntity
import ru.kolyagin.worktracker.data.database.entities.WorkStatisticEntity

@Database(
    entities = [
        DayScheduleEntity::class,
        WorkPeriodEntity::class,
        WorkStatisticEntity::class,
        WorkEventEntity::class
    ], version = 2, exportSchema = false
)
@TypeConverters(TimeConverter::class, LocalDateConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getScheduleDao(): ScheduleDao

    abstract fun getWorkStatisticDao(): WorkStatisticDao
}