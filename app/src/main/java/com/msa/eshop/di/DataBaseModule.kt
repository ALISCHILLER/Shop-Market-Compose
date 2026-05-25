package com.msa.eshop.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.msa.eshop.data.local.AppDatabase
import com.msa.eshop.data.local.dao.OrderAddressDao
import com.msa.eshop.data.local.dao.OrderDao
import com.msa.eshop.data.local.dao.PaymentMethodDao
import com.msa.eshop.data.local.dao.ProductDao
import com.msa.eshop.data.local.dao.ProductGroupDao
import com.msa.eshop.data.local.dao.UserDao
import com.msa.eshop.utils.CompanionValues
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataBaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            CompanionValues.DATABASE_NAME
        )
            /*
             * بهتر برای performance و همزمانی read/write.
             */
            .setJournalMode(RoomDatabase.JournalMode.WRITE_AHEAD_LOGGING)

            /*
             * عمداً fallbackToDestructiveMigration نگذاشتم.
             * برای production بهتر است Migration واقعی بنویسی، نه پاک کردن دیتابیس کاربر.
             */
            .build()
    }

    @Provides
    @Singleton
    fun provideUserDao(
        appDatabase: AppDatabase
    ): UserDao {
        return appDatabase.userDao()
    }

    @Provides
    @Singleton
    fun provideProductDao(
        appDatabase: AppDatabase
    ): ProductDao {
        return appDatabase.productDao()
    }

    @Provides
    @Singleton
    fun provideProductGroupDao(
        appDatabase: AppDatabase
    ): ProductGroupDao {
        return appDatabase.productGroupDao()
    }

    @Provides
    @Singleton
    fun provideOrderDao(
        appDatabase: AppDatabase
    ): OrderDao {
        return appDatabase.orderDao()
    }

    @Provides
    @Singleton
    fun providePaymentMethodDao(
        appDatabase: AppDatabase
    ): PaymentMethodDao {
        return appDatabase.paymentMethodDao()
    }

    @Provides
    @Singleton
    fun provideOrderAddressDao(
        appDatabase: AppDatabase
    ): OrderAddressDao {
        return appDatabase.orderAddressDao()
    }
}