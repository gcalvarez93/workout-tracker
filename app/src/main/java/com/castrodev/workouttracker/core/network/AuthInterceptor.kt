// Path: app/src/main/java/com/castrodev/workouttracker/core/network/AuthInterceptor.kt
package com.castrodev.workouttracker.core.network

import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val tokenProvider: FirebaseTokenProvider) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = runBlocking { tokenProvider.getToken() }
        val request = chain.request().newBuilder().apply {
            token?.let { addHeader("Authorization", "Bearer $it") }
            addHeader("Content-Type", "application/json")
        }.build()
        return chain.proceed(request)
    }
}