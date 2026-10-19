// Path: app/src/test/java/com/castrodev/workouttracker/features/auth/presentation/viewmodel/AuthViewModelTest.kt
package com.castrodev.workouttracker.features.auth.presentation.viewmodel

import com.castrodev.workouttracker.fakes.FakeAuthRepository
import com.castrodev.workouttracker.fakes.testUser
import com.castrodev.workouttracker.features.auth.domain.usecase.IsAuthenticatedUseCase
import com.castrodev.workouttracker.features.auth.domain.usecase.SignInUseCase
import com.castrodev.workouttracker.features.auth.domain.usecase.SignInWithGoogleUseCase
import com.castrodev.workouttracker.features.auth.domain.usecase.SignOutUseCase
import com.castrodev.workouttracker.features.auth.domain.usecase.SignUpUseCase
import com.castrodev.workouttracker.util.MainDispatcherRule
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class AuthViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var repository: FakeAuthRepository

    @Before
    fun setUp() {
        repository = FakeAuthRepository()
    }

    private fun viewModel() = AuthViewModel(
        signIn = SignInUseCase(repository),
        signUp = SignUpUseCase(repository),
        signInWithGoogle = SignInWithGoogleUseCase(repository),
        signOut = SignOutUseCase(repository),
        isUserAuthenticated = IsAuthenticatedUseCase(repository)
    )

    @Test
    fun `arranca autenticado si ya hay sesion`() {
        repository.signedInUser = testUser

        assertTrue(viewModel().isAuthenticated.value)
    }

    @Test
    fun `arranca sin autenticar si no hay sesion`() {
        assertFalse(viewModel().isAuthenticated.value)
    }

    @Test
    fun `login correcto publica el usuario y marca autenticado`() {
        val vm = viewModel()

        vm.signIn("ana@test.com", "123456")

        assertEquals(AuthState.Success(testUser), vm.state.value)
        assertTrue(vm.isAuthenticated.value)
    }

    @Test
    fun `login fallido publica el error y no autentica`() {
        repository.signInResult = Result.failure(IllegalStateException("credenciales invalidas"))
        val vm = viewModel()

        vm.signIn("ana@test.com", "mala")

        assertEquals(AuthState.Error("credenciales invalidas"), vm.state.value)
        assertFalse(vm.isAuthenticated.value)
    }

    @Test
    fun `registro correcto marca autenticado`() {
        val vm = viewModel()

        vm.signUp("ana@test.com", "123456", "Ana")

        assertEquals(AuthState.Success(testUser), vm.state.value)
        assertTrue(vm.isAuthenticated.value)
    }

    @Test
    fun `cerrar sesion limpia el estado`() {
        repository.signedInUser = testUser
        val vm = viewModel()

        vm.signOut()

        assertFalse(vm.isAuthenticated.value)
        assertEquals(AuthState.Idle, vm.state.value)
        assertFalse(repository.isAuthenticated())
    }
}