// Path: app/src/main/java/com/castrodev/workouttracker/features/analytics/presentation/viewmodel/AnalyticsViewModel.kt
package com.castrodev.workouttracker.features.analytics.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.castrodev.workouttracker.features.analytics.data.repository.AnalyticsRepositoryImpl
import com.castrodev.workouttracker.features.analytics.domain.entity.ExerciseProgressEntity
import com.castrodev.workouttracker.features.analytics.domain.entity.WeeklySummaryEntity
import com.castrodev.workouttracker.features.analytics.domain.usecase.GetExerciseProgressUseCase
import com.castrodev.workouttracker.features.analytics.domain.usecase.GetWeeklyAnalyticsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Calendar

sealed class WeeklySummaryState {
    object Loading : WeeklySummaryState()
    data class Success(val summary: WeeklySummaryEntity) : WeeklySummaryState()
    data class Error(val message: String) : WeeklySummaryState()
}

sealed class ExerciseProgressState {
    object Idle : ExerciseProgressState()
    object Loading : ExerciseProgressState()
    data class Success(val progress: ExerciseProgressEntity) : ExerciseProgressState()
    data class Error(val message: String) : ExerciseProgressState()
}

class AnalyticsViewModel : ViewModel() {
    private val repository          = AnalyticsRepositoryImpl()
    private val getWeeklySummary    = GetWeeklyAnalyticsUseCase(repository)
    private val getExerciseProgress = GetExerciseProgressUseCase(repository)

    private val _summaryState = MutableStateFlow<WeeklySummaryState>(WeeklySummaryState.Loading)
    val summaryState: StateFlow<WeeklySummaryState> = _summaryState

    private val _progressState = MutableStateFlow<ExerciseProgressState>(ExerciseProgressState.Idle)
    val progressState: StateFlow<ExerciseProgressState> = _progressState

    private val calendar = Calendar.getInstance()
    var selectedYear: Int = calendar.get(Calendar.YEAR)
    var selectedWeek: Int = calendar.get(Calendar.WEEK_OF_YEAR)

    init { loadWeeklySummary() }

    fun loadWeeklySummary() {
        viewModelScope.launch {
            _summaryState.value = WeeklySummaryState.Loading
            getWeeklySummary(selectedYear, selectedWeek)
                .onSuccess { _summaryState.value = WeeklySummaryState.Success(it) }
                .onFailure { _summaryState.value = WeeklySummaryState.Error(it.message ?: "Error") }
        }
    }

    fun loadExerciseProgress(exerciseName: String) {
        viewModelScope.launch {
            _progressState.value = ExerciseProgressState.Loading
            getExerciseProgress(exerciseName)
                .onSuccess { _progressState.value = ExerciseProgressState.Success(it) }
                .onFailure { _progressState.value = ExerciseProgressState.Error(it.message ?: "Error") }
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