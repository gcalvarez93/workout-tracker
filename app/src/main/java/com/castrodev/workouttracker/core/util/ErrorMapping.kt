// Path: app/src/main/java/com/castrodev/workouttracker/core/util/ErrorMapping.kt
package com.castrodev.workouttracker.core.util

import com.castrodev.workouttracker.core.error.AppError
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException

private const val FIREBASE_INVALID_EMAIL = "ERROR_INVALID_EMAIL"

/**
 * Traduce cualquier excepción de infraestructura (Retrofit, Firebase, red) a un [AppError].
 * La excepción original se conserva como `cause` para los logs.
 */
fun Throwable.toAppError(): AppError = when (this) {
    is AppError -> this

    is HttpException -> when (code()) {
        401 -> AppError.Unauthorized(this)
        403 -> AppError.Forbidden(this)
        404 -> AppError.NotFound(this)
        429 -> AppError.TooManyRequests(this)
        in 500..599 -> AppError.Server(this)
        else -> AppError.Unknown(this)
    }

    // SocketTimeoutException es un IOException: tiene que ir antes para no contarse como "sin red".
    is SocketTimeoutException -> AppError.Timeout(this)
    is FirebaseNetworkException, is IOException -> AppError.Network(this)
    is FirebaseTooManyRequestsException -> AppError.TooManyRequests(this)

    // FirebaseAuthWeakPasswordException hereda de FirebaseAuthInvalidCredentialsException:
    // tiene que comprobarse antes, o se confundiría con unas credenciales incorrectas.
    is FirebaseAuthWeakPasswordException -> AppError.WeakPassword(this)
    is FirebaseAuthUserCollisionException -> AppError.EmailAlreadyInUse(this)
    is FirebaseAuthInvalidUserException -> AppError.InvalidCredentials(this)
    is FirebaseAuthInvalidCredentialsException ->
        if (errorCode == FIREBASE_INVALID_EMAIL) AppError.InvalidEmail(this)
        else AppError.InvalidCredentials(this)

    else -> AppError.Unknown(this)
}