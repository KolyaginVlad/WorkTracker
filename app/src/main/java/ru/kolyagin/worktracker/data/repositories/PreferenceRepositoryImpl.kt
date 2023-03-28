package ru.kolyagin.worktracker.data.repositories

import android.content.SharedPreferences
import androidx.core.content.edit
import ru.kolyagin.worktracker.domain.models.TimeWithSeconds
import ru.kolyagin.worktracker.domain.models.WorkState
import ru.kolyagin.worktracker.domain.repositories.PreferenceRepository
import ru.kolyagin.worktracker.utils.toSeconds
import java.time.LocalTime
import javax.inject.Inject

class PreferenceRepositoryImpl @Inject constructor(
    private val sharedPreferences: SharedPreferences
) : PreferenceRepository {
    override var currentWorkState: WorkState
        get() = WorkState.values()[sharedPreferences.getInt(CURRENT_WORK_STATE, 0)]
        set(value) {
            sharedPreferences.edit {
                putInt(CURRENT_WORK_STATE, value.ordinal)
                putLong(
                    CURRENT_WORK_STATE_SET_SECONDS,
                    LocalTime.now().toSeconds()
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


    companion object {
        const val CURRENT_WORK_STATE = "currentWorkState"
        const val CURRENT_WORK_STATE_SET_SECONDS = "current_work_state_set_seconds"
    }
}