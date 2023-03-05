package ru.kolyagin.worktracker.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("DaySchedule")
data class DayScheduleEntity (
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo("day")
    val day: Int,

    @ColumnInfo("isDinnerInclude")
    val isDinnerInclude: Boolean
)
