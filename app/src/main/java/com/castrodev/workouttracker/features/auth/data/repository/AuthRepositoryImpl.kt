// Path: app/src/main/java/com/castrodev/workouttracker/features/auth/data/repository/AuthRepositoryImpl.kt
package com.castrodev.workouttracker.features.auth.data.repository

import com.castrodev.workouttracker.features.auth.data.datasource.AuthRemoteDataSource
import com.castrodev.workouttracker.features.auth.domain.entity.UserEntity
import com.castrodev.workouttracker.features.auth.domain.repository.AuthRepository

class AuthRepositoryImpl(
    private val dataSource: AuthRemoteDataSource = AuthRemoteDataSource()
) : AuthRepository {
    override suspend fun signIn(email: String, password: String): Result<UserEntity> =
        dataSource.signIn(email, password).map { it.toEntity() }

    override suspend fun signUp(email: String, password: String, name: String): Result<UserEntity> =
        dataSource.signUp(email, password, name).map { it.toEntity() }

    override suspend fun signInWithGoogle(idToken: String): Result<UserEntity> =
        dataSource.signInWithGoogle(idToken).map { it.toEntity() }

    override fun signOut() = dataSource.signOut()
    override fun isAuthenticated() = dataSource.isAuthenticated()
    override fun getCurrentUser(): UserEntity? {
        val user = dataSource.getCurrentUser() ?: return null
        return UserEntity(id = user.uid, name = user.displayName ?: "", email = user.email ?: "", photoUrl = user.photoUrl?.toString() ?: "")
    }
}