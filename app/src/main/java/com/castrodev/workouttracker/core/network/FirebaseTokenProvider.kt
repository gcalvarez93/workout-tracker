// Path: app/src/main/java/com/castrodev/workouttracker/core/network/FirebaseTokenProvider.kt
package com.castrodev.workouttracker.core.network

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

class FirebaseTokenProvider {
    suspend fun getToken(): String? {
        return try {
            FirebaseAuth.getInstance().currentUser?.getIdToken(false)?.await()?.token
        } catch (e: Exception) { null }
    }
}