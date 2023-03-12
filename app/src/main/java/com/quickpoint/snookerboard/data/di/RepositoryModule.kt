package com.quickpoint.snookerboard.data.di

import com.quickpoint.snookerboard.data.repository.DataStoreRepositoryImpl
import com.quickpoint.snookerboard.data.repository.GameRepositoryImpl
import com.quickpoint.snookerboard.domain.repository.DataStoreRepository
import com.quickpoint.snookerboard.domain.repository.GameRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindGameRepository(gameRepositoryImpl: GameRepositoryImpl): GameRepository

    @Binds
    @Singleton
    abstract fun bindDataStoreRepository(dataStoreRepositoryImpl: DataStoreRepositoryImpl): DataStoreRepository
}