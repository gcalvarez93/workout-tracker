// Path: app/src/main/java/com/castrodev/workouttracker/features/auth/data/datasource/AuthRemoteDataSource.kt
package com.castrodev.workouttracker.features.auth.data.datasource

import com.castrodev.workouttracker.core.network.FirebaseTokenProvider
import com.castrodev.workouttracker.core.network.NetworkClient
import com.castrodev.workouttracker.features.auth.data.model.GoogleSignInRequest
import com.castrodev.workouttracker.features.auth.data.model.SignInRequest
import com.castrodev.workouttracker.features.auth.data.model.SignUpRequest
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await

class AuthRemoteDataSource {
    private val api = NetworkClient.buildRetrofit(FirebaseTokenProvider()).create(AuthApiService::class.java)
    private val firebaseAuth = FirebaseAuth.getInstance()

    suspend fun signIn(email: String, password: String) = runCatching {
        firebaseAuth.signInWithEmailAndPassword(email, password).await()
        api.signIn(SignInRequest(email, password))
    }

    suspend fun signUp(email: String, password: String, name: String) = runCatching {
        firebaseAuth.createUserWithEmailAndPassword(email, password).await()
        api.signUp(SignUpRequest(email, password, name))
    }

    suspend fun signInWithGoogle(idToken: String) = runCatching {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credential).await()
        api.signInWithGoogle(GoogleSignInRequest(idToken))
    }

    fun signOut() = firebaseAuth.signOut()
    fun isAuthenticated() = firebaseAuth.currentUser != null
    fun getCurrentUser() = firebaseAuth.currentUser
}