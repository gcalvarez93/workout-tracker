// Path: app/src/main/java/com/castrodev/workouttracker/features/auth/data/repository/AuthRepositoryImpl.kt
package com.castrodev.workouttracker.features.auth.data.repository

import com.castrodev.workouttracker.core.util.safeCall
import com.castrodev.workouttracker.features.auth.data.datasource.AuthRemoteDataSource
import com.castrodev.workouttracker.features.auth.domain.entity.UserEntity
import com.castrodev.workouttracker.features.auth.domain.repository.AuthRepository
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val dataSource: AuthRemoteDataSource
) : AuthRepository {
    override suspend fun signIn(email: String, password: String): Result<UserEntity> =
        safeCall("AuthRepositoryImpl.signIn") { dataSource.signIn(email, password).toEntity() }

    override suspend fun signUp(email: String, password: String, name: String): Result<UserEntity> =
        safeCall("AuthRepositoryImpl.signUp") { dataSource.signUp(email, password, name).toEntity() }

    override suspend fun signInWithGoogle(idToken: String): Result<UserEntity> =
        safeCall("AuthRepositoryImpl.signInWithGoogle") { dataSource.signInWithGoogle(idToken).toEntity() }

    override fun signOut() = dataSource.signOut()

    override fun isAuthenticated(): Boolean = dataSource.isAuthenticated()

    override fun getCurrentUser(): UserEntity? = dataSource.getCurrentUser()?.toEntity()
}