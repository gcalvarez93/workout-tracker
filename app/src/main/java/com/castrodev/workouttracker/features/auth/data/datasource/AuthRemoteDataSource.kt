// Path: app/src/main/java/com/castrodev/workouttracker/features/auth/data/datasource/AuthRemoteDataSource.kt
package com.castrodev.workouttracker.features.auth.data.datasource

import com.castrodev.workouttracker.features.auth.data.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await

class AuthRemoteDataSource {
    private val firebaseAuth = FirebaseAuth.getInstance()

    suspend fun signIn(email: String, password: String) = runCatching {
        val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
        val user = result.user!!
        UserModel(id = user.uid, name = user.displayName ?: "", email = user.email ?: "", photoUrl = user.photoUrl?.toString() ?: "")
    }

    suspend fun signUp(email: String, password: String, name: String) = runCatching {
        val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
        val user = result.user!!
        UserModel(id = user.uid, name = name, email = user.email ?: "", photoUrl = "")
    }

    suspend fun signInWithGoogle(idToken: String) = runCatching {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        val result = firebaseAuth.signInWithCredential(credential).await()
        val user = result.user!!
        UserModel(id = user.uid, name = user.displayName ?: "", email = user.email ?: "", photoUrl = user.photoUrl?.toString() ?: "")
    }

    fun signOut() = firebaseAuth.signOut()
    fun isAuthenticated() = firebaseAuth.currentUser != null
    fun getCurrentUser() = firebaseAuth.currentUser
}