package ru.kolyagin.worktracker.data.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import ru.kolyagin.worktracker.data.database.entities.WorkStatisticEntity
import ru.kolyagin.worktracker.domain.models.TimeWithSeconds
import java.time.LocalDate

@Dao
abstract class WorkStatisticDao {

    @Transaction
    open suspend fun addWorkTime(date: LocalDate, time: TimeWithSeconds) {
        val statistic = getOrInsert(date)
        upsert(statistic.copy(workTime = statistic.workTime + time))
    }

    @Transaction
    open suspend fun addPlannedPauseTime(date: LocalDate, time: TimeWithSeconds) {
        val statistic = getOrInsert(date)
        upsert(statistic.copy(plannedPauseTime = statistic.plannedPauseTime + time))
    }

    @Transaction
    open suspend fun addUnplannedPauseTime(date: LocalDate, time: TimeWithSeconds) {
        val statistic = getOrInsert(date)
        upsert(statistic.copy(unplannedPauseTime = statistic.unplannedPauseTime + time))
    }

    @Query("SELECT * FROM WorkStatistic WHERE date = :date")
    abstract suspend fun getStatistic(date: LocalDate): WorkStatisticEntity?

    @Query("SELECT * FROM WorkStatistic WHERE date BETWEEN :dateStart AND :dateEnd")
    abstract suspend fun getStatistic(dateStart: LocalDate, dateEnd: LocalDate): List<WorkStatisticEntity>

    @Query("SELECT * FROM WorkStatistic WHERE date = :date")
    abstract fun getStatisticFlow(date: LocalDate): Flow<WorkStatisticEntity?>

    @Upsert
    protected abstract suspend fun upsert(statistic: WorkStatisticEntity): Long

    @Transaction
    protected open suspend fun getOrInsert(date: LocalDate): WorkStatisticEntity {
        val statistic = getStatistic(date) ?: run {
            val stat = WorkStatisticEntity(
                id = 0,
                date = date,
                plannedPauseTime = TimeWithSeconds.fromSeconds(0),
                unplannedPauseTime = TimeWithSeconds.fromSeconds(0),
                workTime = TimeWithSeconds.fromSeconds(0),
            )
            stat.copy(id = upsert(stat))
        }
        return statistic
    }
}