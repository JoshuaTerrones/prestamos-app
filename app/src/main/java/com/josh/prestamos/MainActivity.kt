package com.josh.prestamos

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.*
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.josh.prestamos.ui.theme.PrestamosTheme

sealed interface Pantalla {
    data object Home : Pantalla
    data object Agregar : Pantalla
    data class Detalle(val id: Long) : Pantalla
    data class Editar(val id: Long) : Pantalla
}

class MainActivity : ComponentActivity() {

    private val db by lazy { Bd.get(this) }

    private val vm: HomeViewModel by viewModels {
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T =
                HomeViewModel(db.dao()) as T
        }
    }

    private val pedirPermiso =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Avisos.crearCanal(this)
        Avisos.programar(this)
        if (Build.VERSION.SDK_INT >= 33 &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            pedirPermiso.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

        setContent {
            PrestamosTheme {
                val personas by vm.personas.collectAsState()
                var pantalla by remember { mutableStateOf<Pantalla>(Pantalla.Home) }

                BackHandler(enabled = pantalla != Pantalla.Home) {
                    pantalla = when (val actual = pantalla) {
                        is Pantalla.Editar -> Pantalla.Detalle(actual.id)
                        else -> Pantalla.Home
                    }
                }

                when (val p = pantalla) {
                    Pantalla.Home -> HomeScreen(
                        personas = personas,
                        onAgregarPersona = { pantalla = Pantalla.Agregar },
                        onPersona = { pantalla = Pantalla.Detalle(it) }
                    )

                    Pantalla.Agregar -> FormularioPersona(
                        titulo = "Nueva persona",
                        inicial = null,
                        onGuardar = { nombre, prestamo, pago, inicio ->
                            vm.agregarPersona(nombre, prestamo, pago, inicio)
                            pantalla = Pantalla.Home
                        },
                        onVolver = { pantalla = Pantalla.Home }
                    )

                    is Pantalla.Detalle -> {
                        val ui = personas.find { it.persona.id == p.id }
                        if (ui != null) {
                            DetalleScreen(
                                ui = ui,
                                onRegistrar = { meses, total -> vm.registrarPagos(p.id, meses, total) },
                                onQuitar = { mes -> vm.quitarPago(p.id, mes) },
                                onCancelar = { vm.cancelar(p.id) },
                                onEditar = { pantalla = Pantalla.Editar(p.id) },
                                onVolver = { pantalla = Pantalla.Home }
                            )
                        } else {
                            LaunchedEffect(Unit) { pantalla = Pantalla.Home }
                        }
                    }

                    is Pantalla.Editar -> {
                        val ui = personas.find { it.persona.id == p.id }
                        if (ui != null) {
                            FormularioPersona(
                                titulo = "Editar",
                                inicial = ui.persona,
                                onGuardar = { nombre, prestamo, pago, inicio ->
                                    vm.actualizarPersona(
                                        ui.persona.copy(
                                            nombre = nombre,
                                            prestamo = prestamo,
                                            pagoMensual = pago,
                                            fechaPrestamoDia = inicio.toEpochDay()
                                        )
                                    )
                                    pantalla = Pantalla.Detalle(p.id)
                                },
                                onVolver = { pantalla = Pantalla.Detalle(p.id) },
                                onEliminar = { vm.eliminar(p.id); pantalla = Pantalla.Home }
                            )
                        } else {
                            LaunchedEffect(Unit) { pantalla = Pantalla.Home }
                        }
                    }
                }
            }
        }
    }
}