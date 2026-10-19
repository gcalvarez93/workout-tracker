// Path: app/src/main/java/com/castrodev/workouttracker/di/BindingsModule.kt
package com.castrodev.workouttracker.di

import com.castrodev.workouttracker.core.network.FirebaseTokenProvider
import com.castrodev.workouttracker.core.network.TokenProvider
import com.castrodev.workouttracker.features.analytics.data.repository.AnalyticsRepositoryImpl
import com.castrodev.workouttracker.features.analytics.domain.repository.AnalyticsRepository
import com.castrodev.workouttracker.features.auth.data.repository.AuthRepositoryImpl
import com.castrodev.workouttracker.features.auth.domain.repository.AuthRepository
import com.castrodev.workouttracker.features.bodyweight.data.repository.BodyweightRepositoryImpl
import com.castrodev.workouttracker.features.bodyweight.domain.repository.BodyweightRepository
import com.castrodev.workouttracker.features.profile.data.repository.ProfileRepositoryImpl
import com.castrodev.workouttracker.features.profile.domain.repository.ProfileRepository
import com.castrodev.workouttracker.features.routines.data.repository.RoutineRepositoryImpl
import com.castrodev.workouttracker.features.routines.domain.repository.RoutineRepository
import com.castrodev.workouttracker.features.sessions.data.repository.SessionRepositoryImpl
import com.castrodev.workouttracker.features.sessions.domain.repository.SessionRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/** Enlaza cada interfaz del dominio con su implementación de la capa data. */
@Module
@InstallIn(SingletonComponent::class)
abstract class BindingsModule {

    @Binds @Singleton
    abstract fun bindTokenProvider(impl: FirebaseTokenProvider): TokenProvider

    @Binds @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds @Singleton
    abstract fun bindRoutineRepository(impl: RoutineRepositoryImpl): RoutineRepository

    @Binds @Singleton
    abstract fun bindSessionRepository(impl: SessionRepositoryImpl): SessionRepository

    @Binds @Singleton
    abstract fun bindAnalyticsRepository(impl: AnalyticsRepositoryImpl): AnalyticsRepository

    @Binds @Singleton
    abstract fun bindBodyweightRepository(impl: BodyweightRepositoryImpl): BodyweightRepository

    @Binds @Singleton
    abstract fun bindProfileRepository(impl: ProfileRepositoryImpl): ProfileRepository
}