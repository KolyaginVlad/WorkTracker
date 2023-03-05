package ru.kolyagin.worktracker.utils.di

import ru.kolyagin.worktracker.utils.log.Logger
import ru.kolyagin.worktracker.utils.log.LoggerImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
interface UtilsModule {

    @Binds
    fun bindLogger(loggerImpl: LoggerImpl): Logger
}