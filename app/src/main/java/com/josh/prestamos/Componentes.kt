package com.josh.prestamos

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

object Paleta {
    val Morado = Color(0xFF534AB7)
    val MoradoClaro = Color(0xFFCECBF6)
    val MoradoOscuro = Color(0xFF3C3489)
    val Fondo = Color(0xFFEEEDFE)
    val Texto = Color(0xFF26215C)
    val Gris = Color(0xFF5F5E5A)
    val RojoFondo = Color(0xFFFCEBEB)
    val RojoTexto = Color(0xFF791F1F)
    val VerdeFondo = Color(0xFFEAF3DE)
    val VerdeTexto = Color(0xFF27500A)
    val GrisFondo = Color(0xFFF1EFE8)
    val GrisTexto = Color(0xFF444441)
}

val ES: Locale = Locale.forLanguageTag("es-PE")

fun soles(x: Double) = if (x % 1.0 == 0.0) "S/ %.0f".format(x) else "S/ %.2f".format(x)
fun montoTexto(x: Double) = if (x % 1.0 == 0.0) "%.0f".format(x) else "%.2f".format(x)
fun numero(s: String) = s.trim().replace(',', '.').toDoubleOrNull()

fun etiquetaMes(m: YearMonth): String =
    m.month.getDisplayName(TextStyle.FULL, ES).replaceFirstChar { it.uppercase() } + " " + m.year

fun fechaCorta(d: LocalDate): String =
    d.format(DateTimeFormatter.ofPattern("d MMM", ES)).replace(".", "")

fun fechaLarga(d: LocalDate): String =
    d.format(DateTimeFormatter.ofPattern("d 'de' MMMM yyyy", ES))

@Composable
fun Etiqueta(texto: String, fondo: Color, color: Color) {
    Text(
        texto,
        color = color,
        fontSize = 13.sp,
        fontWeight = FontWeight.Medium,
        modifier = Modifier
            .background(fondo, RoundedCornerShape(50))
            .padding(horizontal = 12.dp, vertical = 5.dp)
    )
}

@Composable
fun EstadoEtiqueta(ui: PersonaUi) {
    when {
        ui.persona.cancelado -> Etiqueta("Préstamo cancelado", Paleta.GrisFondo, Paleta.GrisTexto)
        ui.mesesDebe == 0 -> Etiqueta("Al día", Paleta.VerdeFondo, Paleta.VerdeTexto)
        ui.mesesDebe == 1 -> Etiqueta("Debe 1 mes · ${soles(ui.debe)}", Paleta.RojoFondo, Paleta.RojoTexto)
        else -> Etiqueta("Debe ${ui.mesesDebe} meses · ${soles(ui.debe)}", Paleta.RojoFondo, Paleta.RojoTexto)
    }
}

@Composable
fun Avatar(nombre: String) {
    Box(
        Modifier.size(40.dp).background(Paleta.MoradoClaro, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(nombre.take(1).uppercase(), color = Paleta.MoradoOscuro, fontWeight = FontWeight.Medium)
    }
}