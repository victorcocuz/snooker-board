package com.quickpoint.snookerboard.data.di

import android.content.Context
import com.quickpoint.snookerboard.data.DataStore
import com.quickpoint.snookerboard.data.database.SnookerDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun provideContext(@ApplicationContext context: Context) = context

    @Provides
    @Singleton
    fun provideDatabase(context: Context) = SnookerDatabase.getDatabase(context)

    @Provides
    @Singleton
    fun provideDataStore(context: Context) = DataStore(context)

}