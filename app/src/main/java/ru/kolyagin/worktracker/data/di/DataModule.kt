package ru.kolyagin.worktracker.data.di

import android.content.Context
import android.content.SharedPreferences
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
import ru.kolyagin.worktracker.data.repositories.PreferenceRepositoryImpl
import ru.kolyagin.worktracker.data.repositories.ScheduleRepositoryImpl
import ru.kolyagin.worktracker.domain.repositories.PreferenceRepository
import ru.kolyagin.worktracker.domain.repositories.ScheduleRepository

@InstallIn(SingletonComponent::class)
@Module
interface DataModule {

    @Binds
    fun bindScheduleRepository(
        impl: ScheduleRepositoryImpl
    ): ScheduleRepository

    @Binds
    fun bindPreferencesRepository(
        impl: PreferenceRepositoryImpl
    ): PreferenceRepository
}

@InstallIn(SingletonComponent::class)
@Module
class DataProvidesModule {

    @Provides
    fun provideFirebaseAnalytics(@ApplicationContext context: Context): FirebaseAnalytics {
        return FirebaseAnalytics.getInstance(context)
    }

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

    @Provides
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences =
        context.getSharedPreferences("WorkTracker", Context.MODE_PRIVATE)
}

