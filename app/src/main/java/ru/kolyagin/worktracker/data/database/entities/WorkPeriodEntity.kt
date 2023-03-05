package ru.kolyagin.worktracker.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.kolyagin.worktracker.domain.models.Time
import ru.kolyagin.worktracker.domain.models.WorkPeriod

@Entity("WorkPeriod")
data class WorkPeriodEntity(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo("id")
    val id: Long,

    @ColumnInfo("timeStart")
    val timeStart: Time,

    @ColumnInfo("timeEnd")
    val timeEnd: Time,

    @ColumnInfo("day")
    val day: Int
)

fun WorkPeriodEntity.mapToDomain() =
    WorkPeriod(
        id = id,
        timeStart = timeStart,
        timeEnd = timeEnd
    )