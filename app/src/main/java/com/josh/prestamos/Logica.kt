package com.josh.prestamos

import java.time.LocalDate
import java.time.YearMonth

data class MesUi(
    val mes: YearMonth,
    val cobro: LocalDate,
    val pago: Pago?,
    val vencido: Boolean
)

data class PersonaUi(
    val persona: Persona,
    val meses: List<MesUi>,   // vencidos + adelantados ya pagados, de antiguo a nuevo
    val proximo: MesUi?,      // primer mes futuro sin pagar
    val totalCobrado: Double
) {
    val mesesDebe get() =
        if (persona.cancelado) 0 else meses.count { it.vencido && it.pago == null }
    val debe get() = mesesDebe * persona.pagoMensual
}

fun construirUi(p: Persona, pagos: List<Pago>, hoy: LocalDate = LocalDate.now()): PersonaUi {
    val inicio = LocalDate.ofEpochDay(p.fechaPrestamoDia)
    val pagoPorMes = pagos.associateBy { it.mes }

    fun mesUi(n: Long): MesUi {
        val cobro = inicio.plusMonths(n)
        val ym = YearMonth.from(cobro)
        return MesUi(ym, cobro, pagoPorMes[ym.toString()], !cobro.isAfter(hoy))
    }

    val todos = generateSequence(1L) { it + 1 }.map { mesUi(it) }
    val vencidos = todos.takeWhile { it.vencido }.toList()
    val futuros = todos.dropWhile { it.vencido }.take(24).toList()
    val adelantados = futuros.filter { it.pago != null }
    val proximo = if (p.cancelado) null else futuros.firstOrNull { it.pago == null }

    return PersonaUi(p, vencidos + adelantados, proximo, pagos.sumOf { it.monto })
}