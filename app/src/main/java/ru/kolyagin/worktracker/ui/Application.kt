package ru.kolyagin.worktracker.ui

import dagger.hilt.android.HiltAndroidApp
import ru.kolyagin.worktracker.ui.notifications.ActivityCallbacks
import javax.inject.Inject

@HiltAndroidApp
class Application : android.app.Application() {

    @Inject
    lateinit var activityCallbacks: ActivityCallbacks

    override fun onCreate() {
        super.onCreate()
        registerActivityLifecycleCallbacks(activityCallbacks)
    }
}
