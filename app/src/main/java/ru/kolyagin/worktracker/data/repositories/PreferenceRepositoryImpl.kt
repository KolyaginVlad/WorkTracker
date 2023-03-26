package ru.kolyagin.worktracker.data.repositories

import android.content.SharedPreferences
import androidx.core.content.edit
import ru.kolyagin.worktracker.domain.models.WorkState
import ru.kolyagin.worktracker.domain.repositories.PreferenceRepository
import javax.inject.Inject

class PreferenceRepositoryImpl @Inject constructor(
    private val sharedPreferences: SharedPreferences
) : PreferenceRepository {
    override var currentWorkState: WorkState
        get() = WorkState.values()[sharedPreferences.getInt(CURRENT_WORK_STATE, 0)]
        set(value) {
            sharedPreferences.edit {
                putInt(CURRENT_WORK_STATE, value.ordinal)
            }
        }


    companion object {
        const val CURRENT_WORK_STATE = "currentWorkState"
    }
}