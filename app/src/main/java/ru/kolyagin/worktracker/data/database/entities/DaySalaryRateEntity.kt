package ru.kolyagin.worktracker.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import ru.kolyagin.worktracker.domain.models.DaySalaryRate
import java.time.DayOfWeek

@Entity("DaySalaryRate", indices = [Index(value = arrayOf("day"), unique = true)])
data class DaySalaryRateEntity(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo("id")
    val id: Long,

    @ColumnInfo("day")
    val day: Int,

    @ColumnInfo("rate")
    val rate: Long
)

fun DaySalaryRateEntity.mapToDomain() =
    DaySalaryRate(
        id = id,
        day = DayOfWeek.of(day + 1),
        rate = rate
    )