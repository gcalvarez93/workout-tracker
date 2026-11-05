// Path: app/src/test/java/com/castrodev/workouttracker/core/network/TokenAuthenticatorTest.kt
package com.castrodev.workouttracker.core.network

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withTimeoutOrNull
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.IOException

class TokenAuthenticatorTest {

    private val events = SessionEvents()
    private var refreshCalls = 0

    private fun provider(refresh: () -> String?) = object : TokenProvider {
        override fun getToken(): String? = "old"
        override fun refreshToken(): String? {
            refreshCalls++
            return refresh()
        }
    }

    private fun authenticator(refresh: () -> String?) = TokenAuthenticator(provider(refresh), events)

    private fun unauthorized(prior: Response? = null): Response =
        Response.Builder()
            .request(
                Request.Builder()
                    .url("https://api.test/items")
                    .header("Authorization", "Bearer old")
                    .build()
            )
            .protocol(Protocol.HTTP_1_1)
            .code(401)
            .message("Unauthorized")
            .priorResponse(prior)
            .build()

    /** Consume un aviso si lo hay. En `runTest` el tiempo es virtual, así que no espera de verdad. */
    private suspend fun sessionExpiredWasNotified(): Boolean =
        withTimeoutOrNull(1) { events.expired.first() } != null

    // --- Primer 401 ------------------------------------------------------------------------

    @Test
    fun `el primer 401 reintenta con el token renovado`() = runTest {
        val retry = authenticator { "new" }.authenticate(null, unauthorized())

        assertEquals("Bearer new", retry?.header("Authorization"))
        assertFalse(sessionExpiredWasNotified())
    }

    @Test
    fun `el reintento conserva la URL de la peticion original`() = runTest {
        val retry = authenticator { "new" }.authenticate(null, unauthorized())

        assertEquals("https://api.test/items", retry?.url?.toString())
    }

    // --- Sesion no recuperable -------------------------------------------------------------

    @Test
    fun `si el token renovado tambien recibe 401 se rinde y avisa`() = runTest {
        val retried = unauthorized(prior = unauthorized())

        val retry = authenticator { "new" }.authenticate(null, retried)

        assertNull(retry)
        assertEquals("No debe pedir otro token", 0, refreshCalls)
        assertTrue(sessionExpiredWasNotified())
    }

    @Test
    fun `sin sesion no reintenta y avisa`() = runTest {
        val retry = authenticator { null }.authenticate(null, unauthorized())

        assertNull(retry)
        assertTrue(sessionExpiredWasNotified())
    }

    // --- Falta de red ----------------------------------------------------------------------

    @Test
    fun `si la renovacion falla por red propaga el error y no cierra la sesion`() = runTest {
        val authenticator = authenticator { throw IOException("sin red") }

        assertThrows(IOException::class.java) { authenticator.authenticate(null, unauthorized()) }
        assertFalse(sessionExpiredWasNotified())
    }

    // --- SessionEvents ---------------------------------------------------------------------

    @Test
    fun `varios avisos seguidos se reducen a uno`() = runTest {
        events.notifyExpired()
        events.notifyExpired()
        events.notifyExpired()

        assertTrue(sessionExpiredWasNotified())
        assertFalse(sessionExpiredWasNotified())
    }

    @Test
    fun `un aviso emitido antes de que alguien escuche no se pierde`() = runTest {
        events.notifyExpired()

        assertTrue(sessionExpiredWasNotified())
    }
}