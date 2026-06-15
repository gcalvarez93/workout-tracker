// Path: app/src/main/java/com/castrodev/workouttracker/features/profile/data/datasource/ProfileApiService.kt
package com.castrodev.workouttracker.features.profile.data.datasource

import com.castrodev.workouttracker.features.profile.data.model.ProfileModel
import com.castrodev.workouttracker.features.profile.data.model.UpdateProfileRequest
import retrofit2.http.*

interface ProfileApiService {
    @GET("api/common/users/me")
    suspend fun getProfile(): ProfileModel

    @PUT("api/common/users/me")
    suspend fun updateProfile(@Body request: UpdateProfileRequest): ProfileModel
}