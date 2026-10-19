// Path: app/src/test/java/com/castrodev/workouttracker/fakes/FakeAuthRepository.kt
package com.castrodev.workouttracker.fakes

import com.castrodev.workouttracker.features.auth.domain.entity.UserEntity
import com.castrodev.workouttracker.features.auth.domain.repository.AuthRepository

val testUser = UserEntity(id = "u1", name = "Ana", email = "ana@test.com", photoUrl = "")

/** Repositorio de autenticación en memoria con resultados configurables. */
class FakeAuthRepository : AuthRepository {
    var signedInUser: UserEntity? = null
    var signInResult: Result<UserEntity> = Result.success(testUser)
    var signUpResult: Result<UserEntity> = Result.success(testUser)

    override suspend fun signIn(email: String, password: String): Result<UserEntity> {
        signInResult.onSuccess { signedInUser = it }
        return signInResult
    }

    override suspend fun signUp(email: String, password: String, name: String): Result<UserEntity> {
        signUpResult.onSuccess { signedInUser = it }
        return signUpResult
    }

    override suspend fun signInWithGoogle(idToken: String): Result<UserEntity> = signInResult

    override fun signOut() {
        signedInUser = null
    }

    override fun isAuthenticated(): Boolean = signedInUser != null

    override fun getCurrentUser(): UserEntity? = signedInUser
}