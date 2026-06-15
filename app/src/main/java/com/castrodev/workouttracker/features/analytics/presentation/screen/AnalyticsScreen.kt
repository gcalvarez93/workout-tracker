// Path: app/src/main/java/com/castrodev/workouttracker/features/analytics/presentation/screen/AnalyticsScreen.kt
package com.castrodev.workouttracker.features.analytics.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.castrodev.workouttracker.R
import com.castrodev.workouttracker.features.analytics.presentation.viewmodel.AnalyticsViewModel
import com.castrodev.workouttracker.features.analytics.presentation.viewmodel.WeeklySummaryState
import com.castrodev.workouttracker.features.bodyweight.presentation.components.AddBodyweightDialog
import com.castrodev.workouttracker.features.bodyweight.presentation.components.BodyweightCard
import com.castrodev.workouttracker.features.bodyweight.presentation.viewmodel.BodyweightState
import com.castrodev.workouttracker.features.bodyweight.presentation.viewmodel.BodyweightViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen(
    analyticsViewModel: AnalyticsViewModel = viewModel(),
    bodyweightViewModel: BodyweightViewModel = viewModel()
) {
    val summaryState    by analyticsViewModel.summaryState.collectAsState()
    val bodyweightState by bodyweightViewModel.state.collectAsState()
    val actionSuccess   by bodyweightViewModel.actionSuccess.collectAsState()
    var showAddWeight   by remember { mutableStateOf(false) }

    LaunchedEffect(actionSuccess) {
        if (actionSuccess) { showAddWeight = false; bodyweightViewModel.resetActionSuccess() }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text(stringResource(R.string.analytics)) }) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddWeight = true },
                containerColor = MaterialTheme.colorScheme.primary
            ) { Icon(Icons.Default.Add, null) }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Week selector
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(onClick = { analyticsViewModel.previousWeek() }) { Icon(Icons.Default.ChevronLeft, null) }
                    Text(
                        text  = "${stringResource(R.string.week)} ${analyticsViewModel.selectedWeek} · ${analyticsViewModel.selectedYear}",
                        style = MaterialTheme.typography.titleMedium
                    )
                    IconButton(onClick = { analyticsViewModel.nextWeek() }) { Icon(Icons.Default.ChevronRight, null) }
                }
            }

            // Weekly stats
            when (val s = summaryState) {
                is WeeklySummaryState.Loading -> item { Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) { CircularProgressIndicator() } }
                is WeeklySummaryState.Error   -> item { Text(s.message, color = MaterialTheme.colorScheme.error) }
                is WeeklySummaryState.Success -> {
                    val summary = s.summary
                    item {
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            StatCard(label = stringResource(R.string.total_sessions), value = summary.totalSessions.toString(), modifier = Modifier.weight(1f))
                            StatCard(label = stringResource(R.string.total_volume), value = "${summary.totalVolumeKg} kg", modifier = Modifier.weight(1f))
                        }
                    }
                    item {
                        StatCard(label = stringResource(R.string.duration_minutes), value = "${summary.totalDurationMinutes} min", modifier = Modifier.fillMaxWidth())
                    }
                    if (summary.sessionsByDay.isNotEmpty()) {
                        item {
                            Spacer(Modifier.height(4.dp))
                            Text(stringResource(R.string.weekly_summary), style = MaterialTheme.typography.titleMedium)
                        }
                        items(summary.sessionsByDay.entries.toList()) { (day, count) ->
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(day, style = MaterialTheme.typography.bodyMedium)
                                Text("$count ${stringResource(R.string.sessions)}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                }
            }

            // Bodyweight section
            item {
                Spacer(Modifier.height(4.dp))
                Text(stringResource(R.string.bodyweight), style = MaterialTheme.typography.titleMedium)
            }

            when (val b = bodyweightState) {
                is BodyweightState.Loading -> item { CircularProgressIndicator(modifier = Modifier.size(24.dp)) }
                is BodyweightState.Error   -> item { Text(b.message, color = MaterialTheme.colorScheme.error) }
                is BodyweightState.Success -> {
                    if (b.entries.isEmpty()) {
                        item { Text(stringResource(R.string.no_bodyweight), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)) }
                    } else {
                        items(b.entries.take(10), key = { it.id }) { entry ->
                            BodyweightCard(entry = entry)
                        }
                    }
                }
            }

            item { Spacer(Modifier.height(80.dp)) }
        }
    }

    if (showAddWeight) {
        AddBodyweightDialog(
            onSave    = { weight, date, notes -> bodyweightViewModel.logWeight(weight, date, notes) },
            onDismiss = { showAddWeight = false }
        )
    }
}

@Composable
private fun StatCard(label: String, value: String, modifier: Modifier = Modifier) {
    Card(modifier = modifier, shape = RoundedCornerShape(12.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(value, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
        }
    }
}