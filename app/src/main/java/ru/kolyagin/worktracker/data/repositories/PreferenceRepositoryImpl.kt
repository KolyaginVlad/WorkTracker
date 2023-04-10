package ru.kolyagin.worktracker.data.repositories

import android.content.SharedPreferences
import androidx.core.content.edit
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.kolyagin.worktracker.data.database.dao.SalaryRateDao
import ru.kolyagin.worktracker.data.database.entities.mapToDomain
import ru.kolyagin.worktracker.domain.models.DaySalaryRate
import ru.kolyagin.worktracker.domain.models.Time
import ru.kolyagin.worktracker.domain.models.Time.Companion.toMinutes
import ru.kolyagin.worktracker.domain.models.TimeWithSeconds
import ru.kolyagin.worktracker.domain.models.WorkState
import ru.kolyagin.worktracker.domain.repositories.PreferenceRepository
import ru.kolyagin.worktracker.utils.Constants
import ru.kolyagin.worktracker.utils.toSeconds
import java.time.DayOfWeek
import java.time.LocalTime
import javax.inject.Inject

class PreferenceRepositoryImpl @Inject constructor(
    private val sharedPreferences: SharedPreferences,
    private val salaryRateDao: SalaryRateDao
) : PreferenceRepository {
    override fun salary(): Flow<ImmutableList<DaySalaryRate>> = salaryRateDao.getSalaryRate()
        .map { salaryRateEntities ->
            salaryRateEntities.map { it.mapToDomain() }.toPersistentList()
        }


    override suspend fun addsalary(
        day: DayOfWeek,
        rate: Long
    ) {
        salaryRateDao.addSalaryRate(day.ordinal, rate)
    }

    override suspend fun deleteSalary(
        id: Long,
    ) {
        salaryRateDao.deleteSalaryRate(id)
    }

    override suspend fun setSalary(
        day: DayOfWeek, rate: Long
    ) {
        salaryRateDao.setSalaryRate(day.ordinal, rate)
    }

    override var currentWorkState: WorkState
        get() = WorkState.values()[sharedPreferences.getInt(CURRENT_WORK_STATE, 0)]
        set(value) {
            sharedPreferences.edit {
                val now = LocalTime.now().toSeconds()
                putInt(CURRENT_WORK_STATE, value.ordinal)
                putLong(
                    CURRENT_WORK_STATE_SET_SECONDS,
                    now
                )
            }
        }
    override val timeOfCurrentStateSet: TimeWithSeconds
        get() = TimeWithSeconds.fromSeconds(
            sharedPreferences.getLong(
                CURRENT_WORK_STATE_SET_SECONDS,
                0
            )
        )
    override var isDinnerEnableToday: Boolean
        get() = sharedPreferences.getBoolean(IS_DINNER_ENABLE_TODAY, true)
        set(value) {
            sharedPreferences.edit {
                putBoolean(IS_DINNER_ENABLE_TODAY, value)
            }
        }

    override var isMorningNotificationEnable: Boolean
        get() = sharedPreferences.getBoolean(IS_MORNING_NOTIFICATION_ENABLE, true)
        set(value) {
            sharedPreferences.edit {
                putBoolean(IS_MORNING_NOTIFICATION_ENABLE, value)
            }
        }

    override var morningNotificationOffset: Time
        get() = Time.fromMinutes(
            sharedPreferences.getInt(
                MORNING_NOTIFICATION_OFFSET,
                Constants.MORNING_OFFSET
            )
        )
        set(value) {
            sharedPreferences.edit {
                putInt(MORNING_NOTIFICATION_OFFSET, value.toMinutes())
            }
        }

    override var morningRange: ClosedRange<Time>
        get() = morningRangeStart..morningRangeEnd
        set(value) {
            morningRangeStart = value.start
            morningRangeEnd = value.endInclusive
        }

    override var isDinnerNotificationEnable: Boolean
        get() = sharedPreferences.getBoolean(IS_DINNER_NOTIFICATION_ENABLE, true)
        set(value) {
            sharedPreferences.edit {
                putBoolean(IS_DINNER_NOTIFICATION_ENABLE, value)
            }
        }

    override var dinnerTimeInNotWorkingTime: Time
        get() = Time.fromMinutes(
            sharedPreferences.getInt(
                DINNER_TIME_IN_NOT_WORKING_TIME,
                Constants.DINNER_TIME
            )
        )
        set(value) {
            sharedPreferences.edit {
                putInt(DINNER_TIME_IN_NOT_WORKING_TIME, value.toMinutes())
            }
        }

    override var isStartWorkNotificationEnable: Boolean
        get() = sharedPreferences.getBoolean(IS_START_WORK_NOTIFICATION_ENABLE, true)
        set(value) {
            sharedPreferences.edit {
                putBoolean(IS_START_WORK_NOTIFICATION_ENABLE, value)
            }
        }

    override var timeBeforeStartWork: Time
        get() = Time.fromMinutes(
            sharedPreferences.getInt(
                TIME_BEFORE_START_WORK,
                Constants.WORK_START_TIME
            )
        )
        set(value) {
            sharedPreferences.edit {
                putInt(TIME_BEFORE_START_WORK, value.toMinutes())
            }
        }

    override var isEndWorkNotificationEnable: Boolean
        get() = sharedPreferences.getBoolean(IS_END_WORK_NOTIFICATION_ENABLE, true)
        set(value) {
            sharedPreferences.edit {
                putBoolean(IS_END_WORK_NOTIFICATION_ENABLE, value)
            }
        }


    override var timeBeforeEndWork: Time
        get() = Time.fromMinutes(
            sharedPreferences.getInt(
                TIME_BEFORE_END_WORK,
                Constants.WORK_END_TIME
            )
        )
        set(value) {
            sharedPreferences.edit {
                putInt(TIME_BEFORE_END_WORK, value.toMinutes())
            }
        }


    private var morningRangeStart: Time
        get() = Time.fromMinutes(
            sharedPreferences.getInt(
                MORNING_RANGE_START,
                Constants.MORNING_START
            )
        )
        set(value) {
            sharedPreferences.edit {
                putInt(MORNING_RANGE_START, value.toMinutes())
            }
        }

    private var morningRangeEnd: Time
        get() = Time.fromMinutes(
            sharedPreferences.getInt(
                MORNING_RANGE_END,
                Constants.MORNING_END
            )
        )
        set(value) {
            sharedPreferences.edit {
                putInt(MORNING_RANGE_END, value.toMinutes())
            }
        }


    companion object {
        const val CURRENT_WORK_STATE = "currentWorkState"
        const val CURRENT_WORK_STATE_SET_SECONDS = "current_work_state_set_seconds"
        const val IS_DINNER_ENABLE_TODAY = "is_dinner_enable_today"
        const val IS_MORNING_NOTIFICATION_ENABLE = "is_morning_notification_enable"
        const val MORNING_NOTIFICATION_OFFSET = "morning_notification_offset"
        const val MORNING_RANGE_START = "morning_range_start"
        const val MORNING_RANGE_END = "morning_range_end"
        const val IS_DINNER_NOTIFICATION_ENABLE = "is_dinner_notification_enable"
        const val DINNER_TIME_IN_NOT_WORKING_TIME = "dinner_time_in_not_working_time"
        const val IS_START_WORK_NOTIFICATION_ENABLE = "is_start_work_notification_enable"
        const val TIME_BEFORE_START_WORK = "time_before_start_work"
        const val TIME_BEFORE_END_WORK = "time_before_end_work"
        const val IS_END_WORK_NOTIFICATION_ENABLE = "is_end_work_notification_enable"
    }
}