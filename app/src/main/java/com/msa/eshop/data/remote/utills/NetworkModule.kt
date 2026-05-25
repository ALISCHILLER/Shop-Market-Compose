//package com.msa.eshop.data.remote.utills
//
//import android.content.SharedPreferences
//import com.google.gson.Gson
//import com.google.gson.GsonBuilder
//import com.msa.eshop.utils.CompanionValues
//import dagger.Module
//import dagger.Provides
//import dagger.hilt.InstallIn
//import dagger.hilt.components.SingletonComponent
//import java.util.concurrent.TimeUnit
//import javax.inject.Singleton
//import okhttp3.Interceptor
//import okhttp3.OkHttpClient
//import okhttp3.Request
//import retrofit2.Retrofit
//import retrofit2.converter.gson.GsonConverterFactory
//import timber.log.Timber
//import com.msa.eshop.data.remote.api.ApiService
//
//@Module
//@InstallIn(SingletonComponent::class)
//object NetworkModule {
//
//    @Provides
//    @Singleton
//    fun provideGson(): Gson {
//        return GsonBuilder()
//            .setLenient()
//            .serializeNulls()
//            .create()
//    }
//
//    @Provides
//    @Singleton
//    fun provideAuthInterceptor(
//        sharedPreferences: SharedPreferences
//    ): Interceptor {
//        return Interceptor { chain ->
//            val originalRequest = chain.request()
//            val token = sharedPreferences
//                .getString(CompanionValues.TOKEN, null)
//                ?.trim()
//                .orEmpty()
//
//            val requestBuilder = originalRequest.newBuilder()
//                .header(Constant.ACCEPT_HEADER, Constant.JSON_MEDIA_TYPE)
//                .header(Constant.CONTENT_TYPE_HEADER, Constant.JSON_MEDIA_TYPE)
//
//            if (token.isNotBlank()) {
//                requestBuilder.header(
//                    Constant.AUTHORIZATION_HEADER,
//                    "${Constant.BEARER_PREFIX} $token"
//                )
//            }
//
//            chain.proceed(requestBuilder.build())
//        }
//    }
//
//    @Provides
//    @Singleton
//    fun provideNetworkLogInterceptor(): Interceptor {
//        return Interceptor { chain ->
//            val request = chain.request()
//            val startedAt = System.nanoTime()
//
//            Timber.tag(Constant.NETWORK_LOG_TAG).d(
//                "OkHttp request | method=${request.method} | url=${request.url.toString().substringBefore("?")}"
//            )
//
//            try {
//                val response = chain.proceed(request)
//                val durationMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startedAt)
//
//                Timber.tag(Constant.NETWORK_LOG_TAG).d(
//                    "OkHttp response | method=${request.method} | url=${request.url.toString().substringBefore("?")} | code=${response.code} | duration=${durationMs}ms"
//                )
//
//                response
//            } catch (throwable: Throwable) {
//                val durationMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startedAt)
//
//                Timber.tag(Constant.NETWORK_LOG_TAG).e(
//                    throwable,
//                    "OkHttp failure | method=${request.method} | url=${request.url.toString().substringBefore("?")} | duration=${durationMs}ms"
//                )
//
//                throw throwable
//            }
//        }
//    }
//
//    @Provides
//    @Singleton
//    fun provideOkHttpClient(
//        authInterceptor: Interceptor,
//        networkLogInterceptor: Interceptor
//    ): OkHttpClient {
//        return OkHttpClient.Builder()
//            .connectTimeout(Constant.CONNECT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
//            .readTimeout(Constant.READ_TIMEOUT_SECONDS, TimeUnit.SECONDS)
//            .writeTimeout(Constant.WRITE_TIMEOUT_SECONDS, TimeUnit.SECONDS)
//            .callTimeout(Constant.CALL_TIMEOUT_SECONDS, TimeUnit.SECONDS)
//            .retryOnConnectionFailure(true)
//            .addInterceptor(authInterceptor)
//            .addInterceptor(networkLogInterceptor)
//            .build()
//    }
//
//    @Provides
//    @Singleton
//    fun provideRetrofit(
//        okHttpClient: OkHttpClient,
//        gson: Gson
//    ): Retrofit {
//        return Retrofit.Builder()
//            .baseUrl(Constant.BASE_URL.ensureTrailingSlash())
//            .client(okHttpClient)
//            .addConverterFactory(GsonConverterFactory.create(gson))
//            .build()
//    }
//
//    @Provides
//    @Singleton
//    fun provideApiService(
//        retrofit: Retrofit
//    ): ApiService {
//        return retrofit.create(ApiService::class.java)
//    }
//
//    private fun String.ensureTrailingSlash(): String {
//        return if (endsWith("/")) this else "$this/"
//    }
//}