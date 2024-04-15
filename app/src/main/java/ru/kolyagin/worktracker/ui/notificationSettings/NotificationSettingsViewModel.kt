package ru.kolyagin.worktracker.ui.notificationSettings

import dagger.hilt.android.lifecycle.HiltViewModel
import ru.kolyagin.worktracker.domain.models.Time
import ru.kolyagin.worktracker.domain.repositories.PreferenceRepository
import ru.kolyagin.worktracker.ui.notifications.NotificationsManager
import ru.kolyagin.worktracker.ui.notifications.alarmManager.AlarmNotificationsManager
import ru.kolyagin.worktracker.utils.base.BaseViewModel
import ru.kolyagin.worktracker.utils.log.Logger
import javax.inject.Inject

@HiltViewModel
class NotificationSettingsViewModel @Inject constructor(
    logger: Logger,
    private val preferenceRepository: PreferenceRepository,
    private val notificationsManager: NotificationsManager
) : BaseViewModel<NotificationSettingsScreenState, NotificationSettingsEvent>(
    NotificationSettingsScreenState(
        isMorningNotificationEnable = preferenceRepository.isMorningNotificationEnable,
        isDinnerNotificationEnable = preferenceRepository.isDinnerNotificationEnable,
        isStartWorkNotificationEnable = preferenceRepository.isStartWorkNotificationEnable,
        isEndWorkNotificationEnable = preferenceRepository.isEndWorkNotificationEnable,
        morningNotificationRange = preferenceRepository.morningRange,
        morningOffset = preferenceRepository.morningNotificationOffset,
        dinnerTime = preferenceRepository.dinnerTimeInNotWorkingTime,
        startWorkOffset = preferenceRepository.timeBeforeStartWork,
        endWorkOffset = preferenceRepository.timeBeforeEndWork,
    ), logger
) {

    fun onMorningNotificationEnableChange(enable: Boolean) {
        updateState {
            preferenceRepository.isMorningNotificationEnable = enable
            it.copy(isMorningNotificationEnable = enable)
        }
        notificationsManager.scheduleMorningNotification()
    }

    fun onMorningStartTimeClick() {
        trySendEvent(
            NotificationSettingsEvent.ShowTimePicker(
                currentState.morningNotificationRange.start,
                ::onMorningStartTimePicked
            )
        )
    }

    fun onMorningEndTimeClick() {
        trySendEvent(
            NotificationSettingsEvent.ShowTimePicker(
                currentState.morningNotificationRange.endInclusive,
                ::onMorningEndTimePicked
            )
        )
    }

    fun onMorningOffsetClick() {
        updateState {
            it.copy(morningOffsetDialogVisible = true)
        }
    }

    fun onMorningOffsetClose() {
        updateState {
            it.copy(morningOffsetDialogVisible = false)
        }
    }

    fun onDinnerNotificationEnableChange(enable: Boolean) {
        updateState {
            preferenceRepository.isDinnerNotificationEnable = enable
            it.copy(isDinnerNotificationEnable = enable)
        }
        notificationsManager.scheduleDinnerNotification()
    }

    fun onDinnerTimeClick() {
        trySendEvent(
            NotificationSettingsEvent.ShowTimePicker(
                currentState.dinnerTime,
                ::onDinnerPicked
            )
        )
    }

    fun onStartWorkNotificationEnableChange(enable: Boolean) {
        updateState {
            preferenceRepository.isStartWorkNotificationEnable = enable
            it.copy(isStartWorkNotificationEnable = enable)
        }
        notificationsManager.schedulePreWorkNotification()
    }

    fun onStartWorkOffsetClick() {
        updateState {
            it.copy(startWorkOffsetDialogVisible = true)
        }
    }
    fun onStartWorkOffsetClose() {
        updateState {
            it.copy(startWorkOffsetDialogVisible = false)
        }
    }

    fun onEndWorkNotificationEnableChange(enable: Boolean) {
        updateState {
            preferenceRepository.isEndWorkNotificationEnable = enable
            it.copy(isEndWorkNotificationEnable = enable)
        }
        notificationsManager.scheduleEveningNotification()
        notificationsManager.scheduleFinWorkNotification()
    }

    fun onEndWorkOffsetClick() {
        updateState {
            it.copy(endWorkOffsetDialogVisible = true)
        }
    }

    fun onEndWorkOffsetClose() {
        updateState {
            it.copy(endWorkOffsetDialogVisible = false)
        }
    }

    fun onMorningOffsetPicked(time: Time) {
        updateState {
            preferenceRepository.morningNotificationOffset = time
            it.copy(morningOffset = time)
        }
        notificationsManager.scheduleMorningNotification()
        onMorningOffsetClose()
    }

    fun onStartWorkOffsetPicked(time: Time) {
        updateState {
            preferenceRepository.timeBeforeStartWork = time
            it.copy(startWorkOffset = time)
        }
        notificationsManager.schedulePreWorkNotification()
        onStartWorkOffsetClose()
    }

    fun onEndWorkOffsetPicked(time: Time) {
        updateState {
            preferenceRepository.timeBeforeEndWork = time
            it.copy(endWorkOffset = time)
        }
        notificationsManager.scheduleFinWorkNotification()
        notificationsManager.scheduleEveningNotification()
        onEndWorkOffsetClose()
    }

    private fun onMorningStartTimePicked(time: Time) {
        updateState {
            val newRange = time..it.morningNotificationRange.endInclusive
            preferenceRepository.morningRange = newRange
            it.copy(
                morningNotificationRange = newRange
            )
        }
        notificationsManager.scheduleMorningNotification()
    }

    private fun onMorningEndTimePicked(time: Time) {
        updateState {
            val newRange = it.morningNotificationRange.start..time
            preferenceRepository.morningRange = newRange
            it.copy(
                morningNotificationRange = newRange
            )
        }
        notificationsManager.scheduleMorningNotification()
    }

    private fun onDinnerPicked(time: Time) {
        updateState {
            preferenceRepository.dinnerTimeInNotWorkingTime = time
            it.copy(dinnerTime = time)
        }
        notificationsManager.scheduleDinnerNotification()
    }

}