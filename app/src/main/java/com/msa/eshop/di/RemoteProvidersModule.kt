package com.msa.eshop.di

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.msa.eshop.BuildConfig
import com.msa.eshop.data.remote.api.ApiService
import com.msa.eshop.utils.CompanionValues
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.util.concurrent.TimeUnit
import javax.inject.Singleton
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber

private const val BASE_URL = "http://10.252.112.98:8282/"

private const val SHARED_PREFS_NAME = "secret_shared_prefs"

private const val CONNECT_TIMEOUT_SECONDS = 60L
private const val READ_TIMEOUT_SECONDS = 60L
private const val WRITE_TIMEOUT_SECONDS = 60L
private const val CALL_TIMEOUT_SECONDS = 90L

private const val HEADER_AUTHORIZATION = "Authorization"
private const val HEADER_ACCEPT = "Accept"
private const val HEADER_CONTENT_TYPE = "Content-Type"

private const val JSON_MEDIA_TYPE = "application/json"
private const val BEARER_PREFIX = "Bearer"

@Module
@InstallIn(SingletonComponent::class)
object RemoteProvidersModule {

    @Provides
    @Singleton
    fun provideSharedPreferences(
        @ApplicationContext context: Context
    ): SharedPreferences {
        return context.applicationContext.getSharedPreferences(
            SHARED_PREFS_NAME,
            Context.MODE_PRIVATE
        )
    }

    @Provides
    @Singleton
    fun provideGson(): Gson {
        return GsonBuilder()
            .setLenient()
            .serializeNulls()
            .create()
    }

    @Provides
    @Singleton
    fun provideAuthInterceptor(
        sharedPreferences: SharedPreferences
    ): Interceptor {
        return Interceptor { chain ->
            val originalRequest = chain.request()

            val token = sharedPreferences
                .getString(CompanionValues.TOKEN, null)
                .orEmpty()
                .trim()

            val requestBuilder = originalRequest
                .newBuilder()
                .header(HEADER_ACCEPT, JSON_MEDIA_TYPE)

            /*
             * برای requestهایی که body دارند Content-Type ست می‌شود.
             * برای GETها لازم نیست.
             */
            if (originalRequest.body != null) {
                requestBuilder.header(HEADER_CONTENT_TYPE, JSON_MEDIA_TYPE)
            }

            /*
             * توکن خالی نباید به شکل "Bearer " ارسال شود.
             */
            if (token.isNotBlank()) {
                requestBuilder.header(
                    HEADER_AUTHORIZATION,
                    "$BEARER_PREFIX $token"
                )
            }

            chain.proceed(requestBuilder.build())
        }
    }

    @Provides
    @Singleton
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor { message ->
            Timber.tag("OkHttp").d(message)
        }.apply {
            /*
             * BODY ممکن است token، رمز، شماره تماس یا اطلاعات سفارش را لاگ کند.
             * BASIC برای debug کافی و امن‌تر است.
             */
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BASIC
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        authInterceptor: Interceptor,
        httpLoggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(CONNECT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(READ_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .writeTimeout(WRITE_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .callTimeout(CALL_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .addInterceptor(authInterceptor)
            .addInterceptor(httpLoggingInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        gson: Gson
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL.ensureTrailingSlash())
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    @Provides
    @Singleton
    fun provideApiService(
        retrofit: Retrofit
    ): ApiService {
        return retrofit.create(ApiService::class.java)
    }

    private fun String.ensureTrailingSlash(): String {
        return if (endsWith("/")) this else "$this/"
    }
}