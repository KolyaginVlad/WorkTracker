package ru.kolyagin.worktracker.data.repositories

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import ru.kolyagin.worktracker.data.database.dao.ScheduleDao
import ru.kolyagin.worktracker.data.database.entities.DayScheduleEntity
import ru.kolyagin.worktracker.data.database.entities.WorkPeriodEntity
import ru.kolyagin.worktracker.data.database.entities.mapToDomain
import ru.kolyagin.worktracker.domain.models.DayWorkInfo
import ru.kolyagin.worktracker.domain.models.WorkPeriod
import ru.kolyagin.worktracker.domain.repositories.ScheduleRepository
import java.time.DayOfWeek
import javax.inject.Inject

class ScheduleRepositoryImpl @Inject constructor(
    private val scheduleDao: ScheduleDao
) : ScheduleRepository {
    override fun schedule(): Flow<ImmutableList<DayWorkInfo>> =
        combine(
            scheduleDao.getDaySchedule(),
            scheduleDao.getWorkPeriods()
        ) { dayScheduleEntities: List<DayScheduleEntity>, workPeriodEntities: List<WorkPeriodEntity> ->
            val groups = workPeriodEntities.groupBy { it.day }
            dayScheduleEntities.map { scheduleEntity ->
                DayWorkInfo(
                    day = DayOfWeek.values()[scheduleEntity.day],
                    periods = groups[scheduleEntity.day]?.map { it.mapToDomain() }
                        ?.toImmutableList() ?: persistentListOf(),
                    isDinnerInclude = scheduleEntity.isDinnerInclude
                )
            }.toImmutableList()
        }

    override suspend fun addPeriod(dayOfWeek: DayOfWeek) {
        scheduleDao.addPeriod(dayOfWeek.ordinal)
    }

    override suspend fun deletePeriod(workPeriod: WorkPeriod) {
        scheduleDao.deletePeriod(workPeriod.id)
    }

    override suspend fun setTime(
        workPeriod: WorkPeriod,
        dayOfWeek: DayOfWeek
    ) {
        scheduleDao.setTime(
            WorkPeriodEntity(
                workPeriod.id,
                workPeriod.timeStart,
                workPeriod.timeEnd,
                dayOfWeek.ordinal
            )
        )
    }

    override suspend fun setDinner(dayOfWeek: DayOfWeek, isDinnerInclude: Boolean) {
        scheduleDao.setDinner(dayOfWeek.ordinal, isDinnerInclude)
    }
}