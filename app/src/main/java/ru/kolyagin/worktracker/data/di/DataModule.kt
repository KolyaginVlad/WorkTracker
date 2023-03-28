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
import ru.kolyagin.worktracker.data.repositories.WorkStatisticRepositoryImpl
import ru.kolyagin.worktracker.domain.repositories.PreferenceRepository
import ru.kolyagin.worktracker.domain.repositories.ScheduleRepository
import ru.kolyagin.worktracker.domain.repositories.WorkStatisticRepository
import javax.inject.Singleton

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

    @Binds
    fun bindWorkStatisticRepository(
        impl: WorkStatisticRepositoryImpl
    ): WorkStatisticRepository
}

@InstallIn(SingletonComponent::class)
@Module
class DataProvidesModule {

    @Provides
    @Singleton
    fun provideFirebaseAnalytics(@ApplicationContext context: Context): FirebaseAnalytics {
        return FirebaseAnalytics.getInstance(context)
    }

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context, roomCallback: RoomCallback) =
        Room.databaseBuilder(
            context, AppDatabase::class.java, "WorkTracker"
        )
            .addCallback(roomCallback)
            .build()

    @Provides
    @Singleton
    fun providesScheduleDao(database: AppDatabase) =
        database.getScheduleDao()

    @Provides
    @Singleton
    fun providesWorkStatisticDao(database: AppDatabase) =
        database.getWorkStatisticDao()

    @Provides
    @Singleton
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences =
        context.getSharedPreferences("WorkTracker", Context.MODE_PRIVATE)
}

