// Path: app/src/main/java/com/castrodev/workouttracker/features/auth/domain/entity/UserEntity.kt
package com.castrodev.workouttracker.features.auth.domain.entity

data class UserEntity(
    val id: String,
    val name: String,
    val email: String,
    val photoUrl: String
)