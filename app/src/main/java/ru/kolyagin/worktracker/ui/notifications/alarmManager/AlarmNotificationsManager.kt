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
import ru.kolyagin.worktracker.domain.models.Time
import ru.kolyagin.worktracker.domain.repositories.ScheduleRepository
import ru.kolyagin.worktracker.ui.notifications.NotificationsManager
import ru.kolyagin.worktracker.utils.Constants
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
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val resultIntent = Intent(context, NotificationReceiver::class.java)
            scheduleRepository.schedule().firstOrNull()?.forEach { dayWorkInfo ->
                dayWorkInfo.periods
                    .takeIf { it.isNotEmpty() }
                    ?.minOfOrNull { it.timeStart }
                    ?.let { workStart ->
                        val pendingIntent = PendingIntent.getBroadcast(
                            context,
                            Constants.MORNING_CONST + dayWorkInfo.day.ordinal,
                            resultIntent,
                            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                        )
                        val time = workStart - Time(
                            Constants.MORNING_HOURS_OFFSET,
                            Constants.MORNING_MINUTES_OFFSET
                        )
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                            if (alarmManager.canScheduleExactAlarms()) {
                                schedule(alarmManager, time, pendingIntent)
                            }
                        } else {
                            schedule(alarmManager, time, pendingIntent)
                        }
                    }
            }
        }
    }

    override fun rescheduleNotifications() {
        scheduleMorningNotification()
    }

    private fun schedule(
        alarmManager: AlarmManager,
        time: Time,
        pendingIntent: PendingIntent?
    ) {
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            calculateTriggerTime(time),
            pendingIntent
        )
    }

    private fun calculateTriggerTime(time: Time): Long {
        var notificationTime = LocalDate.now().atTime(time.hours, time.minutes, 0)

        val currentTime = LocalDateTime.now()
        if (currentTime.isAfter(notificationTime)) {
            notificationTime = notificationTime.plusDays(Constants.DAY_INTERVAL)
        }
        val offset = OffsetDateTime.now().offset
        return notificationTime.toInstant(offset).toEpochMilli()
    }
}