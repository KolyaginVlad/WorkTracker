package ru.kolyagin.worktracker.data.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import ru.kolyagin.worktracker.data.database.entities.WorkStatisticEntity
import ru.kolyagin.worktracker.domain.models.TimeWithSeconds

@Dao
abstract class WorkStatisticDao {

    @Transaction
    open suspend fun addWorkTime(day: Int, month: Int, year: Int, time: TimeWithSeconds) {
        val statistic = getOrInsert(day, month, year)
        upsert(statistic.copy(workTime = statistic.workTime + time))
    }

    @Transaction
    open suspend fun addPlannedPauseTime(day: Int, month: Int, year: Int, time: TimeWithSeconds) {
        val statistic = getOrInsert(day, month, year)
        upsert(statistic.copy(plannedPauseTime = statistic.plannedPauseTime + time))
    }

    @Transaction
    open suspend fun addUnplannedPauseTime(day: Int, month: Int, year: Int, time: TimeWithSeconds) {
        val statistic = getOrInsert(day, month, year)
        upsert(statistic.copy(unplannedPauseTime = statistic.unplannedPauseTime + time))
    }

    @Query("SELECT * FROM WorkStatistic WHERE dayOfMonth == :day AND month == :month AND year == :year")
    abstract suspend fun getStatistic(day: Int, month: Int, year: Int): WorkStatisticEntity?

    @Query("SELECT * FROM WorkStatistic WHERE dayOfMonth == :day AND month == :month AND year == :year")
    abstract fun getStatisticFlow(day: Int, month: Int, year: Int): Flow<WorkStatisticEntity?>

    @Upsert
    protected abstract suspend fun upsert(statistic: WorkStatisticEntity): Long

    @Transaction
    protected open suspend fun getOrInsert(day: Int, month: Int, year: Int): WorkStatisticEntity {
        val statistic = getStatistic(day, month, year) ?: run {
            val stat = WorkStatisticEntity(
                id = 0,
                dayOfMonth = day,
                month = month,
                year = year,
                plannedPauseTime = TimeWithSeconds.fromSeconds(0),
                unplannedPauseTime = TimeWithSeconds.fromSeconds(0),
                workTime = TimeWithSeconds.fromSeconds(0),
            )
            stat.copy(id = upsert(stat))
        }
        return statistic
    }
}