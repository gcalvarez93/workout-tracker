// Path: app/src/test/java/com/castrodev/workouttracker/features/auth/presentation/viewmodel/AuthViewModelTest.kt
package com.castrodev.workouttracker.features.auth.presentation.viewmodel

import com.castrodev.workouttracker.R
import com.castrodev.workouttracker.core.error.AppError
import com.castrodev.workouttracker.core.network.SessionEvents
import com.castrodev.workouttracker.fakes.FakeAuthRepository
import com.castrodev.workouttracker.fakes.testUser
import com.castrodev.workouttracker.features.auth.domain.entity.UserEntity
import com.castrodev.workouttracker.features.auth.domain.repository.AuthRepository
import com.castrodev.workouttracker.features.auth.domain.usecase.IsAuthenticatedUseCase
import com.castrodev.workouttracker.features.auth.domain.usecase.SignInUseCase
import com.castrodev.workouttracker.features.auth.domain.usecase.SignInWithGoogleUseCase
import com.castrodev.workouttracker.features.auth.domain.usecase.SignOutUseCase
import com.castrodev.workouttracker.features.auth.domain.usecase.SignUpUseCase
import com.castrodev.workouttracker.util.MainDispatcherRule
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.IOException

class AuthViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var repository: FakeAuthRepository
    private lateinit var sessionEvents: SessionEvents

    @Before
    fun setUp() {
        repository = FakeAuthRepository()
        sessionEvents = SessionEvents()
    }

    private fun viewModel(source: AuthRepository = repository) = AuthViewModel(
        signIn = SignInUseCase(source),
        signUp = SignUpUseCase(source),
        signInWithGoogle = SignInWithGoogleUseCase(source),
        signOut = SignOutUseCase(source),
        isUserAuthenticated = IsAuthenticatedUseCase(source),
        sessionEvents = sessionEvents
    )

    // --- Estado inicial --------------------------------------------------------------------

    @Test
    fun `arranca autenticado si ya hay sesion`() {
        repository.signedInUser = testUser

        assertTrue(viewModel().isAuthenticated.value)
    }

    @Test
    fun `arranca sin autenticar si no hay sesion`() {
        assertFalse(viewModel().isAuthenticated.value)
    }

    // --- Inicio de sesion ------------------------------------------------------------------

    @Test
    fun `login correcto publica el usuario y marca autenticado`() {
        val vm = viewModel()

        vm.signIn("ana@test.com", "123456")

        assertEquals(AuthState.Success(testUser), vm.state.value)
        assertTrue(vm.isAuthenticated.value)
    }

    @Test
    fun `login con credenciales incorrectas muestra el aviso correspondiente`() {
        repository.signInResult = Result.failure(AppError.InvalidCredentials())
        val vm = viewModel()

        vm.signIn("ana@test.com", "mala")

        assertEquals(AuthState.Error(R.string.error_invalid_credentials), vm.state.value)
        assertFalse(vm.isAuthenticated.value)
    }

    @Test
    fun `login sin red muestra el aviso de red`() {
        repository.signInResult = Result.failure(IOException("detalle tecnico"))
        val vm = viewModel()

        vm.signIn("ana@test.com", "123456")

        assertEquals(AuthState.Error(R.string.error_network), vm.state.value)
    }

    @Test
    fun `el mensaje tecnico de una excepcion nunca se muestra`() {
        repository.signInResult = Result.failure(IllegalStateException("Sign in returned no user"))
        val vm = viewModel()

        vm.signIn("ana@test.com", "123456")

        assertEquals(AuthState.Error(R.string.error_unknown), vm.state.value)
    }

    @Test
    fun `un segundo intento mientras hay uno en curso se ignora`() {
        val gate = CompletableDeferred<Result<UserEntity>>()
        var calls = 0
        val slow = object : AuthRepository by repository {
            override suspend fun signIn(email: String, password: String): Result<UserEntity> {
                calls++
                return gate.await()
            }
        }
        val vm = viewModel(slow)

        vm.signIn("ana@test.com", "123456")
        vm.signIn("ana@test.com", "123456")

        assertEquals(1, calls)
        assertEquals(AuthState.Loading, vm.state.value)

        gate.complete(Result.success(testUser))
        assertEquals(AuthState.Success(testUser), vm.state.value)
    }

    // --- Registro y Google -----------------------------------------------------------------

    @Test
    fun `registro correcto marca autenticado`() {
        val vm = viewModel()

        vm.signUp("ana@test.com", "123456", "Ana")

        assertEquals(AuthState.Success(testUser), vm.state.value)
        assertTrue(vm.isAuthenticated.value)
    }

    @Test
    fun `registro con un correo ya usado muestra su aviso`() {
        repository.signUpResult = Result.failure(AppError.EmailAlreadyInUse())
        val vm = viewModel()

        vm.signUp("ana@test.com", "123456", "Ana")

        assertEquals(AuthState.Error(R.string.error_email_in_use), vm.state.value)
        assertFalse(vm.isAuthenticated.value)
    }

    @Test
    fun `registro con contrasena debil muestra su aviso`() {
        repository.signUpResult = Result.failure(AppError.WeakPassword())
        val vm = viewModel()

        vm.signUp("ana@test.com", "123", "Ana")

        assertEquals(AuthState.Error(R.string.error_weak_password), vm.state.value)
    }

    @Test
    fun `un fallo al entrar con Google tambien se traduce`() {
        repository.signInResult = Result.failure(IOException("sin red"))
        val vm = viewModel()

        vm.signInWithGoogle("token")

        assertEquals(AuthState.Error(R.string.error_network), vm.state.value)
    }

    // --- Cierre de sesion ------------------------------------------------------------------

    @Test
    fun `cerrar sesion limpia el estado`() {
        repository.signedInUser = testUser
        val vm = viewModel()

        vm.signOut()

        assertFalse(vm.isAuthenticated.value)
        assertEquals(AuthState.Idle, vm.state.value)
        assertFalse(repository.isAuthenticated())
    }

    // --- Sesion caducada -------------------------------------------------------------------

    @Test
    fun `sesion caducada cierra la sesion y deja el motivo para el login`() {
        repository.signedInUser = testUser
        val vm = viewModel()

        val handled = vm.onSessionExpired()

        assertTrue(handled)
        assertFalse(vm.isAuthenticated.value)
        assertNull(repository.signedInUser)
        assertEquals(AuthState.Error(R.string.error_unauthorized), vm.state.value)
    }

    @Test
    fun `sesion caducada sin sesion abierta no hace nada`() {
        val vm = viewModel()

        val handled = vm.onSessionExpired()

        assertFalse(handled)
        assertEquals(AuthState.Idle, vm.state.value)
    }

    @Test
    fun `un segundo aviso de sesion caducada no vuelve a cerrar sesion`() {
        repository.signedInUser = testUser
        val vm = viewModel()
        vm.onSessionExpired()

        assertFalse(vm.onSessionExpired())
    }

    @Test
    fun `el aviso de la capa de red llega al ViewModel`() = runTest {
        val vm = viewModel()
        sessionEvents.notifyExpired()

        assertEquals(Unit, vm.sessionExpired.first())
    }
}