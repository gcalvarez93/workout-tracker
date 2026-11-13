// Path: app/src/test/java/com/castrodev/workouttracker/features/routines/presentation/viewmodel/RoutineViewModelTest.kt
package com.castrodev.workouttracker.features.routines.presentation.viewmodel

import com.castrodev.workouttracker.R
import com.castrodev.workouttracker.core.error.AppError
import com.castrodev.workouttracker.fakes.FakeRoutineRepository
import com.castrodev.workouttracker.fakes.testRoutine
import com.castrodev.workouttracker.features.routines.domain.entity.RoutineEntity
import com.castrodev.workouttracker.features.routines.domain.repository.RoutineRepository
import com.castrodev.workouttracker.features.routines.domain.usecase.CreateRoutineUseCase
import com.castrodev.workouttracker.features.routines.domain.usecase.DeleteRoutineUseCase
import com.castrodev.workouttracker.features.routines.domain.usecase.GetRoutineByIdUseCase
import com.castrodev.workouttracker.features.routines.domain.usecase.GetRoutinesUseCase
import com.castrodev.workouttracker.features.routines.domain.usecase.UpdateRoutineUseCase
import com.castrodev.workouttracker.util.MainDispatcherRule
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.IOException

class RoutineViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var repository: FakeRoutineRepository

    @Before
    fun setUp() {
        repository = FakeRoutineRepository()
    }

    private fun viewModel(source: RoutineRepository = repository) = RoutineViewModel(
        getRoutines = GetRoutinesUseCase(source),
        getRoutineById = GetRoutineByIdUseCase(source),
        createRoutine = CreateRoutineUseCase(source),
        updateRoutine = UpdateRoutineUseCase(source),
        deleteRoutine = DeleteRoutineUseCase(source)
    )

    // --- Carga -----------------------------------------------------------------------------

    @Test
    fun `al crearse carga las rutinas`() {
        val routines = listOf(testRoutine("r1"), testRoutine("r2"))
        repository.routinesResult = Result.success(routines)

        val vm = viewModel()

        assertEquals(RoutinesState.Success(routines), vm.routinesState.value)
    }

    @Test
    fun `si la carga falla por la red publica el aviso de red`() {
        repository.routinesResult = Result.failure(IOException("sin red"))

        val vm = viewModel()

        assertEquals(RoutinesState.Error(R.string.error_network), vm.routinesState.value)
    }

    @Test
    fun `el mensaje tecnico de una excepcion nunca se muestra`() {
        repository.routinesResult = Result.failure(IllegalStateException("Cannot invoke method on null at Foo.kt:42"))

        val vm = viewModel()

        assertEquals(RoutinesState.Error(R.string.error_unknown), vm.routinesState.value)
    }

    @Test
    fun `gana siempre la ultima carga`() {
        val first = CompletableDeferred<Result<List<RoutineEntity>>>()
        var calls = 0
        val newest = listOf(testRoutine("nueva"))
        val source = object : RoutineRepository by repository {
            override suspend fun getRoutines(): Result<List<RoutineEntity>> =
                if (++calls == 1) first.await() else Result.success(newest)
        }
        val vm = viewModel(source)

        vm.loadRoutines()
        first.complete(Result.success(listOf(testRoutine("vieja"))))

        assertEquals(RoutinesState.Success(newest), vm.routinesState.value)
    }

    @Test
    fun `si la carga inicial falla y se reintenta con exito se muestra la lista`() {
        repository.routinesResult = Result.failure(IOException("sin red"))
        val vm = viewModel()
        assertEquals(RoutinesState.Error(R.string.error_network), vm.routinesState.value)

        val routines = listOf(testRoutine("r1"))
        repository.routinesResult = Result.success(routines)
        vm.loadRoutines()

        assertEquals(RoutinesState.Success(routines), vm.routinesState.value)
    }

    @Test
    fun `si recargar falla con rutinas en pantalla las conserva y avisa`() {
        val routines = listOf(testRoutine("r1"))
        repository.routinesResult = Result.success(routines)
        val vm = viewModel()

        repository.routinesResult = Result.failure(IOException("sin red"))
        vm.loadRoutines()

        assertEquals(RoutinesState.Success(routines), vm.routinesState.value)
        assertEquals(R.string.error_network, vm.actionError.value)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `recargar con rutinas en pantalla no pasa por el estado de carga`() = runTest {
        repository.routinesResult = Result.success(listOf(testRoutine("r1")))
        val vm = viewModel()
        val states = mutableListOf<RoutinesState>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            vm.routinesState.toList(states)
        }

        vm.loadRoutines()

        assertTrue(states.none { it is RoutinesState.Loading })
    }

    @Test
    fun `si recargar falla con la lista vacia muestra la pantalla de error`() {
        repository.routinesResult = Result.success(emptyList())
        val vm = viewModel()

        repository.routinesResult = Result.failure(IOException("sin red"))
        vm.loadRoutines()

        assertEquals(RoutinesState.Error(R.string.error_network), vm.routinesState.value)
        assertNull(vm.actionError.value)
    }

    // --- Detalle ---------------------------------------------------------------------------

    @Test
    fun `cargar el detalle publica la rutina`() {
        repository.routineByIdResult = Result.success(testRoutine("r9"))
        val vm = viewModel()

        vm.loadRoutineById("r9")

        assertEquals(RoutineDetailState.Success(testRoutine("r9")), vm.detailState.value)
    }

    @Test
    fun `si falla el detalle publica el aviso correspondiente`() {
        repository.routineByIdResult = Result.failure(AppError.NotFound())
        val vm = viewModel()

        vm.loadRoutineById("r9")

        assertEquals(RoutineDetailState.Error(R.string.error_not_found), vm.detailState.value)
    }

    // --- Crear y editar --------------------------------------------------------------------

    @Test
    fun `crear una rutina marca la accion como correcta y recarga la lista`() {
        val vm = viewModel()

        vm.create(testRoutine(name = "Pierna"))

        assertTrue(vm.actionSuccess.value)
        assertEquals(listOf("Pierna"), repository.created.map { it.name })
        assertEquals(2, repository.getRoutinesCalls)
        assertNull(vm.actionError.value)
        assertFalse(vm.isSaving.value)
    }

    @Test
    fun `crear una rutina sin nombre avisa sin tocar la lista ni llamar al repositorio`() {
        val loaded = listOf(testRoutine("r1"))
        repository.routinesResult = Result.success(loaded)
        val vm = viewModel()

        vm.create(testRoutine(name = ""))

        assertEquals(R.string.validation_routine_name_required, vm.actionError.value)
        assertEquals(RoutinesState.Success(loaded), vm.routinesState.value)
        assertFalse(vm.actionSuccess.value)
        assertTrue(repository.created.isEmpty())
    }

    @Test
    fun `si falla crear se conserva la lista y el error queda aparte`() {
        val loaded = listOf(testRoutine("r1"), testRoutine("r2"))
        repository.routinesResult = Result.success(loaded)
        repository.createResult = Result.failure(IOException("sin red"))
        val vm = viewModel()

        vm.create(testRoutine(name = "Pierna"))

        assertEquals(R.string.error_network, vm.actionError.value)
        assertEquals(RoutinesState.Success(loaded), vm.routinesState.value)
        assertFalse(vm.actionSuccess.value)
        assertEquals(1, repository.getRoutinesCalls)
        assertFalse(vm.isSaving.value)
    }

    @Test
    fun `tras un fallo se puede reintentar`() {
        repository.createResult = Result.failure(IOException("sin red"))
        val vm = viewModel()
        vm.create(testRoutine(name = "Pierna"))

        repository.createResult = Result.success("id-1")
        vm.create(testRoutine(name = "Pierna"))

        assertTrue(vm.actionSuccess.value)
        assertNull(vm.actionError.value)
        assertEquals(2, repository.created.size)
    }

    @Test
    fun `editar una rutina marca la accion como correcta`() {
        val vm = viewModel()

        vm.update(testRoutine("r1", name = "Nueva"))

        assertTrue(vm.actionSuccess.value)
        assertEquals(listOf("Nueva"), repository.updated.map { it.name })
    }

    @Test
    fun `si falla editar se conserva la lista y avisa`() {
        val loaded = listOf(testRoutine("r1"))
        repository.routinesResult = Result.success(loaded)
        repository.updateResult = Result.failure(AppError.Server())
        val vm = viewModel()

        vm.update(testRoutine("r1", name = "Nueva"))

        assertEquals(R.string.error_server, vm.actionError.value)
        assertEquals(RoutinesState.Success(loaded), vm.routinesState.value)
        assertFalse(vm.actionSuccess.value)
    }

    @Test
    fun `un segundo guardado mientras hay uno en curso se ignora`() {
        val gate = CompletableDeferred<Result<String>>()
        var calls = 0
        val source = object : RoutineRepository by repository {
            override suspend fun createRoutine(routine: RoutineEntity): Result<String> {
                calls++
                return gate.await()
            }
        }
        val vm = viewModel(source)

        vm.create(testRoutine(name = "Pierna"))
        vm.create(testRoutine(name = "Pierna"))

        assertEquals(1, calls)
        assertTrue(vm.isSaving.value)

        gate.complete(Result.success("id"))
        assertFalse(vm.isSaving.value)
        assertTrue(vm.actionSuccess.value)
    }

    // --- Borrar ----------------------------------------------------------------------------

    @Test
    fun `borrar una rutina llama al repositorio y recarga`() {
        val vm = viewModel()

        vm.delete("r1")

        assertEquals(listOf("r1"), repository.deletedIds)
        assertEquals(2, repository.getRoutinesCalls)
        assertFalse(vm.actionSuccess.value)
    }

    @Test
    fun `si falla el borrado se conserva la lista, avisa y no recarga`() {
        val loaded = listOf(testRoutine("r1"))
        repository.routinesResult = Result.success(loaded)
        repository.deleteResult = Result.failure(AppError.Server())
        val vm = viewModel()

        vm.delete("r1")

        assertEquals(R.string.error_server, vm.actionError.value)
        assertEquals(RoutinesState.Success(loaded), vm.routinesState.value)
        assertEquals(1, repository.getRoutinesCalls)
    }

    // --- Marcas ----------------------------------------------------------------------------

    @Test
    fun `resetActionSuccess limpia la marca`() {
        val vm = viewModel()
        vm.create(testRoutine())

        vm.resetActionSuccess()

        assertFalse(vm.actionSuccess.value)
    }

    @Test
    fun `clearActionError limpia el error`() {
        repository.deleteResult = Result.failure(IOException("sin red"))
        val vm = viewModel()
        vm.delete("r1")

        vm.clearActionError()

        assertNull(vm.actionError.value)
    }
}