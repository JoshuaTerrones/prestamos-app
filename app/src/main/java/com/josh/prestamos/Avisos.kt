package com.josh.prestamos

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.*
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

object Avisos {
    private const val CANAL = "cobros"

    fun crearCanal(context: Context) {
        val canal = NotificationChannel(CANAL, "Cobros", NotificationManager.IMPORTANCE_HIGH)
        canal.description = "Avisos del día de cobro y de pagos atrasados"
        context.getSystemService(NotificationManager::class.java).createNotificationChannel(canal)
    }

    @SuppressLint("MissingPermission")
    fun mostrar(context: Context, id: Int, titulo: String, texto: String) {
        if (Build.VERSION.SDK_INT >= 33 &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) return

        val abrir = PendingIntent.getActivity(
            context, 0, Intent(context, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val aviso = NotificationCompat.Builder(context, CANAL)
            .setSmallIcon(android.R.drawable.ic_popup_reminder)
            .setContentTitle(titulo)
            .setContentText(texto)
            .setContentIntent(abrir)
            .setAutoCancel(true)
            .build()
        NotificationManagerCompat.from(context).notify(id, aviso)
    }

    fun programar(context: Context) {
        val ahora = LocalDateTime.now()
        var proxima = ahora.toLocalDate().atTime(9, 0)
        if (!proxima.isAfter(ahora)) proxima = proxima.plusDays(1)

        val pedido = PeriodicWorkRequestBuilder<AvisosWorker>(1, TimeUnit.DAYS)
            .setInitialDelay(Duration.between(ahora, proxima))
            .build()
        WorkManager.getInstance(context)
            .enqueueUniquePeriodicWork("avisos_diarios", ExistingPeriodicWorkPolicy.UPDATE, pedido)
    }
}

class AvisosWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val dao = Bd.get(applicationContext).dao()
        val pagos = dao.pagosUnaVez()
        val hoy = LocalDate.now()

        dao.personasUnaVez().filter { !it.cancelado }.forEach { p ->
            val ui = construirUi(p, pagos.filter { it.personaId == p.id }, hoy)

            ui.meses.firstOrNull { it.cobro == hoy && it.pago == null }?.let {
                Avisos.mostrar(
                    applicationContext, (p.id * 10 + 1).toInt(),
                    "Hoy toca cobrar", "${p.nombre} · ${soles(p.pagoMensual)}"
                )
            }

            ui.meses.firstOrNull { it.cobro == hoy.minusDays(3) && it.pago == null }?.let {
                Avisos.mostrar(
                    applicationContext, (p.id * 10 + 2).toInt(),
                    "${p.nombre} no ha pagado", "Falta el pago de ${etiquetaMes(it.mes)}"
                )
            }
        }
        return Result.success()
    }
}