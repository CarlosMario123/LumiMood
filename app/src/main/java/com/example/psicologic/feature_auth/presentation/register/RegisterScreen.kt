// RegisterScreen.kt
package com.example.psicologic.feature_auth.presentation.register

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.psicologic.core.di.ServiceLocator
import com.example.psicologic.core.di.ViewModelFactory
import com.example.psicologic.core.presentation.components.PsicologicButton

/**
 * Pantalla de registro de usuario
 *
 * @param onNavigateToLogin Callback para navegar a la pantalla de login
 * @param onRegisterSuccess Callback para indicar que el registro fue exitoso
 */
@Composable
fun RegisterScreen(
    onNavigateToLogin: () -> Unit,
    onRegisterSuccess: () -> Unit
) {
    // Obtener el contexto dentro del cuerpo de la función composable
    val context = LocalContext.current

    // Crear el ViewModel con el contexto
    val viewModel = viewModel<RegisterViewModel>(
        factory = ViewModelFactory {
            ServiceLocator.provideRegisterViewModel(context)
        }
    )

    val uiState by viewModel.uiState.collectAsState()
    val name by viewModel.name.collectAsState()
    val email by viewModel.email.collectAsState()
    val password by viewModel.password.collectAsState()
    val confirmPassword by viewModel.confirmPassword.collectAsState()

    val focusManager = LocalFocusManager.current
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    // Efecto para manejar registro exitoso
    LaunchedEffect(uiState) {
        if (uiState is RegisterUiState.Success) {
            onRegisterSuccess()
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Título
            Text(
                text = "Crear Cuenta",
                style = MaterialTheme.typography.h5,
                modifier = Modifier.padding(vertical = 24.dp)
            )

            // Campo de nombre
            OutlinedTextField(
                value = name,
                onValueChange = viewModel::onNameChanged,
                label = { Text("Nombre completo") },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                ),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )

            // Campo de email
            OutlinedTextField(
                value = email,
                onValueChange = viewModel::onEmailChanged,
                label = { Text("Correo electrónico") },
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                ),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )

            // Campo de contraseña
            OutlinedTextField(
                value = password,
                onValueChange = viewModel::onPasswordChanged,
                label = { Text("Contraseña") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña"
                        )
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                ),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )

            // Campo de confirmar contraseña
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = viewModel::onConfirmPasswordChanged,
                label = { Text("Confirmar contraseña") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                trailingIcon = {
                    IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                        Icon(
                            imageVector = if (confirmPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = if (confirmPasswordVisible) "Ocultar contraseña" else "Mostrar contraseña"
                        )
                    }
                },
                visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        focusManager.clearFocus()
                        viewModel.register()
                    }
                ),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
            )

            // Botón de registro
            PsicologicButton(
                text = "Registrarse",
                onClick = { viewModel.register() },
                isLoading = uiState is RegisterUiState.Loading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            )

            // Enlace para ir a login
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("¿Ya tienes una cuenta?")
                TextButton(onClick = onNavigateToLogin) {
                    Text("Iniciar sesión")
                }
            }

            // Mensaje de error
            if (uiState is RegisterUiState.Error) {
                Text(
                    text = (uiState as RegisterUiState.Error).message,
                    color = Color.Red,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                )
            }
        }

        // Indicador de carga
        if (uiState is RegisterUiState.Loading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }
}