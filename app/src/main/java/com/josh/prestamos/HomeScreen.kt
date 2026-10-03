package com.josh.prestamos

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HomeScreen(
    personas: List<PersonaUi>,
    onAgregarPersona: () -> Unit,
    onPersona: (Long) -> Unit
) {
    Scaffold(
        containerColor = Paleta.Fondo,
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onAgregarPersona,
                containerColor = Paleta.Morado,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Agregar", fontWeight = FontWeight.Medium, fontSize = 16.sp)
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp)
        ) {
            item {
                Text("Tus préstamos", fontSize = 26.sp, fontWeight = FontWeight.Medium, color = Paleta.Texto)
            }
            item { Resumen(personas) }
            if (personas.isEmpty()) {
                item {
                    Text(
                        "Empieza con tu primer préstamo. Toca Agregar.",
                        color = Paleta.Gris,
                        fontSize = 16.sp
                    )
                }
            }
            items(personas, key = { it.persona.id }) { p ->
                PersonaCard(p) { onPersona(p.persona.id) }
            }
        }
    }
}

@Composable
private fun Resumen(personas: List<PersonaUi>) {
    Row(
        Modifier.fillMaxWidth().background(Paleta.Morado, RoundedCornerShape(20.dp)).padding(18.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text("Total cobrado", color = Paleta.MoradoClaro, fontSize = 13.sp)
            Text(
                soles(personas.sumOf { it.totalCobrado }),
                color = Color.White, fontSize = 30.sp, fontWeight = FontWeight.Medium
            )
        }
        Column(horizontalAlignment = Alignment.End) {
            Text("Atrasado", color = Paleta.MoradoClaro, fontSize = 13.sp)
            Text(
                soles(personas.sumOf { it.debe }),
                color = Color.White, fontSize = 30.sp, fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun PersonaCard(ui: PersonaUi, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(0.5.dp, Paleta.MoradoClaro)
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Avatar(ui.persona.nombre)
                Text(ui.persona.nombre, fontSize = 20.sp, fontWeight = FontWeight.Medium, color = Paleta.Texto)
            }
            EstadoEtiqueta(ui)
            Text(
                "Préstamo ${soles(ui.persona.prestamo)} · ${soles(ui.persona.pagoMensual)} al mes",
                color = Paleta.Gris, fontSize = 15.sp
            )
            ui.proximo?.let {
                Text("Próximo cobro: ${fechaCorta(it.cobro)}", color = Paleta.Gris, fontSize = 14.sp)
            }
        }
    }
}