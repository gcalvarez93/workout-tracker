// Path: app/src/main/java/com/castrodev/workouttracker/features/auth/data/datasource/AuthApiService.kt
package com.castrodev.workouttracker.features.auth.data.datasource

import com.castrodev.workouttracker.features.auth.data.model.GoogleSignInRequest
import com.castrodev.workouttracker.features.auth.data.model.SignInRequest
import com.castrodev.workouttracker.features.auth.data.model.SignUpRequest
import com.castrodev.workouttracker.features.auth.data.model.UserModel
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {
    @POST("api/common/auth/login")
    suspend fun signIn(@Body request: SignInRequest): UserModel

    @POST("api/common/auth/register")
    suspend fun signUp(@Body request: SignUpRequest): UserModel

    @POST("api/common/auth/google")
    suspend fun signInWithGoogle(@Body request: GoogleSignInRequest): UserModel
}