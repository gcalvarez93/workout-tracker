// Path: app/src/main/java/com/castrodev/workouttracker/features/analytics/presentation/screen/AnalyticsScreen.kt
package com.castrodev.workouttracker.features.analytics.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen(viewModel: AnalyticsViewModel = viewModel()) {
    val summaryState by viewModel.summaryState.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text(stringResource(R.string.analytics)) }) }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            // Week selector
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = { viewModel.previousWeek() }) { Icon(Icons.Default.ChevronLeft, null) }
                Text(
                    text  = "${stringResource(R.string.week)} ${viewModel.selectedWeek} · ${viewModel.selectedYear}",
                    style = MaterialTheme.typography.titleMedium
                )
                IconButton(onClick = { viewModel.nextWeek() }) { Icon(Icons.Default.ChevronRight, null) }
            }

            when (val s = summaryState) {
                is WeeklySummaryState.Loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
                is WeeklySummaryState.Error   -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text(s.message) }
                is WeeklySummaryState.Success -> {
                    val summary = s.summary
                    Column(
                        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Stats cards
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            StatCard(
                                label = stringResource(R.string.total_sessions),
                                value = summary.totalSessions.toString(),
                                modifier = Modifier.weight(1f)
                            )
                            StatCard(
                                label = stringResource(R.string.total_volume),
                                value = "${summary.totalVolumeKg} kg",
                                modifier = Modifier.weight(1f)
                            )
                        }
                        StatCard(
                            label = stringResource(R.string.duration_minutes),
                            value = "${summary.totalDurationMinutes} min",
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Sessions by day
                        if (summary.sessionsByDay.isNotEmpty()) {
                            Spacer(Modifier.height(8.dp))
                            Text(stringResource(R.string.weekly_summary), style = MaterialTheme.typography.titleMedium)
                            summary.sessionsByDay.forEach { (day, count) ->
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(day, style = MaterialTheme.typography.bodyMedium)
                                    Text("$count ${stringResource(R.string.sessions)}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
                                }
                            }
                        }
                    }
                }
            }
        }
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