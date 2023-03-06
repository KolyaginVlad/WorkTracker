package ru.kolyagin.worktracker.ui.notifications

import android.app.Activity
import android.app.Application
import android.os.Bundle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import ru.kolyagin.worktracker.utils.Constants
import javax.inject.Inject
import javax.inject.Named

class ActivityCallbacks @Inject constructor(
    @Named(Constants.NOTIFICATION_SCOPE) private val scope: CoroutineScope,
    private val notificationsManager: NotificationsManager
): Application.ActivityLifecycleCallbacks {
    
    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}

    override fun onActivityStarted(activity: Activity) {}

    override fun onActivityResumed(activity: Activity) {}

    override fun onActivityPaused(activity: Activity) {}

    override fun onActivityStopped(activity: Activity) {
        scope.launch {
            notificationsManager.rescheduleNotifications()
        }
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}

    override fun onActivityDestroyed(activity: Activity) {}
}