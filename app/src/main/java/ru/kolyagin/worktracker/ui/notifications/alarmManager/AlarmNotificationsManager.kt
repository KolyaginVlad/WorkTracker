package ru.kolyagin.worktracker.ui.notifications.alarmManager

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import ru.kolyagin.worktracker.R
import ru.kolyagin.worktracker.domain.models.Time
import ru.kolyagin.worktracker.domain.repositories.ScheduleRepository
import ru.kolyagin.worktracker.ui.notifications.NotificationsManager
import ru.kolyagin.worktracker.utils.Constants
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.OffsetDateTime
import javax.inject.Inject
import javax.inject.Named


class AlarmNotificationsManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val scheduleRepository: ScheduleRepository,
    @Named(Constants.NOTIFICATION_SCOPE) private val scope: CoroutineScope
) : NotificationsManager {

    override fun scheduleMorningNotification() {
        scope.launch {
            scheduleRepository.schedule().firstOrNull()?.forEach { dayWorkInfo ->
                dayWorkInfo.periods
                    .takeIf { it.isNotEmpty() }
                    ?.minOfOrNull { it.timeStart }
                    ?.takeIf {
                        it < Time(
                            Constants.MORNING_HOURS_LIMIT,
                            Constants.MORNING_MINUTES_LIMIT
                        ) && it > Time(
                            Constants.MORNING_HOURS_OFFSET,
                            Constants.MORNING_MINUTES_OFFSET
                        )
                    }
                    ?.let { workStart ->
                        scheduleAlarm(
                            dayWorkInfo.day.ordinal,
                            Constants.MORNING_CONST,
                            workStart - Time(
                                Constants.MORNING_HOURS_OFFSET,
                                Constants.MORNING_MINUTES_OFFSET
                            )
                        ) {
                            putExtra(Constants.DESCRIPTION, R.string.morning_start_work_description)
                        }
                    }
            }
        }
    }

    override fun schedulePreWorkNotification() {
        scope.launch {
            scheduleRepository.schedule().firstOrNull()?.forEach { dayWorkInfo ->
                dayWorkInfo.periods
                    .takeIf { it.isNotEmpty() }
                    ?.map { it.timeStart }
                    ?.forEach {
                        it.takeIf {
                            it >= Time(
                                Constants.MORNING_HOURS_LIMIT,
                                Constants.MORNING_MINUTES_LIMIT
                            )
                        }?.let { workStart ->
                            scheduleAlarm(
                                dayWorkInfo.day.ordinal,
                                Constants.PRE_WORK_CONST,
                                workStart - Time(
                                    Constants.PRE_WORK_HOURS_OFFSET,
                                    Constants.PRE_WORK_MINUTES_OFFSET
                                )
                            ) {
                                putExtra(Constants.DESCRIPTION, R.string.start_work_description)
                            }
                        }
                    }

            }
        }
    }

    override fun scheduleEveningNotification() {
        scope.launch {
            scheduleRepository.schedule().firstOrNull()?.forEach { dayWorkInfo ->
                dayWorkInfo.periods
                    .takeIf { it.isNotEmpty() }
                    ?.map { it.timeEnd }
                    ?.forEach {
                        it.takeIf {
                            it >= Time(
                                Constants.EVENING_HOURS_LIMIT,
                                Constants.EVENING_MINUTES_LIMIT
                            )
                        }?.let { workEnd ->
                            scheduleAlarm(
                                dayWorkInfo.day.ordinal,
                                Constants.EVENING_CONST,
                                workEnd - Time(
                                    Constants.FIN_WORK_HOURS_OFFSET,
                                    Constants.FIN_WORK_MINUTES_OFFSET
                                )
                            ) {
                                putExtra(Constants.DESCRIPTION, R.string.evening_finish_work_description)
                            }
                        }
                    }

            }
        }
    }

    override fun scheduleFinWorkNotification() {
        scope.launch {
            scheduleRepository.schedule().firstOrNull()?.forEach { dayWorkInfo ->
                dayWorkInfo.periods
                    .takeIf { it.isNotEmpty() }
                    ?.map { it.timeEnd }
                    ?.forEach {
                        it.takeIf {
                            it < Time(
                                Constants.EVENING_HOURS_LIMIT,
                                Constants.EVENING_MINUTES_LIMIT
                            )
                        }?.let { workEnd ->
                            scheduleAlarm(
                                dayWorkInfo.day.ordinal,
                                Constants.FIN_WORK_CONST,
                                workEnd - Time(
                                    Constants.FIN_WORK_HOURS_OFFSET,
                                    Constants.FIN_WORK_MINUTES_OFFSET
                                )
                            ) {
                                putExtra(Constants.DESCRIPTION, R.string.finish_work_description)
                            }
                        }
                    }

            }
        }
    }

    override fun scheduleDinnerNotification() {
        scope.launch {
            val time = Time(
                Constants.DINNER_HOURS,
                Constants.DINNER_MINUTES
            )
            scheduleRepository.schedule().firstOrNull()?.forEach { dayWorkInfo ->
                if (!dayWorkInfo.isDinnerInclude && dayWorkInfo.periods.all {
                        time !in it.timeStart..it.timeEnd
                    }
                ) {
                    scheduleAlarm(
                        dayWorkInfo.day.ordinal,
                        Constants.NOT_WORK_DINNER_CONST,
                        time
                    ) {
                        putExtra(Constants.DESCRIPTION, R.string.dinner_description)
                    }
                }
            }
        }
    }

    override fun rescheduleNotifications() {
        scheduleMorningNotification()
        scheduleDinnerNotification()
        schedulePreWorkNotification()
        scheduleEveningNotification()
        scheduleFinWorkNotification()
    }

    private fun scheduleAlarm(
        day: Int,
        const: Int,
        workStart: Time,
        modifyIntent: Intent.() -> Unit = {}
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val resultIntent = Intent(context, NotificationReceiver::class.java).apply(modifyIntent)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            const + day,
            resultIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms()) {
                schedule(alarmManager, workStart, pendingIntent, day)
            }
        } else {
            schedule(alarmManager, workStart, pendingIntent, day)
        }
    }

    private fun schedule(
        alarmManager: AlarmManager,
        time: Time,
        pendingIntent: PendingIntent?,
        day: Int
    ) {
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            calculateTriggerTime(time, day),
            pendingIntent
        )
    }

    private fun calculateTriggerTime(time: Time, day: Int): Long {
        var notificationTime = LocalDate.now().atTime(time.hours, time.minutes, 0)
        val currentDayOfWeek = notificationTime.dayOfWeek.ordinal
        val dif = day - currentDayOfWeek

        notificationTime =
            if (dif > 0 || (dif == 0 && LocalDateTime.now().isBefore(notificationTime))) {
                notificationTime.plusDays(dif.toLong())
            } else {
                notificationTime.plusDays((DayOfWeek.values().size - currentDayOfWeek + day).toLong())
            }
        val offset = OffsetDateTime.now().offset
        return notificationTime.toInstant(offset).toEpochMilli()
    }
}