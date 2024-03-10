package com.hocel.demodriver.di

import android.app.Application
import android.content.Context
import com.google.android.gms.location.LocationServices
import com.hocel.demodriver.common.RingtoneManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class AppModule {
    @Singleton
    @Provides
    fun provideCoroutineScope(): CoroutineScope {
        return CoroutineScope(Dispatchers.Default)
    }
    @Singleton
    @Provides
    fun provideRingtoneManager(application: Application, scope: CoroutineScope): RingtoneManager {
        return RingtoneManager(application, scope)
    }

    @Provides
    @Singleton
    fun provideFusedLocationProviderClient(
        @ApplicationContext app: Context
    ) = LocationServices.getFusedLocationProviderClient(app)

}