package ru.kolyagin.worktracker.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.kolyagin.worktracker.domain.models.TimeWithSeconds
import ru.kolyagin.worktracker.domain.models.WorkStatistic
import java.time.LocalDate

@Entity("WorkStatistic")
data class WorkStatisticEntity(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo("id")
    val id: Long,

    @ColumnInfo("date")
    val date: LocalDate,

    @ColumnInfo("workTime")
    val workTime: TimeWithSeconds,

    @ColumnInfo("plannedPauseTime")
    val plannedPauseTime: TimeWithSeconds,

    @ColumnInfo("unplannedPauseTime")
    val unplannedPauseTime: TimeWithSeconds
)

fun WorkStatisticEntity.mapToDomain() =
    WorkStatistic(
        workTime = workTime,
        plannedPauseTime = plannedPauseTime,
        unplannedPauseTime = unplannedPauseTime
    )