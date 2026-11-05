// Path: app/src/test/java/com/castrodev/workouttracker/core/util/SafeCallTest.kt
package com.castrodev.workouttracker.core.util

import com.castrodev.workouttracker.core.error.AppError
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class SafeCallTest {

    @Test
    fun `devuelve el valor cuando todo va bien`() = runTest {
        val result = safeCall("test") { 42 }

        assertEquals(42, result.getOrNull())
    }

    @Test
    fun `un fallo de red llega como AppError Network con la causa original`() = runTest {
        val cause = IOException("sin red")

        val failure = safeCall<Int>("test") { throw cause }.exceptionOrNull()

        assertTrue(failure is AppError.Network)
        assertSame(cause, failure?.cause)
    }

    @Test
    fun `un error HTTP 500 llega como AppError Server`() = runTest {
        val http = HttpException(Response.error<Any>(500, "".toResponseBody(null)))

        val failure = safeCall<Int>("test") { throw http }.exceptionOrNull()

        assertTrue(failure is AppError.Server)
    }

    @Test
    fun `un AppError lanzado por el bloque no se envuelve`() = runTest {
        val error = AppError.NotFound()

        val failure = safeCall<Int>("test") { throw error }.exceptionOrNull()

        assertSame(error, failure)
    }

    @Test
    fun `la cancelacion se propaga y no se convierte en fallo`() = runTest {
        try {
            safeCall<Int>("test") { throw CancellationException("cancelada") }
            fail("Se esperaba que la cancelacion se propagara")
        } catch (e: CancellationException) {
            assertEquals("cancelada", e.message)
        }
    }
}