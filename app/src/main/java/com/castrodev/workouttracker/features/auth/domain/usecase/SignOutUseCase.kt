// Path: app/src/main/java/com/castrodev/workouttracker/features/auth/domain/usecase/SignOutUseCase.kt
package com.castrodev.workouttracker.features.auth.domain.usecase

import com.castrodev.workouttracker.features.auth.domain.repository.AuthRepository
import javax.inject.Inject

class SignOutUseCase @Inject constructor(private val repository: AuthRepository) {
    operator fun invoke() = repository.signOut()
}