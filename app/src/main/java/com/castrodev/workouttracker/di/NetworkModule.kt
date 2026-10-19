// Path: app/src/main/java/com/castrodev/workouttracker/di/NetworkModule.kt
package com.castrodev.workouttracker.di

import com.castrodev.workouttracker.BuildConfig
import com.castrodev.workouttracker.core.network.ApiConfig
import com.castrodev.workouttracker.core.network.AuthInterceptor
import com.castrodev.workouttracker.core.network.TokenProvider
import com.castrodev.workouttracker.features.analytics.data.datasource.AnalyticsApiService
import com.castrodev.workouttracker.features.bodyweight.data.datasource.BodyweightApiService
import com.castrodev.workouttracker.features.profile.data.datasource.ProfileApiService
import com.castrodev.workouttracker.features.routines.data.datasource.RoutineApiService
import com.castrodev.workouttracker.features.sessions.data.datasource.SessionApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(tokenProvider: TokenProvider): OkHttpClient {
        // El cuerpo de las peticiones solo se registra en debug, y nunca el token.
        val logging = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
            redactHeader("Authorization")
        }
        return OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(tokenProvider))
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(ApiConfig.BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun provideRoutineApiService(retrofit: Retrofit): RoutineApiService =
        retrofit.create(RoutineApiService::class.java)

    @Provides
    @Singleton
    fun provideSessionApiService(retrofit: Retrofit): SessionApiService =
        retrofit.create(SessionApiService::class.java)

    @Provides
    @Singleton
    fun provideAnalyticsApiService(retrofit: Retrofit): AnalyticsApiService =
        retrofit.create(AnalyticsApiService::class.java)

    @Provides
    @Singleton
    fun provideBodyweightApiService(retrofit: Retrofit): BodyweightApiService =
        retrofit.create(BodyweightApiService::class.java)

    @Provides
    @Singleton
    fun provideProfileApiService(retrofit: Retrofit): ProfileApiService =
        retrofit.create(ProfileApiService::class.java)
}