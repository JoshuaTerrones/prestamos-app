@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.josh.prestamos

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneOffset

// ================================================================ DETALLE

@Composable
fun DetalleScreen(
    ui: PersonaUi,
    onRegistrar: (List<YearMonth>, Double) -> Unit,
    onQuitar: (YearMonth) -> Unit,
    onCancelar: () -> Unit,
    onEditar: () -> Unit,
    onVolver: () -> Unit
) {
    var hojaAbierta by remember { mutableStateOf(false) }
    var preseleccion by remember { mutableStateOf<YearMonth?>(null) }
    var quitar by remember { mutableStateOf<MesUi?>(null) }
    var confirmarCancelar by remember { mutableStateOf(false) }
    val p = ui.persona

    Scaffold(containerColor = Paleta.Fondo) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            item {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        IconButton(onClick = onVolver, modifier = Modifier.background(Color.White, CircleShape)) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver", tint = Paleta.Morado)
                        }
                        Text(p.nombre, fontSize = 24.sp, fontWeight = FontWeight.Medium, color = Paleta.Texto)
                    }
                    IconButton(onClick = onEditar, modifier = Modifier.background(Color.White, CircleShape)) {
                        Icon(Icons.Default.Edit, "Editar", tint = Paleta.Morado)
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(0.5.dp, Paleta.MoradoClaro)
                ) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            "Préstamo ${soles(p.prestamo)} · ${soles(p.pagoMensual)} al mes",
                            color = Paleta.Gris, fontSize = 15.sp
                        )
                        EstadoEtiqueta(ui)
                        Text("Total cobrado: ${soles(ui.totalCobrado)}", color = Paleta.Gris, fontSize = 14.sp)
                        ui.proximo?.let {
                            Text("Próximo cobro: ${fechaCorta(it.cobro)}", color = Paleta.Gris, fontSize = 14.sp)
                        }
                    }
                }
            }

            if (!p.cancelado) {
                item {
                    Button(
                        onClick = { preseleccion = null; hojaAbierta = true },
                        modifier = Modifier.fillMaxWidth().height(60.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Paleta.Morado)
                    ) {
                        Icon(Icons.Default.Check, null)
                        Spacer(Modifier.width(8.dp))
                        Text("Me pagó", fontSize = 18.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }

            item { Text("Historial", color = Paleta.Gris, fontSize = 14.sp) }

            if (ui.meses.isEmpty()) {
                item { Text("Todavía no hay meses por cobrar.", color = Paleta.Gris) }
            }

            items(ui.meses.reversed(), key = { it.mes.toString() }) { m ->
                Card(
                    onClick = {
                        if (!p.cancelado) {
                            if (m.pago == null) { preseleccion = m.mes; hojaAbierta = true }
                            else quitar = m
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(0.5.dp, Paleta.MoradoClaro)
                ) {
                    Row(
                        Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(etiquetaMes(m.mes), fontSize = 16.sp, fontWeight = FontWeight.Medium, color = Paleta.Texto)
                        if (m.pago == null) {
                            Etiqueta("Debe ${soles(p.pagoMensual)}", Paleta.RojoFondo, Paleta.RojoTexto)
                        } else {
                            val f = fechaCorta(LocalDate.ofEpochDay(m.pago.fechaPagoDia))
                            Etiqueta("Pagó ${soles(m.pago.monto)} · $f", Paleta.VerdeFondo, Paleta.VerdeTexto)
                        }
                    }
                }
            }

            if (!p.cancelado) {
                item {
                    Spacer(Modifier.height(4.dp))
                    OutlinedButton(
                        onClick = { confirmarCancelar = true },
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, Paleta.Morado)
                    ) { Text("Devolvió el préstamo", color = Paleta.Morado, fontWeight = FontWeight.Medium) }
                }
            }
        }
    }

    if (hojaAbierta) {
        MarcarPagosSheet(
            ui = ui,
            preseleccion = preseleccion,
            onGuardar = { meses, total -> onRegistrar(meses, total); hojaAbierta = false },
            onCerrar = { hojaAbierta = false }
        )
    }

    quitar?.let { m ->
        AlertDialog(
            onDismissRequest = { quitar = null },
            title = { Text("¿Quitar el pago de ${etiquetaMes(m.mes)}?") },
            text = { Text("Ese mes volverá a aparecer como pendiente.") },
            confirmButton = {
                TextButton(onClick = { onQuitar(m.mes); quitar = null }) { Text("Quitar pago") }
            },
            dismissButton = { TextButton(onClick = { quitar = null }) { Text("Cancelar") } }
        )
    }

    if (confirmarCancelar) {
        AlertDialog(
            onDismissRequest = { confirmarCancelar = false },
            title = { Text("¿Devolvió el préstamo?") },
            text = { Text("La tarjeta de ${p.nombre} se cierra y el total cobrado se queda guardado.") },
            confirmButton = {
                TextButton(onClick = { onCancelar(); confirmarCancelar = false }) { Text("Sí, cerrar") }
            },
            dismissButton = { TextButton(onClick = { confirmarCancelar = false }) { Text("Cancelar") } }
        )
    }
}

// ================================================================ HOJA "¿QUÉ MESES PAGÓ?"

@Composable
fun MarcarPagosSheet(
    ui: PersonaUi,
    preseleccion: YearMonth?,
    onGuardar: (List<YearMonth>, Double) -> Unit,
    onCerrar: () -> Unit
) {
    val opciones = remember(ui) {
        listOfNotNull(ui.proximo) + ui.meses.filter { it.pago == null }.reversed()
    }
    var marcados by remember { mutableStateOf(setOfNotNull(preseleccion)) }
    var editado by remember { mutableStateOf<String?>(null) }
    var aviso by remember { mutableStateOf<String?>(null) }
    val texto = editado ?: montoTexto(marcados.size * ui.persona.pagoMensual)

    val estadoHoja = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onCerrar,
        sheetState = estadoHoja,
        containerColor = Paleta.Fondo
    ) {
        Column(
            Modifier
                .padding(horizontal = 16.dp)
                .navigationBarsPadding()
                .imePadding()
                .padding(bottom = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text("¿Qué meses pagó?", fontSize = 22.sp, fontWeight = FontWeight.Medium, color = Paleta.Texto)
            Text("Marca los meses que ${ui.persona.nombre} te pagó", color = Paleta.Gris)

            opciones.forEach { m ->
                val activo = m.mes in marcados
                Row(
                    Modifier
                        .fillMaxWidth()
                        .background(if (activo) Paleta.MoradoClaro else Color.White, RoundedCornerShape(12.dp))
                        .toggleable(value = activo, onValueChange = {
                            marcados = if (activo) marcados - m.mes else marcados + m.mes
                            editado = null
                            aviso = null
                        })
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Checkbox(checked = activo, onCheckedChange = null)
                    Column(Modifier.weight(1f)) {
                        Text(etiquetaMes(m.mes), fontWeight = FontWeight.Medium, fontSize = 16.sp, color = Paleta.Texto)
                        if (m.vencido) Text("Debe", color = Paleta.RojoTexto, fontSize = 13.sp)
                        else Text("Próximo · toca el ${fechaCorta(m.cobro)}", color = Paleta.Gris, fontSize = 13.sp)
                    }
                    Text(soles(ui.persona.pagoMensual), color = Paleta.Gris)
                }
            }

            OutlinedTextField(
                value = texto,
                onValueChange = { editado = it; aviso = null },
                label = { Text("Total recibido (S/)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth()
            )
            aviso?.let { Text(it, color = Paleta.RojoTexto) }

            Button(
                onClick = {
                    val total = numero(texto)
                    when {
                        marcados.isEmpty() -> aviso = "Marca al menos un mes"
                        total == null || total <= 0 -> aviso = "Revisa el monto"
                        else -> onGuardar(marcados.sorted(), total)
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Paleta.Morado)
            ) { Text("Guardar pago", fontSize = 17.sp, fontWeight = FontWeight.Medium) }
        }
    }
}

// ================================================================ FORMULARIO (AGREGAR Y EDITAR)

@Composable
fun FormularioPersona(
    titulo: String,
    inicial: Persona?,
    onGuardar: (String, Double, Double, LocalDate) -> Unit,
    onVolver: () -> Unit,
    onEliminar: (() -> Unit)? = null
) {
    var nombre by remember { mutableStateOf(inicial?.nombre.orEmpty()) }
    var prestamo by remember { mutableStateOf(inicial?.let { montoTexto(it.prestamo) }.orEmpty()) }
    var pago by remember { mutableStateOf(inicial?.let { montoTexto(it.pagoMensual) }.orEmpty()) }
    var inicio by remember {
        mutableStateOf(inicial?.let { LocalDate.ofEpochDay(it.fechaPrestamoDia) } ?: LocalDate.now())
    }
    var calendario by remember { mutableStateOf(false) }
    var confirmarEliminar by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    Scaffold(containerColor = Paleta.Fondo) { padding ->
        Column(
            Modifier.padding(padding).padding(16.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(titulo, fontSize = 26.sp, fontWeight = FontWeight.Medium, color = Paleta.Texto)

            OutlinedTextField(
                value = nombre, onValueChange = { nombre = it },
                label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = prestamo, onValueChange = { prestamo = it },
                label = { Text("Préstamo (S/)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = pago, onValueChange = { pago = it },
                label = { Text("Pago al mes (S/)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth()
            )

            Box {
                OutlinedTextField(
                    value = fechaLarga(inicio),
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Fecha de inicio") },
                    trailingIcon = { Icon(Icons.Default.DateRange, null) },
                    supportingText = { Text("Desde cuándo llevas el registro. El primer cobro es un mes después.") },
                    modifier = Modifier.fillMaxWidth()
                )
                Box(Modifier.matchParentSize().clickable { calendario = true })
            }

            error?.let { Text(it, color = Paleta.RojoTexto) }

            Button(
                onClick = {
                    val pr = numero(prestamo)
                    val pg = numero(pago)
                    when {
                        nombre.isBlank() -> error = "Escribe el nombre"
                        pr == null || pr <= 0 -> error = "Revisa el monto del préstamo"
                        pg == null || pg <= 0 -> error = "Revisa el pago al mes"
                        else -> onGuardar(nombre.trim(), pr, pg, inicio)
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Paleta.Morado)
            ) { Text("Guardar", fontSize = 17.sp, fontWeight = FontWeight.Medium) }

            OutlinedButton(
                onClick = onVolver,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Paleta.Morado)
            ) { Text("Cancelar", color = Paleta.Morado) }

            if (onEliminar != null) {
                TextButton(onClick = { confirmarEliminar = true }, modifier = Modifier.fillMaxWidth()) {
                    Text("Eliminar persona", color = Paleta.RojoTexto)
                }
            }
        }
    }

    if (calendario) {
        val estado = rememberDatePickerState(
            initialSelectedDateMillis = inicio.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
        )
        DatePickerDialog(
            onDismissRequest = { calendario = false },
            confirmButton = {
                TextButton(onClick = {
                    estado.selectedDateMillis?.let {
                        inicio = Instant.ofEpochMilli(it).atZone(ZoneOffset.UTC).toLocalDate()
                    }
                    calendario = false
                }) { Text("Listo") }
            },
            dismissButton = { TextButton(onClick = { calendario = false }) { Text("Cancelar") } }
        ) { DatePicker(state = estado) }
    }

    if (confirmarEliminar && onEliminar != null) {
        AlertDialog(
            onDismissRequest = { confirmarEliminar = false },
            title = { Text("¿Eliminar a ${inicial?.nombre.orEmpty()}?") },
            text = { Text("Se borra la persona y todos sus pagos. No se puede deshacer.") },
            confirmButton = {
                TextButton(onClick = { confirmarEliminar = false; onEliminar() }) { Text("Eliminar") }
            },
            dismissButton = { TextButton(onClick = { confirmarEliminar = false }) { Text("Cancelar") } }
        )
    }
}