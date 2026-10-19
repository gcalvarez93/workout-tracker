// Path: app/src/main/java/com/castrodev/workouttracker/features/profile/domain/usecase/GetProfileUseCase.kt
package com.castrodev.workouttracker.features.profile.domain.usecase

import com.castrodev.workouttracker.features.profile.domain.repository.ProfileRepository
import javax.inject.Inject

class GetProfileUseCase @Inject constructor(private val repository: ProfileRepository) {
    suspend operator fun invoke() = repository.getProfile()
}