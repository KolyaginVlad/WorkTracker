package ru.kolyagin.worktracker.data.repositories

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import ru.kolyagin.worktracker.data.database.dao.ScheduleDao
import ru.kolyagin.worktracker.data.database.entities.WorkPeriodEntity
import ru.kolyagin.worktracker.data.database.entities.mapToDomain
import ru.kolyagin.worktracker.domain.models.DayWorkInfo
import ru.kolyagin.worktracker.domain.models.WorkEvent
import ru.kolyagin.worktracker.domain.models.WorkPeriod
import ru.kolyagin.worktracker.domain.models.mapToEntity
import ru.kolyagin.worktracker.domain.repositories.ScheduleRepository
import java.time.DayOfWeek
import javax.inject.Inject

class ScheduleRepositoryImpl @Inject constructor(
	private val scheduleDao: ScheduleDao
) : ScheduleRepository {
	override fun schedule(): Flow<ImmutableList<DayWorkInfo>> =
		combine(
			scheduleDao.getDaySchedule(),
			scheduleDao.getWorkPeriods(),
			scheduleDao.getWorkEvents()
		) { dayScheduleEntities, workPeriodEntities, events ->
			val groups = workPeriodEntities.groupBy { it.day }
			val eventGroups = events.groupBy({ it.day }) { it.mapToDomain() }
			dayScheduleEntities.map { scheduleEntity ->
				val eventsList = eventGroups[scheduleEntity.day]?.toImmutableList()
					?: persistentListOf()
				DayWorkInfo(
					day = DayOfWeek.values()[scheduleEntity.day],
					periods = groups[scheduleEntity.day]?.map { it.mapToDomain() }
						?.toImmutableList()
						?: persistentListOf(),
					isDinnerInclude = scheduleEntity.isDinnerInclude,
					events = eventsList
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
	
	override suspend fun setDinner(dayOfWeek: Int, isDinnerInclude: Boolean) {
		scheduleDao.setDinner(dayOfWeek, isDinnerInclude)
	}
	
	
	override suspend fun addWorkEvent(dayOfWeek: Int, event: WorkEvent) {
		if (dayOfWeek == -1) {
			for (i in 0 until 6)
				scheduleDao.addEvent(
					i,
					event.name,
					event.isDinner,
					event.timeStart,
					event.timeEnd
				)
		} else {
			scheduleDao.addEvent(
				dayOfWeek,
				event.name,
				event.isDinner,
				event.timeStart,
				event.timeEnd
			)
		}
	}
	
	override suspend fun deleteWorkEvent(workEvent: WorkEvent) {
		scheduleDao.deleteEvent(workEvent.id)
	}
	
	override suspend fun deleteDinner(dayOfWeek: Int) {
		scheduleDao.deleteDinner(dayOfWeek)
	}
	
	override suspend fun setWorkEventTime(
		workEvent: WorkEvent, dayOfWeek: Int
	) {
		scheduleDao.changeEvent(
			workEvent.mapToEntity().copy(day = dayOfWeek)
		)
	}
}