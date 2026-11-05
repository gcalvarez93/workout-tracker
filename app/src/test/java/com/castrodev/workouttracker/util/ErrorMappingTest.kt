// Path: app/src/test/java/com/castrodev/workouttracker/core/util/ErrorMappingTest.kt
package com.castrodev.workouttracker.core.util

import com.castrodev.workouttracker.R
import com.castrodev.workouttracker.core.error.AppError
import com.castrodev.workouttracker.core.ui.messageRes
import com.castrodev.workouttracker.core.ui.toMessageRes
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import io.mockk.every
import io.mockk.mockk
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class ErrorMappingTest {

    private fun http(code: Int) = HttpException(Response.error<Any>(code, "".toResponseBody(null)))

    // Las excepciones de Firebase validan sus argumentos con utilidades de Android que en un test
    // JVM son stubs y lanzan RuntimeException. Por eso se simulan en lugar de construirlas.
    private fun invalidCredentials(code: String) =
        mockk<FirebaseAuthInvalidCredentialsException>(relaxed = true) { every { errorCode } returns code }

    // --- HTTP ------------------------------------------------------------------------------

    @Test
    fun `401 es sesion no valida`() {
        assertTrue(http(401).toAppError() is AppError.Unauthorized)
    }

    @Test
    fun `403 es falta de permiso y no sesion no valida`() {
        assertTrue(http(403).toAppError() is AppError.Forbidden)
    }

    @Test
    fun `404 es no encontrado`() {
        assertTrue(http(404).toAppError() is AppError.NotFound)
    }

    @Test
    fun `429 son demasiados intentos`() {
        assertTrue(http(429).toAppError() is AppError.TooManyRequests)
    }

    @Test
    fun `los 5xx son error de servidor`() {
        assertTrue(http(500).toAppError() is AppError.Server)
        assertTrue(http(503).toAppError() is AppError.Server)
    }

    @Test
    fun `otro codigo HTTP es desconocido`() {
        assertTrue(http(418).toAppError() is AppError.Unknown)
    }

    // --- Red -------------------------------------------------------------------------------

    @Test
    fun `los fallos de IO son de red`() {
        assertTrue(IOException().toAppError() is AppError.Network)
        assertTrue(UnknownHostException().toAppError() is AppError.Network)
    }

    @Test
    fun `un timeout no se cuenta como falta de red`() {
        // SocketTimeoutException es un IOException: el orden del mapeo importa.
        assertTrue(SocketTimeoutException().toAppError() is AppError.Timeout)
    }

    @Test
    fun `FirebaseNetworkException es de red`() {
        assertTrue(mockk<FirebaseNetworkException>(relaxed = true).toAppError() is AppError.Network)
    }

    // --- Firebase Auth ---------------------------------------------------------------------

    @Test
    fun `credenciales incorrectas`() {
        val error = invalidCredentials("ERROR_INVALID_CREDENTIAL").toAppError()

        assertTrue(error is AppError.InvalidCredentials)
    }

    @Test
    fun `usuario inexistente o deshabilitado se muestra como credenciales incorrectas`() {
        val error = mockk<FirebaseAuthInvalidUserException>(relaxed = true).toAppError()

        assertTrue(error is AppError.InvalidCredentials)
    }

    @Test
    fun `correo mal formado`() {
        val error = invalidCredentials("ERROR_INVALID_EMAIL").toAppError()

        assertTrue(error is AppError.InvalidEmail)
    }

    @Test
    fun `correo ya registrado`() {
        val error = mockk<FirebaseAuthUserCollisionException>(relaxed = true).toAppError()

        assertTrue(error is AppError.EmailAlreadyInUse)
    }

    @Test
    fun `contrasena debil no se confunde con credenciales incorrectas`() {
        // FirebaseAuthWeakPasswordException hereda de FirebaseAuthInvalidCredentialsException.
        val error = mockk<FirebaseAuthWeakPasswordException>(relaxed = true).toAppError()

        assertTrue(error is AppError.WeakPassword)
    }

    @Test
    fun `demasiados intentos en Firebase`() {
        assertTrue(mockk<FirebaseTooManyRequestsException>(relaxed = true).toAppError() is AppError.TooManyRequests)
    }

    // --- Resto -----------------------------------------------------------------------------

    @Test
    fun `una excepcion desconocida es Unknown y conserva la causa`() {
        val original = IllegalStateException("algo raro")

        val error = original.toAppError()

        assertTrue(error is AppError.Unknown)
        assertSame(original, error.cause)
    }

    @Test
    fun `un AppError ya clasificado no se vuelve a envolver`() {
        val error = AppError.NotFound()

        assertSame(error, error.toAppError())
    }

    // --- Textos ----------------------------------------------------------------------------

    @Test
    fun `cada error tiene su texto localizado`() {
        assertEquals(R.string.error_network, AppError.Network().messageRes())
        assertEquals(R.string.error_server, AppError.Server().messageRes())
        assertEquals(R.string.error_timeout, AppError.Timeout().messageRes())
        assertEquals(R.string.error_unauthorized, AppError.Unauthorized().messageRes())
        assertEquals(R.string.error_forbidden, AppError.Forbidden().messageRes())
        assertEquals(R.string.error_not_found, AppError.NotFound().messageRes())
        assertEquals(R.string.error_too_many_requests, AppError.TooManyRequests().messageRes())
        assertEquals(R.string.error_invalid_credentials, AppError.InvalidCredentials().messageRes())
        assertEquals(R.string.error_invalid_email, AppError.InvalidEmail().messageRes())
        assertEquals(R.string.error_email_in_use, AppError.EmailAlreadyInUse().messageRes())
        assertEquals(R.string.error_weak_password, AppError.WeakPassword().messageRes())
        assertEquals(R.string.error_unknown, AppError.Unknown().messageRes())
    }

    @Test
    fun `cada validacion tiene su texto`() {
        assertEquals(
            R.string.validation_routine_name_required,
            AppError.Validation(AppError.Validation.Reason.RoutineNameRequired).messageRes()
        )
    }

    @Test
    fun `cualquier Throwable tiene un texto seguro`() {
        assertEquals(R.string.error_unknown, RuntimeException("detalle tecnico").toMessageRes())
        assertEquals(R.string.error_network, IOException("detalle tecnico").toMessageRes())
    }
}