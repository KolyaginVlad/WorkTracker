package ru.kolyagin.worktracker.data.repositories

import ru.kolyagin.worktracker.data.database.dao.WorkStatisticDao
import ru.kolyagin.worktracker.domain.models.TimeWithSeconds
import ru.kolyagin.worktracker.domain.repositories.WorkStatisticRepository
import java.time.LocalDate
import javax.inject.Inject

class WorkStatisticRepositoryImpl @Inject constructor(
    private val workStatisticDao: WorkStatisticDao
) : WorkStatisticRepository {
    override fun addWorkTime(date: LocalDate, time: TimeWithSeconds) {
        TODO("Not yet implemented")
    }

    override fun addPlannedPauseTime(date: LocalDate, time: TimeWithSeconds) {
        TODO("Not yet implemented")
    }

    override fun addUnplannedPauseTime(date: LocalDate, time: TimeWithSeconds) {
        TODO("Not yet implemented")
    }
}