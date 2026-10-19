// Path: app/src/test/java/com/castrodev/workouttracker/core/network/AuthInterceptorTest.kt
package com.castrodev.workouttracker.core.network

import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class AuthInterceptorTest {

    private val chain = mockk<Interceptor.Chain>()
    private val response = mockk<Response>(relaxed = true)
    private val sentRequest = slot<Request>()

    private fun interceptWith(token: String?): Request {
        every { chain.request() } returns Request.Builder().url("https://api.test/items").build()
        every { chain.proceed(capture(sentRequest)) } returns response

        val provider = object : TokenProvider {
            override fun getToken(): String? = token
        }
        AuthInterceptor(provider).intercept(chain)
        return sentRequest.captured
    }

    @Test
    fun `con token anade la cabecera bearer`() {
        val request = interceptWith("abc123")

        assertEquals("Bearer abc123", request.header("Authorization"))
        assertEquals("application/json", request.header("Content-Type"))
    }

    @Test
    fun `sin token envia la peticion sin cabecera de autorizacion`() {
        val request = interceptWith(null)

        assertNull(request.header("Authorization"))
        assertEquals("application/json", request.header("Content-Type"))
    }
}