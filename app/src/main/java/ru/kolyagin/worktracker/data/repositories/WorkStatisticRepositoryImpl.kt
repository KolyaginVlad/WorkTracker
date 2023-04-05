package ru.kolyagin.worktracker.data.repositories

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import ru.kolyagin.worktracker.data.database.dao.WorkStatisticDao
import ru.kolyagin.worktracker.data.database.entities.mapToDomain
import ru.kolyagin.worktracker.domain.models.TimeWithSeconds
import ru.kolyagin.worktracker.domain.models.WorkStatistic
import ru.kolyagin.worktracker.domain.repositories.WorkStatisticRepository
import ru.kolyagin.worktracker.utils.Constants
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Named

class WorkStatisticRepositoryImpl @Inject constructor(
    private val workStatisticDao: WorkStatisticDao,
    @Named(Constants.DATA_SCOPE) scope: CoroutineScope
) : WorkStatisticRepository {

    private val cashedDate = LocalDate.now()
    private var cashedStatistic: WorkStatistic? = null

    init {
        scope.launch {
            workStatisticDao.getStatisticFlow(
                cashedDate.dayOfMonth,
                cashedDate.month.ordinal,
                cashedDate.year,
            ).collect {
                cashedStatistic = it?.mapToDomain()
            }
        }
    }

    override suspend fun addWorkTime(date: LocalDate, time: TimeWithSeconds) {
        workStatisticDao.addWorkTime(
            date.dayOfMonth,
            date.month.ordinal,
            date.year,
            time
        )
    }

    override suspend fun addPlannedPauseTime(date: LocalDate, time: TimeWithSeconds) {
        workStatisticDao.addPlannedPauseTime(
            date.dayOfMonth,
            date.month.ordinal,
            date.year,
            time
        )
    }

    override suspend fun addUnplannedPauseTime(date: LocalDate, time: TimeWithSeconds) {
        workStatisticDao.addUnplannedPauseTime(
            date.dayOfMonth,
            date.month.ordinal,
            date.year,
            time
        )
    }

    override suspend fun getStatistic(date: LocalDate): WorkStatistic =
        (if (date == cashedDate)
            cashedStatistic
        else
            workStatisticDao.getStatistic(
                date.dayOfMonth,
                date.month.ordinal,
                date.year,
            )?.mapToDomain()) ?: WorkStatistic(
            TimeWithSeconds.fromSeconds(0),
            TimeWithSeconds.fromSeconds(0),
            TimeWithSeconds.fromSeconds(0)
        )
}