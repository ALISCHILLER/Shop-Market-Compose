package com.msa.eshop.di

import android.content.Context
import com.msa.eshop.utils.map.location.PiLocationManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    /*
     * اگر جایی Context بدون qualifier inject شده باشد، این provider کمک می‌کند.
     * در کدهای جدیدتر بهتر است مستقیم @ApplicationContext استفاده شود.
     */
    @Provides
    @Singleton
    fun provideApplicationContext(
        @ApplicationContext context: Context
    ): Context {
        return context
    }

    @Provides
    @Singleton
    fun providePiLocationManager(
        @ApplicationContext context: Context
    ): PiLocationManager {
        return PiLocationManager(context)
    }
}