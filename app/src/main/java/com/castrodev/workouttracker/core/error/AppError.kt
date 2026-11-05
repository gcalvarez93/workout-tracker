// Path: app/src/main/java/com/castrodev/workouttracker/core/error/AppError.kt
package com.castrodev.workouttracker.core.error

/**
 * Errores de la aplicación, ya clasificados. Kotlin puro: no conoce Retrofit, Firebase
 * ni recursos de Android.
 *
 * El mensaje de la excepción es técnico y solo sirve para los logs. Lo que ve el usuario
 * se decide en la capa de presentación (`core/ui/ErrorMessages.kt`).
 */
sealed class AppError(message: String, cause: Throwable? = null) : Exception(message, cause) {

    class Network(cause: Throwable? = null) : AppError("Network error", cause)
    class Server(cause: Throwable? = null) : AppError("Server error", cause)
    class Timeout(cause: Throwable? = null) : AppError("Timeout", cause)
    class Unauthorized(cause: Throwable? = null) : AppError("Unauthorized", cause)
    class Forbidden(cause: Throwable? = null) : AppError("Forbidden", cause)
    class NotFound(cause: Throwable? = null) : AppError("Not found", cause)
    class TooManyRequests(cause: Throwable? = null) : AppError("Too many requests", cause)

    class InvalidCredentials(cause: Throwable? = null) : AppError("Invalid credentials", cause)
    class InvalidEmail(cause: Throwable? = null) : AppError("Invalid email", cause)
    class EmailAlreadyInUse(cause: Throwable? = null) : AppError("Email already in use", cause)
    class WeakPassword(cause: Throwable? = null) : AppError("Weak password", cause)

    /** Validación de negocio que se resuelve sin llamar a la red. */
    class Validation(val reason: Reason) : AppError("Validation failed: $reason") {
        enum class Reason {
            RoutineNameRequired
        }
    }

    class Unknown(cause: Throwable? = null) : AppError("Unknown error", cause)
}