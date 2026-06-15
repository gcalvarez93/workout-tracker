// Path: app/src/main/java/com/castrodev/workouttracker/features/profile/domain/entity/ProfileEntity.kt
package com.castrodev.workouttracker.features.profile.domain.entity

data class ProfileEntity(
    val id: String,
    val name: String,
    val email: String,
    val photoUrl: String,
    val language: String,
    val notifications: Boolean
)