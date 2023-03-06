package ru.kolyagin.worktracker.ui.notifications.module

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.plus
import ru.kolyagin.worktracker.ui.notifications.Notification
import ru.kolyagin.worktracker.ui.notifications.NotificationImpl
import ru.kolyagin.worktracker.ui.notifications.NotificationsManager
import ru.kolyagin.worktracker.ui.notifications.alarmManager.AlarmNotificationsManager
import ru.kolyagin.worktracker.utils.Constants
import ru.kolyagin.worktracker.utils.log.Logger
import javax.inject.Named

@Module
@InstallIn(SingletonComponent::class)
interface NotificationModule {

    @Binds
    fun bindNotification(
        impl: NotificationImpl
    ): Notification

    @Binds
    fun bindNotificationManager(
        impl: AlarmNotificationsManager
    ): NotificationsManager

}

@Module
@InstallIn(SingletonComponent::class)
class NotificationModuleProvider {

    @Provides
    @Named(Constants.NOTIFICATION_SCOPE)
    fun provideScope(handler: CoroutineExceptionHandler) =
        CoroutineScope(Dispatchers.IO) + SupervisorJob() + handler

    @Provides
    fun provideDefaultExceptionHandler(log: Logger) =
        CoroutineExceptionHandler { _, throwable ->
            log.error(throwable)
        }
}