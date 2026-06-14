// Path: app/src/main/java/com/castrodev/workouttracker/features/auth/domain/usecase/SignInUseCase.kt
package com.castrodev.workouttracker.features.auth.domain.usecase
import com.castrodev.workouttracker.features.auth.domain.repository.AuthRepository
class SignInUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(email: String, password: String) = repository.signIn(email, password)
}