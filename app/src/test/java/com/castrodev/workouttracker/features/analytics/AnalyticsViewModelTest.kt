// Path: app/src/test/java/com/castrodev/workouttracker/features/analytics/presentation/viewmodel/AnalyticsViewModelTest.kt
package com.castrodev.workouttracker.features.analytics.presentation.viewmodel

import com.castrodev.workouttracker.R
import com.castrodev.workouttracker.core.error.AppError
import com.castrodev.workouttracker.fakes.FakeAnalyticsRepository
import com.castrodev.workouttracker.fakes.testExerciseProgress
import com.castrodev.workouttracker.fakes.testWeeklySummary
import com.castrodev.workouttracker.features.analytics.domain.entity.WeeklySummaryEntity
import com.castrodev.workouttracker.features.analytics.domain.repository.AnalyticsRepository
import com.castrodev.workouttracker.features.analytics.domain.usecase.GetExerciseProgressUseCase
import com.castrodev.workouttracker.features.analytics.domain.usecase.GetWeeklyAnalyticsUseCase
import com.castrodev.workouttracker.util.MainDispatcherRule
import kotlinx.coroutines.CompletableDeferred
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.IOException

class AnalyticsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var repository: FakeAnalyticsRepository

    @Before
    fun setUp() {
        repository = FakeAnalyticsRepository()
    }

    private fun viewModel(source: AnalyticsRepository = repository) = AnalyticsViewModel(
        getWeeklySummary = GetWeeklyAnalyticsUseCase(source),
        getExerciseProgress = GetExerciseProgressUseCase(source)
    )

    // --- Resumen semanal -------------------------------------------------------------------

    @Test
    fun `al crearse carga el resumen de la semana actual`() {
        val vm = viewModel()

        assertEquals(listOf(vm.selectedYear to vm.selectedWeek), repository.summaryRequests)
        assertEquals(WeeklySummaryState.Success(testWeeklySummary()), vm.summaryState.value)
    }

    @Test
    fun `si la carga falla por la red publica el aviso de red`() {
        repository.summaryResult = Result.failure(IOException("sin red"))

        val vm = viewModel()

        assertEquals(WeeklySummaryState.Error(R.string.error_network), vm.summaryState.value)
    }

    @Test
    fun `el mensaje tecnico de una excepcion nunca se muestra`() {
        repository.summaryResult = Result.failure(IllegalStateException("Cannot invoke method on null at Foo.kt:42"))

        val vm = viewModel()

        assertEquals(WeeklySummaryState.Error(R.string.error_unknown), vm.summaryState.value)
    }

    // --- Cambio de semana ------------------------------------------------------------------

    @Test
    fun `la semana anterior pide el resumen de esa semana`() {
        val vm = viewModel()
        vm.selectedYear = 2026
        vm.selectedWeek = 10

        vm.previousWeek()

        assertEquals(2026 to 9, repository.summaryRequests.last())
    }

    @Test
    fun `al retroceder desde la semana 1 pasa a la ultima del ano anterior`() {
        val vm = viewModel()
        vm.selectedYear = 2026
        vm.selectedWeek = 1

        vm.previousWeek()

        assertEquals(2025, vm.selectedYear)
        assertEquals(52, vm.selectedWeek)
        assertEquals(2025 to 52, repository.summaryRequests.last())
    }

    @Test
    fun `la semana siguiente pide el resumen de esa semana`() {
        val vm = viewModel()
        vm.selectedYear = 2026
        vm.selectedWeek = 10

        vm.nextWeek()

        assertEquals(2026 to 11, repository.summaryRequests.last())
    }

    @Test
    fun `al avanzar desde la semana 52 pasa a la primera del ano siguiente`() {
        val vm = viewModel()
        vm.selectedYear = 2026
        vm.selectedWeek = 52

        vm.nextWeek()

        assertEquals(2027, vm.selectedYear)
        assertEquals(1, vm.selectedWeek)
    }

    @Test
    fun `gana siempre la ultima semana pedida`() {
        val first = CompletableDeferred<Result<WeeklySummaryEntity>>()
        var calls = 0
        val newest = testWeeklySummary(week = 2)
        val source = object : AnalyticsRepository by repository {
            override suspend fun getWeeklySummary(year: Int, week: Int): Result<WeeklySummaryEntity> =
                if (++calls == 1) first.await() else Result.success(newest)
        }
        val vm = viewModel(source)

        vm.nextWeek()
        first.complete(Result.success(testWeeklySummary(week = 1)))

        assertEquals(WeeklySummaryState.Success(newest), vm.summaryState.value)
    }

    // --- Progreso de un ejercicio ----------------------------------------------------------

    @Test
    fun `cargar el progreso de un ejercicio publica el resultado`() {
        val vm = viewModel()

        vm.loadExerciseProgress("Press banca")

        assertEquals(listOf("Press banca"), repository.progressRequests)
        assertEquals(ExerciseProgressState.Success(testExerciseProgress()), vm.progressState.value)
    }

    @Test
    fun `si falla el progreso publica el aviso correspondiente`() {
        repository.progressResult = Result.failure(AppError.NotFound())
        val vm = viewModel()

        vm.loadExerciseProgress("Press banca")

        assertEquals(ExerciseProgressState.Error(R.string.error_not_found), vm.progressState.value)
    }
}