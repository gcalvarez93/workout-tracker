// Path: app/src/main/java/com/castrodev/workouttracker/features/profile/domain/usecase/UpdateProfileUseCase.kt
package com.castrodev.workouttracker.features.profile.domain.usecase
import com.castrodev.workouttracker.features.profile.domain.repository.ProfileRepository
class UpdateProfileUseCase(private val repository: ProfileRepository) {
    suspend operator fun invoke(name: String, language: String, notifications: Boolean) =
        repository.updateProfile(name, language, notifications)
}