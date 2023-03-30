package ru.kolyagin.worktracker.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.kolyagin.worktracker.domain.models.Time
import ru.kolyagin.worktracker.domain.models.WorkEvent

@Entity("WorkEvent")
data class WorkEventEntity(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo("id")
    val id: Long,

    @ColumnInfo("name")
    val name: String,

    @ColumnInfo("timeStart")
    val timeStart: Time,

    @ColumnInfo("timeEnd")
    val timeEnd: Time,

    @ColumnInfo("day")
    val day: Int,

    @ColumnInfo("isDinner")
    val isDinner: Boolean,
)
fun WorkEventEntity.mapToDomain() =
    WorkEvent(
        id = id,
        timeStart = timeStart,
        timeEnd = timeEnd,
        name = name,
        isDinner=isDinner
    )