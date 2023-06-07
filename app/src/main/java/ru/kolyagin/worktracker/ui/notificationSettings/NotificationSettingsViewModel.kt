package ru.kolyagin.worktracker.ui.notificationSettings

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import ru.kolyagin.worktracker.domain.models.Time
import ru.kolyagin.worktracker.domain.repositories.PreferenceRepository
import ru.kolyagin.worktracker.utils.base.BaseViewModel
import java.time.DayOfWeek
import javax.inject.Inject

@HiltViewModel
class NotificationSettingsViewModel @Inject constructor(
    private val preferenceRepository: PreferenceRepository
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
        salaryRates = persistentListOf()
    )
) {
    init {
        preferenceRepository.salary().subscribe { salaries ->
            updateState {
                it.copy(salaryRates = salaries.toPersistentList())
            }
        }
    }

    fun addSalary(forallDays: Boolean, day: Int, rate: Long) {
        launchViewModelScope {
            if (forallDays) {
                for (i in 0 until 7) preferenceRepository.addsalary(DayOfWeek.of(i + 1), rate)
            } else {
                preferenceRepository.addsalary(DayOfWeek.of(day + 1), rate)
            }
        }
    }

    fun setSalary(forallDays: Boolean, day: Int, rate: Long) {
        launchViewModelScope {
            if (forallDays) {
                for (i in 0 until 7) preferenceRepository.setSalary(DayOfWeek.of(i + 1), rate)
            } else {
                preferenceRepository.setSalary(DayOfWeek.of(day + 1), rate)
            }
        }
    }

    fun onAddSalary() {
        launchViewModelScope {
            sendEvent(NotificationSettingsEvent.AddSalary)
        }
    }

    fun onSetSalary(day: DayOfWeek) {
        launchViewModelScope {
            sendEvent(NotificationSettingsEvent.SetSalary(day))
        }
    }

    fun onDeleteSalary(id: Long) {
        launchViewModelScope {
            preferenceRepository.deleteSalary(id)
        }
    }

    fun onMorningNotificationEnableChange(enable: Boolean) {
        updateState {
            preferenceRepository.isMorningNotificationEnable = enable
            it.copy(isMorningNotificationEnable = enable)
        }
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
        trySendEvent(
            NotificationSettingsEvent.ShowTimePicker(
                currentState.morningOffset,
                ::onMorningOffsetPicked
            )
        )
    }

    fun onDinnerNotificationEnableChange(enable: Boolean) {
        updateState {
            preferenceRepository.isDinnerNotificationEnable = enable
            it.copy(isDinnerNotificationEnable = enable)
        }
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
    }

    fun onStartWorkOffsetClick() {
        trySendEvent(
            NotificationSettingsEvent.ShowTimePicker(
                currentState.startWorkOffset,
                ::onStartWorkOffsetPicked
            )
        )
    }

    fun onEndWorkNotificationEnableChange(enable: Boolean) {
        updateState {
            preferenceRepository.isEndWorkNotificationEnable = enable
            it.copy(isEndWorkNotificationEnable = enable)
        }
    }

    fun onEndWorkOffsetClick() {
        trySendEvent(
            NotificationSettingsEvent.ShowTimePicker(
                currentState.endWorkOffset,
                ::onEndWorkOffsetPicked
            )
        )
    }

    private fun onMorningStartTimePicked(time: Time) {
        updateState {
            val newRange = time..it.morningNotificationRange.endInclusive
            preferenceRepository.morningRange = newRange
            it.copy(
                morningNotificationRange = newRange
            )
        }
    }

    private fun onMorningEndTimePicked(time: Time) {
        updateState {
            val newRange = it.morningNotificationRange.start..time
            preferenceRepository.morningRange = newRange
            it.copy(
                morningNotificationRange = newRange
            )
        }
    }

    private fun onMorningOffsetPicked(time: Time) {
        updateState {
            preferenceRepository.morningNotificationOffset = time
            it.copy(morningOffset = time)
        }
    }

    private fun onDinnerPicked(time: Time) {
        updateState {
            preferenceRepository.dinnerTimeInNotWorkingTime = time
            it.copy(dinnerTime = time)
        }
    }

    private fun onStartWorkOffsetPicked(time: Time) {
        updateState {
            preferenceRepository.timeBeforeStartWork = time
            it.copy(startWorkOffset = time)
        }
    }

    private fun onEndWorkOffsetPicked(time: Time) {
        updateState {
            preferenceRepository.timeBeforeEndWork = time
            it.copy(endWorkOffset = time)
        }
    }

}