package com.josh.prestamos.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val EsquemaMorado = lightColorScheme(
    primary = Color(0xFF534AB7),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFCECBF6),
    onPrimaryContainer = Color(0xFF26215C),
    secondary = Color(0xFF7F77DD),
    background = Color(0xFFEEEDFE),
    onBackground = Color(0xFF26215C),
    surface = Color(0xFFEEEDFE),
    onSurface = Color(0xFF26215C),
    surfaceVariant = Color(0xFFCECBF6),
    onSurfaceVariant = Color(0xFF3C3489),
    outline = Color(0xFFAFA9EC),
    surfaceContainerHigh = Color.White
)

@Composable
fun PrestamosTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = EsquemaMorado, typography = Typography, content = content)
}