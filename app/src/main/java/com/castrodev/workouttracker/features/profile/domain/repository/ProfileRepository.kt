// Path: app/src/main/java/com/castrodev/workouttracker/features/profile/domain/repository/ProfileRepository.kt
package com.castrodev.workouttracker.features.profile.domain.repository

import com.castrodev.workouttracker.features.profile.domain.entity.ProfileEntity

interface ProfileRepository {
    suspend fun getProfile(): Result<ProfileEntity>
    suspend fun updateProfile(name: String, language: String, notifications: Boolean): Result<ProfileEntity>
}