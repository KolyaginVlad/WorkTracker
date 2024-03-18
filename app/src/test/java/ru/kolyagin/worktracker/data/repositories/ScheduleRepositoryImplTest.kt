package ru.kolyagin.worktracker.data.repositories

import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before

import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import ru.kolyagin.worktracker.data.database.dao.ScheduleDao
import ru.kolyagin.worktracker.data.database.entities.DayScheduleEntity
import ru.kolyagin.worktracker.data.database.entities.WorkEventEntity
import ru.kolyagin.worktracker.data.database.entities.WorkPeriodEntity
import ru.kolyagin.worktracker.data.database.entities.mapToDomain
import ru.kolyagin.worktracker.domain.models.DayWorkInfo
import ru.kolyagin.worktracker.domain.models.Time
import ru.kolyagin.worktracker.domain.models.WorkPeriod
import java.time.DayOfWeek

class ScheduleRepositoryImplTest {

    private val dao = mock<ScheduleDao>()

    private val repository = ScheduleRepositoryImpl(
        dao
    )

    private val day = DayOfWeek.FRIDAY

    private val workEventEntities = listOf(
        WorkEventEntity(
            id = 1,
            name = "Name",
            day = day.ordinal,
            isDinner = false,
            timeEnd = Time.fromMinutes(10),
            timeStart = Time.fromMinutes(1)
        )
    )

    private val dayScheduleEntities = listOf(
        DayScheduleEntity(
            day = day.ordinal,
            isDinnerInclude = false
        )
    )

    private val workPeriod = WorkPeriod(
        id = 1,
        timeEnd = Time.fromMinutes(10),
        timeStart = Time.fromMinutes(1),
    )

    @Before
    fun before() {
        whenever(dao.getWorkEvents()).thenReturn(
            flow {
                emit(workEventEntities)
            }
        )

        whenever(dao.getDaySchedule()).thenReturn(
            flow {
                emit(dayScheduleEntities)
            }
        )

        whenever(dao.getWorkPeriods()).thenReturn(
            flow { emit(emptyList()) }
        )
    }

    @Test
    fun schedule() = runTest {
        val result = repository.schedule()
        verify(dao, times(1)).getDaySchedule()
        verify(dao, times(1)).getWorkPeriods()
        verify(dao, times(1)).getWorkEvents()
        assertEquals(
            DayWorkInfo(
                day = day,
                isDinnerInclude = false,
                events = workEventEntities.map { it.mapToDomain() }.toPersistentList(),
                periods = persistentListOf()
            ),
            result.first().first()
        )
    }

    @Test
    fun addPeriod() = runTest {
        val day = DayOfWeek.FRIDAY
        repository.addPeriod(day)
        verify(dao, times(1)).addPeriod(day.ordinal)
    }

    @Test
    fun deletePeriod() = runTest {
        val id = 1L
        repository.deletePeriod(WorkPeriod(id, Time.fromMinutes(1), Time.fromMinutes(2)))
        verify(dao, times(1)).deletePeriod(id)
    }

    @Test
    fun setTime() = runTest {
        repository.setTime(
            workPeriod,
            day
        )
        verify(dao, times(1)).setTime(
            WorkPeriodEntity(
                workPeriod.id,
                workPeriod.timeStart,
                workPeriod.timeEnd,
                day.ordinal
            )
        )
    }

    @Test
    fun setDinner() = runTest {
        repository.setDinner(day.ordinal, true)
        verify(dao, times(1)).setDinner(day.ordinal, true)
    }

    @Test
    fun addWorkEvent() = runTest {
        val event = workEventEntities.first().mapToDomain()
        repository.addWorkEvent(day.ordinal, event)
        verify(dao, times(1)).addEvent(
            day.ordinal,
            event.name,
            event.isDinner,
            event.timeStart,
            event.timeEnd
        )
    }

    @Test
    fun deleteWorkEvent() = runTest {
        val event = workEventEntities.first().mapToDomain()
        repository.deleteWorkEvent(event)
        verify(dao, times(1)).deleteEvent(event.id)
    }

    @Test
    fun deleteDinner() = runTest {
        repository.deleteDinner(day.ordinal)

        verify(dao, times(1)).deleteDinner(day.ordinal)
    }

    @Test
    fun setWorkEventTime() = runTest {
        val event = workEventEntities.first().mapToDomain()
        repository.setWorkEventTime(event, day.ordinal)
        verify(dao, times(1)).changeEvent(
            workEventEntities.first().copy(day = day.ordinal)
        )
    }
}