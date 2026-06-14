// Path: app/src/main/java/com/castrodev/workouttracker/features/auth/data/model/AuthModel.kt
package com.castrodev.workouttracker.features.auth.data.model

import com.castrodev.workouttracker.features.auth.domain.entity.UserEntity

data class UserModel(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val photoUrl: String = ""
) {
    fun toEntity() = UserEntity(id = id, name = name, email = email, photoUrl = photoUrl)
}

data class SignInRequest(val email: String, val password: String)
data class SignUpRequest(val email: String, val password: String, val name: String)
data class GoogleSignInRequest(val idToken: String)