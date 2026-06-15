// Path: app/src/main/java/com/castrodev/workouttracker/features/profile/presentation/screen/ProfileScreen.kt
package com.castrodev.workouttracker.features.profile.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.castrodev.workouttracker.R
import com.castrodev.workouttracker.features.auth.presentation.viewmodel.AuthViewModel
import com.castrodev.workouttracker.features.profile.domain.entity.ProfileEntity
import com.castrodev.workouttracker.features.profile.presentation.viewmodel.ProfileState
import com.castrodev.workouttracker.features.profile.presentation.viewmodel.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    authViewModel: AuthViewModel,
    onLogout: () -> Unit,
    profileViewModel: ProfileViewModel = viewModel()
) {
    val state by profileViewModel.state.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text(stringResource(R.string.profile)) }) }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when (val s = state) {
                is ProfileState.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                is ProfileState.Error   -> Text(s.message, modifier = Modifier.align(Alignment.Center))
                is ProfileState.Success -> ProfileContent(
                    profile  = s.profile,
                    onUpdate = { name, lang, notifs -> profileViewModel.update(name, lang, notifs) },
                    onLogout = { authViewModel.signOut(); onLogout() }
                )
            }
        }
    }
}

@Composable
private fun ProfileContent(
    profile: ProfileEntity,
    onUpdate: (String, String, Boolean) -> Unit,
    onLogout: () -> Unit
) {
    var showEditDialog by remember { mutableStateOf(false) }
    var notifications  by remember { mutableStateOf(profile.notifications) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Avatar
        Card(modifier = Modifier.fillMaxWidth()) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(64.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text       = profile.name.firstOrNull()?.uppercaseChar()?.toString() ?: "?",
                        style      = MaterialTheme.typography.headlineMedium,
                        color      = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(Modifier.width(16.dp))
                Column {
                    Text(profile.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(profile.email, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        Card(modifier = Modifier.fillMaxWidth()) {
            Column {
                ListItem(
                    headlineContent = { Text(stringResource(R.string.edit_profile)) },
                    leadingContent  = { Icon(Icons.Default.Person, null) },
                    trailingContent = { Icon(Icons.Default.ChevronRight, null) },
                    modifier        = Modifier.clickable { showEditDialog = true }
                )
                HorizontalDivider()
                ListItem(
                    headlineContent    = { Text(stringResource(R.string.language)) },
                    supportingContent  = { Text(stringResource(R.string.language_hint), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)) },
                    leadingContent     = { Icon(Icons.Default.Language, null) }
                )
                HorizontalDivider()
                ListItem(
                    headlineContent = { Text(stringResource(R.string.notifications)) },
                    leadingContent  = { Icon(Icons.Default.Notifications, null) },
                    trailingContent = {
                        Switch(
                            checked         = notifications,
                            onCheckedChange = {
                                notifications = it
                                onUpdate(profile.name, profile.language, it)
                            }
                        )
                    }
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        Card(modifier = Modifier.fillMaxWidth()) {
            ListItem(
                headlineContent = { Text(stringResource(R.string.sign_out), color = MaterialTheme.colorScheme.error) },
                leadingContent  = { Icon(Icons.Default.Logout, null, tint = MaterialTheme.colorScheme.error) },
                modifier        = Modifier.clickable { onLogout() }
            )
        }
    }

    if (showEditDialog) {
        EditProfileDialog(
            currentName = profile.name,
            onSave      = { name -> onUpdate(name, profile.language, profile.notifications); showEditDialog = false },
            onDismiss   = { showEditDialog = false }
        )
    }
}

@Composable
private fun EditProfileDialog(
    currentName: String,
    onSave: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf(currentName) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title            = { Text(stringResource(R.string.edit_profile)) },
        text             = {
            OutlinedTextField(
                value         = name,
                onValueChange = { name = it },
                label         = { Text(stringResource(R.string.name)) },
                singleLine    = true,
                modifier      = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            TextButton(onClick = { onSave(name) }, enabled = name.isNotBlank()) {
                Text(stringResource(R.string.save))
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel)) } }
    )
}