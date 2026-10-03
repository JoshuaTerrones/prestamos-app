package com.josh.prestamos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth

class HomeViewModel(private val dao: PrestamoDao) : ViewModel() {

    val personas: StateFlow<List<PersonaUi>> =
        combine(dao.personas(), dao.pagos()) { personas, pagos ->
            personas.map { p -> construirUi(p, pagos.filter { it.personaId == p.id }) }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun agregarPersona(nombre: String, prestamo: Double, pagoMensual: Double, inicio: LocalDate) =
        viewModelScope.launch {
            dao.insertarPersona(
                Persona(
                    nombre = nombre,
                    prestamo = prestamo,
                    pagoMensual = pagoMensual,
                    fechaPrestamoDia = inicio.toEpochDay()
                )
            )
        }

    fun actualizarPersona(p: Persona) = viewModelScope.launch { dao.actualizarPersona(p) }

    fun registrarPagos(personaId: Long, meses: List<YearMonth>, total: Double) =
        viewModelScope.launch {
            if (meses.isEmpty()) return@launch
            val porMes = Math.round(total / meses.size * 100) / 100.0
            val hoy = LocalDate.now().toEpochDay()
            meses.forEach {
                dao.guardarPago(
                    Pago(personaId = personaId, mes = it.toString(), monto = porMes, fechaPagoDia = hoy)
                )
            }
        }

    fun quitarPago(personaId: Long, mes: YearMonth) =
        viewModelScope.launch { dao.borrarPago(personaId, mes.toString()) }

    fun cancelar(personaId: Long) = viewModelScope.launch { dao.cancelar(personaId) }

    fun eliminar(personaId: Long) = viewModelScope.launch { dao.eliminarPersona(personaId) }
}