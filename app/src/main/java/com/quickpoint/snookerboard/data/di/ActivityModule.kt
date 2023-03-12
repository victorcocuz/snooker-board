package com.quickpoint.snookerboard.data.di

import android.app.Activity
import android.content.Context
import com.quickpoint.snookerboard.core.billing.PurchaseHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ActivityContext

@Module
@InstallIn(ActivityComponent::class)
object PurchaseModule {
    @Provides
    fun providePurchaseHelper(@ActivityContext context: Context): PurchaseHelper {
        return PurchaseHelper(context as Activity)
    }
}