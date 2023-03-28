package ru.kolyagin.worktracker.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.kolyagin.worktracker.domain.models.TimeWithSeconds

@Entity("WorkStatistic")
data class WorkStatisticEntity(

    @PrimaryKey
    @ColumnInfo("id")
    val id: Long,

    @ColumnInfo("dayOfMonth")
    val dayOfMonth: Int,

    @ColumnInfo("month")
    val month: Int,

    @ColumnInfo("year")
    val year: Int,

    @ColumnInfo("workTime")
    val workTime: TimeWithSeconds,

    @ColumnInfo("plannedPauseTime")
    val plannedPauseTime: TimeWithSeconds,

    @ColumnInfo("unplannedPauseTime")
    val unplannedPauseTime: TimeWithSeconds
)