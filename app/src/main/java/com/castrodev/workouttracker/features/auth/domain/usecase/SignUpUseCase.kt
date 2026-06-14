// Path: app/src/main/java/com/castrodev/workouttracker/features/auth/domain/usecase/SignUpUseCase.kt
package com.castrodev.workouttracker.features.auth.domain.usecase
import com.castrodev.workouttracker.features.auth.domain.repository.AuthRepository
class SignUpUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(email: String, password: String, name: String) = repository.signUp(email, password, name)
}