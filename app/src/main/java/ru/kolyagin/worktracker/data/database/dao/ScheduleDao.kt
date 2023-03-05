package ru.kolyagin.worktracker.data.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import ru.kolyagin.worktracker.data.database.entities.DayScheduleEntity
import ru.kolyagin.worktracker.data.database.entities.WorkPeriodEntity
import ru.kolyagin.worktracker.domain.models.Time

@Dao
interface ScheduleDao {

    @Query(
        """
            SELECT * FROM DaySchedule
        """
    )
    fun getDaySchedule(): Flow<List<DayScheduleEntity>>

    @Query(
        """
            SELECT * FROM WorkPeriod
        """
    )
    fun getWorkPeriods(): Flow<List<WorkPeriodEntity>>

    @Query(
        """
             INSERT INTO WorkPeriod (timeStart, timeEnd, day) VALUES (:timeStart, :timeEnd, :day)
        """
    )
    suspend fun addPeriod(
        day: Int,
        timeStart: Time = Time(8, 0),
        timeEnd: Time = Time(17, 0)
    )

    @Query(
        """
            DELETE FROM WorkPeriod WHERE id == :id
        """
    )
    suspend fun deletePeriod(id: Long)

    @Update
    suspend fun setTime(workPeriodEntity: WorkPeriodEntity)

    @Query(
        """
            UPDATE DaySchedule SET isDinnerInclude = :isDinnerInclude WHERE day == :id
        """
    )
    suspend fun setDinner(id: Int, isDinnerInclude: Boolean)
}