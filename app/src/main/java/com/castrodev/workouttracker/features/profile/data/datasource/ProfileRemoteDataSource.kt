// Path: app/src/main/java/com/castrodev/workouttracker/features/profile/data/datasource/ProfileRemoteDataSource.kt
package com.castrodev.workouttracker.features.profile.data.datasource

import com.castrodev.workouttracker.core.network.FirebaseTokenProvider
import com.castrodev.workouttracker.core.network.NetworkClient
import com.castrodev.workouttracker.features.profile.data.model.UpdateProfileRequest

class ProfileRemoteDataSource {
    private val api = NetworkClient.buildRetrofit(FirebaseTokenProvider()).create(ProfileApiService::class.java)

    suspend fun getProfile() = api.getProfile()
    suspend fun updateProfile(name: String, language: String, notifications: Boolean) =
        api.updateProfile(UpdateProfileRequest(name, language, notifications))
}