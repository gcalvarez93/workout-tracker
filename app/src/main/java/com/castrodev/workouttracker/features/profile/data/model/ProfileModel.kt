// Path: app/src/main/java/com/castrodev/workouttracker/features/profile/data/model/ProfileModel.kt
package com.castrodev.workouttracker.features.profile.data.model

import com.castrodev.workouttracker.features.profile.domain.entity.ProfileEntity

data class ProfileModel(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val photoUrl: String = "",
    val language: String = "es",
    val notifications: Boolean = true
) {
    fun toEntity() = ProfileEntity(id, name, email, photoUrl, language, notifications)
}

data class UpdateProfileRequest(
    val name: String,
    val language: String,
    val notifications: Boolean,
    val photoUrl: String = ""
)