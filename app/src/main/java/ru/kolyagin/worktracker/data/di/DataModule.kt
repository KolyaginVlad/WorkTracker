package ru.kolyagin.worktracker.data.di

import android.content.Context
import androidx.room.Room
import com.google.firebase.analytics.FirebaseAnalytics
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ru.kolyagin.worktracker.data.database.AppDatabase
import ru.kolyagin.worktracker.data.database.RoomCallback
import ru.kolyagin.worktracker.data.repositories.ScheduleRepositoryImpl
import ru.kolyagin.worktracker.domain.repositories.ScheduleRepository

@InstallIn(SingletonComponent::class)
@Module
interface DataModule {

    @Binds
    fun bindScheduleRepository(
        impl: ScheduleRepositoryImpl
    ): ScheduleRepository
}

@Module
@InstallIn(SingletonComponent::class)
class AnalyticsModule {

    @Provides
    fun provideFirebaseAnalytics(@ApplicationContext context: Context): FirebaseAnalytics {
        return FirebaseAnalytics.getInstance(context)
    }
}

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {

    @Provides
    fun provideDatabase(@ApplicationContext context: Context, roomCallback: RoomCallback) =
        Room.databaseBuilder(
            context, AppDatabase::class.java, "WorkTracker"
        )
            .addCallback(roomCallback)
            .build()

    @Provides
    fun providesScheduleDao(database: AppDatabase) =
        database.getScheduleDao()
}
