// Path: app/src/main/java/com/castrodev/workouttracker/features/auth/presentation/screen/LoginScreen.kt
package com.castrodev.workouttracker.features.auth.presentation.screen

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.castrodev.workouttracker.R
import com.castrodev.workouttracker.features.auth.presentation.viewmodel.AuthState
import com.castrodev.workouttracker.features.auth.presentation.viewmodel.AuthViewModel

@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    onNavigateToRegister: () -> Unit,
    onLoginSuccess: () -> Unit
) {
    val state    by viewModel.state.collectAsState()
    val darkTheme = isSystemInDarkTheme()
    var email    by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val textColor    = if (darkTheme) Color.White else Color.Black
    val subtitleColor = if (darkTheme) Color.White.copy(alpha = 0.6f) else Color.Black.copy(alpha = 0.6f)

    LaunchedEffect(state) {
        if (state is AuthState.Success) onLoginSuccess()
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(stringResource(R.string.app_name), fontSize = 32.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.height(8.dp))
        Text(stringResource(R.string.login_subtitle), style = MaterialTheme.typography.bodyMedium, color = subtitleColor)
        Spacer(Modifier.height(40.dp))

        OutlinedTextField(
            value         = email,
            onValueChange = { email = it },
            label         = { Text(stringResource(R.string.email)) },
            modifier      = Modifier.fillMaxWidth(),
            singleLine    = true,
            colors        = OutlinedTextFieldDefaults.colors(
                focusedTextColor   = textColor,
                unfocusedTextColor = textColor
            )
        )
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value                = password,
            onValueChange        = { password = it },
            label                = { Text(stringResource(R.string.password)) },
            modifier             = Modifier.fillMaxWidth(),
            singleLine           = true,
            visualTransformation = PasswordVisualTransformation(),
            colors               = OutlinedTextFieldDefaults.colors(
                focusedTextColor   = textColor,
                unfocusedTextColor = textColor
            )
        )
        Spacer(Modifier.height(24.dp))

        if (state is AuthState.Error) {
            Text((state as AuthState.Error).message, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            Spacer(Modifier.height(8.dp))
        }

        Button(
            onClick  = { viewModel.signIn(email, password) },
            enabled  = email.isNotBlank() && password.isNotBlank() && state !is AuthState.Loading,
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors   = ButtonDefaults.buttonColors(
                containerColor         = MaterialTheme.colorScheme.primary,
                contentColor           = Color.White,
                disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                disabledContentColor   = Color.White.copy(alpha = 0.7f)
            )
        ) {
            if (state is AuthState.Loading) CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp, color = Color.White)
            else Text(stringResource(R.string.sign_in))
        }
        Spacer(Modifier.height(16.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(stringResource(R.string.no_account), style = MaterialTheme.typography.bodyMedium, color = subtitleColor)
            TextButton(onClick = onNavigateToRegister) { Text(stringResource(R.string.register)) }
        }
    }
}