// Path: app/src/main/java/com/castrodev/workouttracker/features/auth/domain/repository/AuthRepository.kt
package com.castrodev.workouttracker.features.auth.domain.repository

import com.castrodev.workouttracker.features.auth.domain.entity.UserEntity

interface AuthRepository {
    suspend fun signIn(email: String, password: String): Result<UserEntity>
    suspend fun signUp(email: String, password: String, name: String): Result<UserEntity>
    suspend fun signInWithGoogle(idToken: String): Result<UserEntity>
    fun signOut()
    fun isAuthenticated(): Boolean
    fun getCurrentUser(): UserEntity?
}