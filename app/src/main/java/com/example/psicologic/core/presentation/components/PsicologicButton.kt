package com.example.psicologic.core.presentation.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Botón personalizado para la aplicación Psicologic
 *
 * @param text Texto a mostrar en el botón
 * @param onClick Acción a realizar al hacer clic
 * @param modifier Modificador para personalizar el botón
 * @param isLoading Indica si el botón está en estado de carga
 * @param enabled Indica si el botón está habilitado
 * @param contentPadding Padding interno del contenido del botón
 * @param backgroundColor Color de fondo del botón
 * @param contentColor Color del texto y el indicador de carga
 */
@Composable
fun PsicologicButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    enabled: Boolean = true,
    contentPadding: PaddingValues = PaddingValues(vertical = 12.dp, horizontal = 16.dp),
    backgroundColor: Color = MaterialTheme.colors.primary,
    contentColor: Color = Color.White
) {
    // Calcular si el botón debe estar activo (no está cargando y está habilitado)
    val isActive = !isLoading && enabled

    Button(
        onClick = { if (isActive) onClick() },
        modifier = modifier.animateContentSize(),
        enabled = isActive,
        shape = RoundedCornerShape(8.dp),
        contentPadding = contentPadding,
        colors = ButtonDefaults.buttonColors(
            backgroundColor = backgroundColor,
            contentColor = contentColor,
            disabledBackgroundColor = if (isLoading) backgroundColor.copy(alpha = 0.7f) else MaterialTheme.colors.onSurface.copy(alpha = 0.12f),
            disabledContentColor = if (isLoading) contentColor.copy(alpha = 0.7f) else MaterialTheme.colors.onSurface.copy(alpha = 0.38f)
        ),
        elevation = ButtonDefaults.elevation(
            defaultElevation = 2.dp,
            pressedElevation = 4.dp,
            disabledElevation = 0.dp
        )
    ) {
        Box(
            contentAlignment = Alignment.Center
        ) {
            // Mostrar el texto si no está cargando
            if (!isLoading) {
                Text(
                    text = text,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp
                )
            }
            // Mostrar el indicador de carga si está cargando
            else {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = contentColor,
                    strokeWidth = 2.dp
                )
            }
        }
    }
}