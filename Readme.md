<p align="center">
  <img src="icons/Default.png" alt="Logo Prestamos" width="128" height="128">
</p>

<h1 align="center">Préstamos App</h1>

<p align="center">
  App Android para gestionar préstamos personales, registrar pagos mensuales y recibir avisos de cobro.
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Android-8.0%2B-3DDC84?logo=android&logoColor=white" alt="Android 8+">
  <img src="https://img.shields.io/badge/Kotlin-2.2.10-7F52FF?logo=kotlin&logoColor=white" alt="Kotlin">
  <img src="https://img.shields.io/badge/Jetpack%20Compose-Material%203-4285F4?logo=jetpackcompose&logoColor=white" alt="Jetpack Compose">
  <img src="https://img.shields.io/badge/Room-2.8.4-orange" alt="Room">
  <img src="https://img.shields.io/badge/WorkManager-2.10.0-5C6BC0" alt="WorkManager">
  <img src="https://img.shields.io/badge/Licencia-MIT-lightgrey" alt="MIT">
</p>

---

## 📌 Descripción

**Préstamos App** permite llevar control de dinero prestado a familiares, amigos o clientes desde el celular:

- Registra personas, monto total, pago mensual y fecha de inicio.
- Calcula automáticamente meses vencidos, pagos adelantados y próximo cobro.
- Guarda historial de pagos por mes.
- Muestra estado de cada préstamo (al día, con atraso o cancelado).
- Envía notificaciones de cobro y retraso.

Toda la información se almacena localmente en el dispositivo con Room (SQLite).

---

## ✨ Funcionalidades principales

- **Alta y edición de personas** con formulario validado.
- **Registro de pagos múltiples** en una sola operación.
- **Ajuste de monto recibido** al registrar pagos.
- **Eliminación de pagos individuales** por mes.
- **Cierre de préstamo** (marca como cancelado sin perder historial).
- **Resumen en home** con:
  - Total cobrado acumulado.
  - Total atrasado pendiente.
- **Notificaciones automáticas**:
  - Aviso el día exacto de cobro.
  - Aviso a los 3 días de atraso si no hay pago.

---

## 🧱 Stack técnico

- **Kotlin**
- **Jetpack Compose + Material 3**
- **Architecture Components** (ViewModel)
- **Room** (persistencia local)
- **KSP** (procesamiento para Room)
- **Coroutines + Flow**
- **WorkManager** (tareas diarias en background)

---

## 🗂️ Estructura del proyecto

```text
prestamos-app/
├── app/
│   ├── src/main/java/com/josh/prestamos/
│   │   ├── MainActivity.kt      # Entry point, navegación y permisos
│   │   ├── HomeScreen.kt        # Pantalla principal y tarjetas
│   │   ├── Pantallas.kt         # Detalle, formulario y hoja de pagos
│   │   ├── HomeViewModel.kt     # Estado y acciones de UI
│   │   ├── Logica.kt            # Cálculos de meses y deuda
│   │   ├── Entidades.kt         # Entidades Room, DAO y DB
│   │   ├── Avisos.kt            # Canal, Worker y notificaciones
│   │   ├── Componentes.kt       # Helpers UI/formatos/colores
│   │   └── ui/theme/            # Tema Compose
│   ├── src/main/res/            # Recursos Android
│   └── build.gradle.kts
├── .github/workflows/
│   └── build-release.yml        # Build release APK por tag v*.*.*
├── icons/
├── gradle/
├── build.gradle.kts
└── Readme.md
```

---

## ✅ Requisitos

- **Android Studio** reciente (recomendado estable actual).
- **JDK 17**.
- **Android SDK min 26** (Android 8.0).

---

## 🚀 Instalación y ejecución

1. Clona el repositorio.
2. Abre el proyecto en Android Studio.
3. Sincroniza Gradle.
4. Ejecuta en emulador o dispositivo físico.

También puedes compilar por terminal:

```bash
./gradlew assembleDebug
```

Para generar release local:

```bash
./gradlew assembleRelease
```

---

## 📱 Flujo de uso

1. En **Home**, toca **Agregar**.
2. Registra nombre, préstamo, pago mensual y fecha de inicio.
3. Abre el detalle y pulsa **Me pagó** para marcar meses cobrados.
4. Si corresponde, ajusta el total recibido antes de guardar.
5. Si hubo error, toca un mes pagado para quitar ese pago.
6. Cuando termine la deuda, usa **Devolvió el préstamo** para cerrarla.

---

## 🔔 Notificaciones

La app programa un `PeriodicWorkRequest` diario (9:00 AM aprox.) para revisar préstamos activos:

- **Hoy toca cobrar**: cuando el mes vence ese día y no está pagado.
- **No ha pagado**: cuando han pasado 3 días desde la fecha de cobro sin pago.

En Android 13+ solicita permiso `POST_NOTIFICATIONS` al iniciar.

---

## 🧪 Testing

Ejecutar unit tests:

```bash
./gradlew test
```

Ejecutar pruebas instrumentadas (requiere dispositivo/emulador):

```bash
./gradlew connectedAndroidTest
```

---

## 📦 CI/CD

Existe un workflow en `.github/workflows/build-release.yml` que:

1. Compila APK release.
2. Publica artefacto del APK.
3. Si el push es un tag `v*.*.*`, crea GitHub Release y adjunta el APK.

---

## 🔒 Privacidad

- Los datos se almacenan en base local del dispositivo (`prestamos.db`).
- No hay backend ni sincronización remota en el estado actual del proyecto.

---

## 📄 Licencia

Este proyecto usa la licencia **MIT**. Ver archivo [LICENSE](LICENSE).

---

## 👤 Autor

**Joshua Terrones**

- GitHub: [@JoshuaTerrones](https://github.com/JoshuaTerrones)
