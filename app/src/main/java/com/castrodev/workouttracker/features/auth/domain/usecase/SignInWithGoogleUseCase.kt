// Path: app/src/main/java/com/castrodev/workouttracker/features/auth/domain/usecase/SignInWithGoogleUseCase.kt
package com.castrodev.workouttracker.features.auth.domain.usecase
import com.castrodev.workouttracker.features.auth.domain.repository.AuthRepository
class SignInWithGoogleUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(idToken: String) = repository.signInWithGoogle(idToken)
}