package ru.kolyagin.worktracker.data.database.dao

import androidx.room.Dao
import androidx.room.Ignore
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.kolyagin.worktracker.data.database.entities.DaySalaryRateEntity

@Dao
interface SalaryRateDao {
    @Query(
        """
            SELECT * FROM DaySalaryRate
        """
    )
    fun getSalaryRate(): Flow<List<DaySalaryRateEntity>>

    @Ignore
    @Query(
        """
             INSERT INTO DaySalaryRate (day,rate)  VALUES (:day,:rate) 
        """
    )
    suspend fun addSalaryRate(
        day: Int, rate: Long
    )

    @Query(
        """
            DELETE FROM DaySalaryRate WHERE id == :Id 
        """
    )
    suspend fun deleteSalaryRate(Id: Long)

    @Query(
        """
            UPDATE DaySalaryRate SET rate = :rate WHERE day == :id
        """
    )
    suspend fun setSalaryRate(id: Int, rate: Long)

}