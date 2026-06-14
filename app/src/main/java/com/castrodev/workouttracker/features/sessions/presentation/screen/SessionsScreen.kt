// Path: app/src/main/java/com/castrodev/workouttracker/features/sessions/presentation/screen/SessionsScreen.kt
package com.castrodev.workouttracker.features.sessions.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.castrodev.workouttracker.R
import com.castrodev.workouttracker.features.sessions.presentation.components.SessionCard
import com.castrodev.workouttracker.features.sessions.presentation.viewmodel.SessionViewModel
import com.castrodev.workouttracker.features.sessions.presentation.viewmodel.SessionsState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SessionsScreen(viewModel: SessionViewModel = viewModel()) {
    val state by viewModel.sessionsState.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text(stringResource(R.string.sessions)) }) }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when (val s = state) {
                is SessionsState.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                is SessionsState.Error   -> Text(s.message, modifier = Modifier.align(Alignment.Center))
                is SessionsState.Success -> {
                    if (s.sessions.isEmpty()) {
                        Text(stringResource(R.string.no_sessions), modifier = Modifier.align(Alignment.Center))
                    } else {
                        LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            items(s.sessions, key = { it.id }) { session ->
                                SessionCard(session = session)
                            }
                        }
                    }
                }
            }
        }
    }
}