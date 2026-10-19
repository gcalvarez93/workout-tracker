// Path: app/src/main/java/com/castrodev/workouttracker/core/network/AuthInterceptor.kt
package com.castrodev.workouttracker.core.network

import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val tokenProvider: TokenProvider) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        // OkHttp ejecuta los interceptores en su propio hilo de red, no en el principal,
        // así que bloquear aquí para obtener el token es seguro.
        val token = tokenProvider.getToken()
        val request = chain.request().newBuilder().apply {
            token?.let { addHeader("Authorization", "Bearer $it") }
            addHeader("Content-Type", "application/json")
        }.build()
        return chain.proceed(request)
    }
}