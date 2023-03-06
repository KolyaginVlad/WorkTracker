package ru.kolyagin.worktracker.ui

import android.os.Build
import androidx.annotation.RequiresApi
import dagger.hilt.android.HiltAndroidApp
import ru.kolyagin.worktracker.ui.notifications.ActivityCallbacks
import javax.inject.Inject

@HiltAndroidApp
class Application : android.app.Application() {

    @Inject
    lateinit var activityCallbacks: ActivityCallbacks
    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate() {
        super.onCreate()
        registerActivityLifecycleCallbacks(activityCallbacks)
    }
}
