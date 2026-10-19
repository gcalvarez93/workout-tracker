// Path: app/src/main/java/com/castrodev/workouttracker/features/profile/data/datasource/ProfileRemoteDataSource.kt
package com.castrodev.workouttracker.features.profile.data.datasource

import com.castrodev.workouttracker.features.profile.data.model.UpdateProfileRequest
import javax.inject.Inject

class ProfileRemoteDataSource @Inject constructor(
    private val api: ProfileApiService
) {
    suspend fun getProfile() = api.getProfile()

    suspend fun updateProfile(name: String, language: String, notifications: Boolean) =
        api.updateProfile(UpdateProfileRequest(name, language, notifications))
}