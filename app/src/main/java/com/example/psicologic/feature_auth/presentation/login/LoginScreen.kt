// LoginScreen.kt
package com.example.psicologic.feature_auth.presentation.login
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
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
 * Pantalla de inicio de sesión
 *
 * @param onNavigateToRegister Callback para navegar a la pantalla de registro
 * @param onLoginSuccess Callback para indicar que el login fue exitoso
 */
@Composable
fun LoginScreen(
onNavigateToRegister: () -> Unit,
onLoginSuccess: () -> Unit
) {
    // Obtener el contexto dentro del cuerpo de la función composable
    val context = LocalContext.current

    // Crear el ViewModel dentro del cuerpo de la función composable
    val viewModel = viewModel<LoginViewModel>(
        factory = ViewModelFactory {
            ServiceLocator.provideLoginViewModel(context)
        }
    )

    val uiState by viewModel.uiState.collectAsState()
    val email by viewModel.email.collectAsState()
    val password by viewModel.password.collectAsState()

    val focusManager = LocalFocusManager.current
    var passwordVisible by remember { mutableStateOf(false) }

    LaunchedEffect(uiState) {
        if (uiState is LoginUiState.Success) {
            onLoginSuccess()
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
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo con icono circular
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.padding(bottom = 24.dp)
            ) {
                // Fondo circular
                Surface(
                    color = MaterialTheme.colors.primary.copy(alpha = 0.1f),
                    shape = CircleShape,
                    modifier = Modifier.size(120.dp)
                ) {}
                // Icono grande centrado
                Icon(
                    imageVector = Icons.Default.Psychology, // Icono de psicología
                    contentDescription = "App Logo",
                    tint = MaterialTheme.colors.primary,
                    modifier = Modifier.size(72.dp)
                )
            }

            // Título
            Text(
                text = "Iniciar Sesión",
                style = MaterialTheme.typography.h5,
                modifier = Modifier.padding(bottom = 24.dp)
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
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        focusManager.clearFocus()
                        viewModel.login()
                    }
                ),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )

            // ¿Olvidaste tu contraseña?
            TextButton(
                onClick = { /* Manejar olvidó contraseña */ },
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(bottom = 24.dp)
            ) {
                Text("¿Olvidaste tu contraseña?")
            }

            // Botón de login
            PsicologicButton(
                text = "Iniciar Sesión",
                onClick = { viewModel.login() },
                isLoading = uiState is LoginUiState.Loading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            )

            // Enlace para registrarse
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("¿No tienes una cuenta?")
                TextButton(onClick = onNavigateToRegister) {
                    Text("Registrarse")
                }
            }

            // Mensaje de error
            if (uiState is LoginUiState.Error) {
                Text(
                    text = (uiState as LoginUiState.Error).message,
                    color = Color.Red,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                )
            }
        }

        // Indicador de carga
        if (uiState is LoginUiState.Loading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }
}