package ru.kolyagin.worktracker.data.repositories

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import ru.kolyagin.worktracker.data.database.dao.ScheduleDao
import ru.kolyagin.worktracker.data.database.entities.DayScheduleEntity
import ru.kolyagin.worktracker.data.database.entities.WorkEventEntity
import ru.kolyagin.worktracker.data.database.entities.WorkPeriodEntity
import ru.kolyagin.worktracker.data.database.entities.mapToDomain
import ru.kolyagin.worktracker.domain.models.DayWorkInfo
import ru.kolyagin.worktracker.domain.models.WorkEvent
import ru.kolyagin.worktracker.domain.models.WorkPeriod
import ru.kolyagin.worktracker.domain.repositories.ScheduleRepository
import ru.kolyagin.worktracker.utils.models.DayOfWeek
import javax.inject.Inject

class ScheduleRepositoryImpl @Inject constructor(
    private val scheduleDao: ScheduleDao
) : ScheduleRepository {
    override fun schedule(): Flow<ImmutableList<DayWorkInfo>> = combine(
        scheduleDao.getDaySchedule(), scheduleDao.getWorkPeriods(), scheduleDao.getWorkEvents()
    ) { dayScheduleEntities: List<DayScheduleEntity>, workPeriodEntities: List<WorkPeriodEntity>, events: List<WorkEventEntity> ->
        val groups = workPeriodEntities.groupBy { it.day }
        val eventGroups = events.groupBy({ it.day }) { it.mapToDomain() }
        dayScheduleEntities.map { scheduleEntity ->
            DayWorkInfo(
                day = DayOfWeek.values()[scheduleEntity.day],
                periods = groups[scheduleEntity.day]?.map { it.mapToDomain() }?.toImmutableList()
                    ?: persistentListOf(),
                isDinnerInclude = scheduleEntity.isDinnerInclude,
                events = (eventGroups[scheduleEntity.day]?.plus(
                    eventGroups[-1] ?: listOf()
                ))?.toImmutableList() ?: eventGroups[-1]?.toImmutableList() ?: persistentListOf()
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
        workPeriod: WorkPeriod, dayOfWeek: DayOfWeek
    ) {
        scheduleDao.setTime(
            WorkPeriodEntity(
                workPeriod.id, workPeriod.timeStart, workPeriod.timeEnd, dayOfWeek.ordinal
            )
        )
    }

    override suspend fun setDinner(dayOfWeek: DayOfWeek, isDinnerInclude: Boolean) {
        scheduleDao.setDinner(dayOfWeek.ordinal, isDinnerInclude)
    }


    override suspend fun addWorkEvent(dayOfWeek: Int, name: String) {
        scheduleDao.addEvent(dayOfWeek, name)
    }

    override suspend fun deleteWorkEvent(workEvent: WorkEvent) {
        scheduleDao.deleteEvent(workEvent.id)
    }

    override suspend fun setWorkEventTime(
        workEvent: WorkEvent, dayOfWeek: Int
    ) {
        scheduleDao.setEventTime(
            WorkEventEntity(
                workEvent.id,
                workEvent.name,
                workEvent.timeStart,
                workEvent.timeEnd,
                dayOfWeek,
                workEvent.isLunch
            )
        )
    }
}