// Path: app/src/main/java/com/castrodev/workouttracker/features/analytics/presentation/viewmodel/AnalyticsViewModel.kt
package com.castrodev.workouttracker.features.analytics.presentation.viewmodel

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.castrodev.workouttracker.core.ui.toMessageRes
import com.castrodev.workouttracker.features.analytics.domain.entity.ExerciseProgressEntity
import com.castrodev.workouttracker.features.analytics.domain.entity.WeeklySummaryEntity
import com.castrodev.workouttracker.features.analytics.domain.usecase.GetExerciseProgressUseCase
import com.castrodev.workouttracker.features.analytics.domain.usecase.GetWeeklyAnalyticsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

sealed class WeeklySummaryState {
    object Loading : WeeklySummaryState()
    data class Success(val summary: WeeklySummaryEntity) : WeeklySummaryState()
    data class Error(@StringRes val messageRes: Int) : WeeklySummaryState()
}

sealed class ExerciseProgressState {
    object Idle : ExerciseProgressState()
    object Loading : ExerciseProgressState()
    data class Success(val progress: ExerciseProgressEntity) : ExerciseProgressState()
    data class Error(@StringRes val messageRes: Int) : ExerciseProgressState()
}

@HiltViewModel
class AnalyticsViewModel @Inject constructor(
    private val getWeeklySummary: GetWeeklyAnalyticsUseCase,
    private val getExerciseProgress: GetExerciseProgressUseCase
) : ViewModel() {

    private val _summaryState = MutableStateFlow<WeeklySummaryState>(WeeklySummaryState.Loading)
    val summaryState: StateFlow<WeeklySummaryState> = _summaryState

    private val _progressState = MutableStateFlow<ExerciseProgressState>(ExerciseProgressState.Idle)
    val progressState: StateFlow<ExerciseProgressState> = _progressState

    private val calendar = Calendar.getInstance()
    var selectedYear: Int = calendar.get(Calendar.YEAR)
    var selectedWeek: Int = calendar.get(Calendar.WEEK_OF_YEAR)

    private var summaryJob: Job? = null

    init { loadWeeklySummary() }

    fun loadWeeklySummary() {
        // Al cambiar de semana varias veces seguidas, gana siempre la última petición: una
        // respuesta lenta de una semana anterior no debe sustituir a la de la semana actual.
        summaryJob?.cancel()
        summaryJob = viewModelScope.launch {
            _summaryState.value = WeeklySummaryState.Loading
            getWeeklySummary(selectedYear, selectedWeek)
                .onSuccess { _summaryState.value = WeeklySummaryState.Success(it) }
                .onFailure { _summaryState.value = WeeklySummaryState.Error(it.toMessageRes()) }
        }
    }

    fun loadExerciseProgress(exerciseName: String) {
        viewModelScope.launch {
            _progressState.value = ExerciseProgressState.Loading
            getExerciseProgress(exerciseName)
                .onSuccess { _progressState.value = ExerciseProgressState.Success(it) }
                .onFailure { _progressState.value = ExerciseProgressState.Error(it.toMessageRes()) }
        }
    }

    fun previousWeek() {
        selectedWeek--
        if (selectedWeek < 1) { selectedYear--; selectedWeek = 52 }
        loadWeeklySummary()
    }

    fun nextWeek() {
        selectedWeek++
        if (selectedWeek > 52) { selectedYear++; selectedWeek = 1 }
        loadWeeklySummary()
    }
}