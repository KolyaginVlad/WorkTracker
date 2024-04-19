package ru.kolyagin.worktracker.data.di

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.google.firebase.analytics.FirebaseAnalytics
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.plus
import ru.kolyagin.worktracker.data.database.AppDatabase
import ru.kolyagin.worktracker.data.database.RoomCallback
import ru.kolyagin.worktracker.data.repositories.PreferenceRepositoryImpl
import ru.kolyagin.worktracker.data.repositories.ScheduleRepositoryImpl
import ru.kolyagin.worktracker.data.repositories.WorkStatisticRepositoryImpl
import ru.kolyagin.worktracker.domain.repositories.PreferenceRepository
import ru.kolyagin.worktracker.domain.repositories.ScheduleRepository
import ru.kolyagin.worktracker.domain.repositories.WorkStatisticRepository
import ru.kolyagin.worktracker.utils.Constants
import javax.inject.Named
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
interface DataModule {

    @Singleton
    @Binds
    fun bindScheduleRepository(
        impl: ScheduleRepositoryImpl,
    ): ScheduleRepository

    @Singleton
    @Binds
    fun bindPreferencesRepository(
        impl: PreferenceRepositoryImpl,
    ): PreferenceRepository

    @Singleton
    @Binds
    fun bindWorkStatisticRepository(
        impl: WorkStatisticRepositoryImpl,
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
    fun provideDatabase(
        @ApplicationContext context: Context,
        roomCallback: RoomCallback,
    ): AppDatabase {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE WorkStatistic RENAME TO TMP")
                database.execSQL(
                    """
                    CREATE TABLE WorkStatistic
                    (
                        id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
                        date INTEGER NOT NULL,
                        workTime INTEGER NOT NULL,
                        plannedPauseTime INTEGER NOT NULL,
                        unplannedPauseTime INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
                database.execSQL("DROP TABLE TMP")
            }
        }

        return Room.databaseBuilder(
            context, AppDatabase::class.java, "WorkTracker"
        )
            .addCallback(roomCallback)
            .addMigrations(MIGRATION_1_2)
            .build()
    }

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

    @Provides
    @Singleton
    @Named(Constants.DATA_SCOPE)
    fun provideScope(handler: CoroutineExceptionHandler) =
        CoroutineScope(Dispatchers.IO) + SupervisorJob() + handler
}

