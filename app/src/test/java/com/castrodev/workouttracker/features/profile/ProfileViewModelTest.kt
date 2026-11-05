// Path: app/src/test/java/com/castrodev/workouttracker/features/profile/presentation/viewmodel/ProfileViewModelTest.kt
package com.castrodev.workouttracker.features.profile.presentation.viewmodel

import com.castrodev.workouttracker.R
import com.castrodev.workouttracker.core.error.AppError
import com.castrodev.workouttracker.fakes.FakeProfileRepository
import com.castrodev.workouttracker.fakes.testProfile
import com.castrodev.workouttracker.features.profile.domain.entity.ProfileEntity
import com.castrodev.workouttracker.features.profile.domain.repository.ProfileRepository
import com.castrodev.workouttracker.features.profile.domain.usecase.GetProfileUseCase
import com.castrodev.workouttracker.features.profile.domain.usecase.UpdateProfileUseCase
import com.castrodev.workouttracker.util.MainDispatcherRule
import kotlinx.coroutines.CompletableDeferred
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.IOException

class ProfileViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var repository: FakeProfileRepository

    @Before
    fun setUp() {
        repository = FakeProfileRepository()
    }

    private fun viewModel(source: ProfileRepository = repository) = ProfileViewModel(
        getProfile = GetProfileUseCase(source),
        updateProfile = UpdateProfileUseCase(source)
    )

    // --- Carga -----------------------------------------------------------------------------

    @Test
    fun `al crearse carga el perfil`() {
        val vm = viewModel()

        assertEquals(ProfileState.Success(testProfile()), vm.state.value)
    }

    @Test
    fun `si la carga falla por la red publica el aviso de red`() {
        repository.profileResult = Result.failure(IOException("sin red"))

        val vm = viewModel()

        assertEquals(ProfileState.Error(R.string.error_network), vm.state.value)
    }

    @Test
    fun `el mensaje tecnico de una excepcion nunca se muestra`() {
        repository.profileResult = Result.failure(IllegalStateException("Cannot invoke method on null at Foo.kt:42"))

        val vm = viewModel()

        assertEquals(ProfileState.Error(R.string.error_unknown), vm.state.value)
    }

    // --- Actualizar ------------------------------------------------------------------------

    @Test
    fun `guardar un cambio publica el perfil actualizado`() {
        val updated = testProfile(notifications = false)
        repository.updateResult = Result.success(updated)
        val vm = viewModel()

        vm.update("Ana", "es", false)

        assertEquals(ProfileState.Success(updated), vm.state.value)
        assertEquals(listOf(Triple("Ana", "es", false)), repository.updates)
        assertNull(vm.actionError.value)
        assertFalse(vm.isUpdating.value)
    }

    @Test
    fun `si falla guardar se conserva el perfil que ya se ve y el error queda aparte`() {
        repository.updateResult = Result.failure(AppError.Server())
        val vm = viewModel()

        vm.update("Ana", "es", false)

        assertEquals(ProfileState.Success(testProfile()), vm.state.value)
        assertEquals(R.string.error_server, vm.actionError.value)
        assertFalse(vm.isUpdating.value)
    }

    @Test
    fun `tras un fallo se puede volver a guardar`() {
        repository.updateResult = Result.failure(IOException("sin red"))
        val vm = viewModel()
        vm.update("Ana", "es", false)

        val updated = testProfile(notifications = false)
        repository.updateResult = Result.success(updated)
        vm.update("Ana", "es", false)

        assertEquals(ProfileState.Success(updated), vm.state.value)
        assertNull(vm.actionError.value)
    }

    @Test
    fun `un segundo guardado mientras hay uno en curso se ignora`() {
        val gate = CompletableDeferred<Result<ProfileEntity>>()
        var calls = 0
        val source = object : ProfileRepository by repository {
            override suspend fun updateProfile(name: String, language: String, notifications: Boolean): Result<ProfileEntity> {
                calls++
                return gate.await()
            }
        }
        val vm = viewModel(source)

        vm.update("Ana", "es", false)
        vm.update("Ana", "es", true)

        assertEquals(1, calls)
        assertTrue(vm.isUpdating.value)

        gate.complete(Result.success(testProfile(notifications = false)))
        assertFalse(vm.isUpdating.value)
    }

    @Test
    fun `clearActionError limpia el error`() {
        repository.updateResult = Result.failure(IOException("sin red"))
        val vm = viewModel()
        vm.update("Ana", "es", false)

        vm.clearActionError()

        assertNull(vm.actionError.value)
    }
}