// Path: app/src/test/java/com/castrodev/workouttracker/features/bodyweight/presentation/viewmodel/BodyweightViewModelTest.kt
package com.castrodev.workouttracker.features.bodyweight.presentation.viewmodel

import com.castrodev.workouttracker.R
import com.castrodev.workouttracker.core.error.AppError
import com.castrodev.workouttracker.fakes.FakeBodyweightRepository
import com.castrodev.workouttracker.fakes.testBodyweight
import com.castrodev.workouttracker.features.bodyweight.domain.repository.BodyweightRepository
import com.castrodev.workouttracker.features.bodyweight.domain.usecase.AddBodyweightUseCase
import com.castrodev.workouttracker.features.bodyweight.domain.usecase.GetBodyweightHistoryUseCase
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

class BodyweightViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var repository: FakeBodyweightRepository

    @Before
    fun setUp() {
        repository = FakeBodyweightRepository()
    }

    private fun viewModel(source: BodyweightRepository = repository) = BodyweightViewModel(
        getHistory = GetBodyweightHistoryUseCase(source),
        addWeight = AddBodyweightUseCase(source)
    )

    // --- Historial -------------------------------------------------------------------------

    @Test
    fun `al crearse carga el historial`() {
        val entries = listOf(testBodyweight("b1"), testBodyweight("b2", 79.5))
        repository.historyResult = Result.success(entries)

        val vm = viewModel()

        assertEquals(BodyweightState.Success(entries), vm.state.value)
    }

    @Test
    fun `si la carga falla por la red publica el aviso de red`() {
        repository.historyResult = Result.failure(IOException("sin red"))

        val vm = viewModel()

        assertEquals(BodyweightState.Error(R.string.error_network), vm.state.value)
    }

    @Test
    fun `el mensaje tecnico de una excepcion nunca se muestra`() {
        repository.historyResult = Result.failure(IllegalStateException("Cannot invoke method on null at Foo.kt:42"))

        val vm = viewModel()

        assertEquals(BodyweightState.Error(R.string.error_unknown), vm.state.value)
    }

    // --- Registrar un peso -----------------------------------------------------------------

    @Test
    fun `registrar un peso lo envia, marca la accion y recarga el historial`() {
        val vm = viewModel()

        vm.logWeight(80.5, "2026-01-02", "tras entrenar")

        assertEquals(listOf(Triple(80.5, "2026-01-02", "tras entrenar")), repository.logged)
        assertTrue(vm.actionSuccess.value)
        assertEquals(2, repository.getHistoryCalls)
        assertNull(vm.actionError.value)
        assertFalse(vm.isSaving.value)
    }

    @Test
    fun `si falla registrar el peso se conserva el historial y el error queda aparte`() {
        val entries = listOf(testBodyweight("b1"))
        repository.historyResult = Result.success(entries)
        repository.logResult = Result.failure(AppError.Server())
        val vm = viewModel()

        vm.logWeight(80.5, "2026-01-02", "")

        assertEquals(R.string.error_server, vm.actionError.value)
        assertEquals(BodyweightState.Success(entries), vm.state.value)
        assertFalse(vm.actionSuccess.value)
        assertEquals(1, repository.getHistoryCalls)
        assertFalse(vm.isSaving.value)
    }

    @Test
    fun `tras un fallo se puede reintentar`() {
        repository.logResult = Result.failure(IOException("sin red"))
        val vm = viewModel()
        vm.logWeight(80.5, "2026-01-02", "")

        repository.logResult = Result.success("id")
        vm.logWeight(80.5, "2026-01-02", "")

        assertTrue(vm.actionSuccess.value)
        assertNull(vm.actionError.value)
    }

    @Test
    fun `un segundo guardado mientras hay uno en curso se ignora`() {
        val gate = CompletableDeferred<Result<String>>()
        var calls = 0
        val source = object : BodyweightRepository by repository {
            override suspend fun logWeight(weightKg: Double, date: String, notes: String): Result<String> {
                calls++
                return gate.await()
            }
        }
        val vm = viewModel(source)

        vm.logWeight(80.5, "2026-01-02", "")
        vm.logWeight(80.5, "2026-01-02", "")

        assertEquals(1, calls)
        assertTrue(vm.isSaving.value)

        gate.complete(Result.success("id"))
        assertFalse(vm.isSaving.value)
        assertTrue(vm.actionSuccess.value)
    }

    @Test
    fun `resetActionSuccess y clearActionError limpian las marcas`() {
        repository.logResult = Result.failure(IOException("sin red"))
        val vm = viewModel()
        vm.logWeight(80.5, "2026-01-02", "")

        vm.clearActionError()
        repository.logResult = Result.success("id")
        vm.logWeight(80.5, "2026-01-02", "")
        vm.resetActionSuccess()

        assertNull(vm.actionError.value)
        assertFalse(vm.actionSuccess.value)
    }
}