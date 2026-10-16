<p align="center">
  <img src="icons/Default.png" alt="Logo Prestamos" width="128" height="128">
</p>

<h1 align="center">Prestamos App</h1>

<p align="center">
  Aplicación Android para gestionar préstamos personales, registrar pagos y recibir avisos de cobro.
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Android-8.0%20(API%2026)%2B-3DDC84?logo=android&logoColor=white" alt="Android 8+">
  <img src="https://img.shields.io/badge/Kotlin-2.2.10-7F52FF?logo=kotlin&logoColor=white" alt="Kotlin">
  <img src="https://img.shields.io/badge/Jetpack%20Compose-Material%203-4285F4?logo=jetpackcompose&logoColor=white" alt="Jetpack Compose">
  <img src="https://img.shields.io/badge/Room-2.8.4-orange" alt="Room">
  <img src="https://img.shields.io/badge/WorkManager-2.10.0-brightgreen" alt="WorkManager">
  <img src="https://img.shields.io/badge/Licencia-MIT-lightgrey" alt="MIT">
</p>

---

## Tabla de contenido

- [Descripción](#descripción)
- [Características](#características)
- [Stack técnico](#stack-técnico)
- [Arquitectura y flujo](#arquitectura-y-flujo)
- [Estructura del proyecto](#estructura-del-proyecto)
- [Requisitos](#requisitos)
- [Configuración y ejecución](#configuración-y-ejecución)
- [Uso de la app](#uso-de-la-app)
- [Notificaciones](#notificaciones)
- [Build y release](#build-y-release)
- [Datos de prueba](#datos-de-prueba)
- [Roadmap sugerido](#roadmap-sugerido)
- [Licencia](#licencia)
- [Autor](#autor)

---

## Descripción

**Prestamos App** ayuda a llevar control de préstamos entre personas de manera simple y visual.

Permite:
- registrar monto prestado y cuota mensual,
- calcular meses vencidos y próximos cobros según fecha de inicio,
- guardar pagos por mes,
- marcar préstamos como cancelados,
- recibir recordatorios automáticos de cobro y atraso.

Toda la información se guarda localmente en el dispositivo mediante Room (SQLite).

---

## Características

- Alta, edición y eliminación de personas con préstamo.
- Historial de meses con estado de pago.
- Registro de uno o varios meses en un solo pago.
- Eliminación de pagos registrados por error.
- Estado resumido por persona:
  - **Al día**
  - **Debe X meses**
  - **Préstamo cancelado**
- Resumen global en home:
  - total cobrado,
  - monto atrasado acumulado.
- Recordatorios diarios con WorkManager:
  - aviso cuando toca cobrar,
  - aviso cuando ya pasaron 3 días sin pago.

---

## Stack técnico

| Tecnología | Uso |
|---|---|
| Kotlin | Lenguaje principal |
| Jetpack Compose | UI declarativa |
| Material 3 | Componentes y estilos |
| AndroidX ViewModel + Flow | Estado de UI y reactividad |
| Room | Persistencia local en SQLite |
| KSP | Procesamiento de anotaciones para Room |
| WorkManager | Programación de notificaciones |
| Java Time API | Cálculos de fecha y meses |

---

## Arquitectura y flujo

- **UI (Compose):** `HomeScreen`, `DetalleScreen`, `FormularioPersona`, `MarcarPagosSheet`.
- **Estado y acciones:** `HomeViewModel`.
- **Dominio/UI model:** `PersonaUi`, `MesUi` y función `construirUi`.
- **Datos:** `PrestamoDao` + entidades `Persona` y `Pago`.
- **Persistencia:** `AppDb` (Room).
- **Recordatorios:** `Avisos` + `AvisosWorker` (WorkManager periódico diario).

Flujo general:
1. La app carga personas y pagos desde Room.
2. El ViewModel construye el estado de cada préstamo.
3. La UI presenta el resumen y permite registrar/quitar pagos.
4. WorkManager ejecuta una revisión diaria para enviar notificaciones.

---

## Estructura del proyecto

```text
prestamos-app/
├── app/
│   ├── src/main/java/com/josh/prestamos/
│   │   ├── MainActivity.kt      # Entrada principal y navegación de pantallas
│   │   ├── HomeScreen.kt        # Pantalla principal y tarjetas de préstamos
│   │   ├── Pantallas.kt         # Detalle, formulario y hoja de registro de pagos
│   │   ├── HomeViewModel.kt     # Estado y operaciones de negocio
│   │   ├── Logica.kt            # Cálculo de meses, deudas y totales
│   │   ├── Entidades.kt         # Entidades Room, DAO y base de datos
│   │   ├── Avisos.kt            # Canal, programación y worker de notificaciones
│   │   ├── Componentes.kt       # Formatos, helpers y componentes visuales
│   │   └── ui/theme/            # Tema de Compose
│   ├── src/main/res/            # Recursos Android
│   └── build.gradle.kts         # Configuración del módulo app
├── .github/workflows/
│   └── build-release.yml        # Pipeline de build/release de APK por tags
├── icons/                       # Recursos gráficos del proyecto
├── gradle/libs.versions.toml    # Versiones centralizadas
└── Readme.md
```

---

## Requisitos

- Android Studio reciente (con soporte para AGP 9+ y Kotlin 2.2+).
- JDK 17 recomendado.
- SDK Android con:
  - `compileSdk = 37`
  - `targetSdk = 37`
  - `minSdk = 26`

---

## Configuración y ejecución

### 1) Clonar el repositorio

```bash
git clone https://github.com/JoshuaTerrones/prestamos-app.git
cd prestamos-app
```

### 2) Compilar en modo debug

```bash
./gradlew assembleDebug
```

### 3) Ejecutar pruebas

```bash
./gradlew test
```

### 4) Generar APK release

```bash
./gradlew assembleRelease
```

El APK se genera dentro de:
`app/build/outputs/apk/release/`

---

## Uso de la app

1. Toca **Agregar** para registrar una persona.
2. Ingresa:
   - nombre,
   - monto del préstamo,
   - pago mensual,
   - fecha de inicio.
3. Abre la tarjeta para ver detalle e historial.
4. Toca **Me pagó** para marcar meses pagados.
5. Si te equivocas, toca un mes pagado para quitar ese pago.
6. Usa **Devolvió el préstamo** para cerrar el préstamo.

---

## Notificaciones

La app crea un canal de notificaciones llamado **Cobros** y programa una tarea diaria (aprox. 9:00 AM) que revisa préstamos activos:

- **Hoy toca cobrar** cuando un mes vence hoy y no tiene pago.
- **No ha pagado** cuando pasaron 3 días desde el cobro sin pago.

En Android 13+ se solicita permiso `POST_NOTIFICATIONS`.

---

## Build y release

Existe un workflow en `.github/workflows/build-release.yml` que:

1. Compila `assembleRelease`.
2. Renombra el APK con el tag (`prestamos-app-<tag>.apk`).
3. Publica artifact de Actions.
4. Crea GitHub Release automáticamente para tags `v*.*.*`.

También puede ejecutarse manualmente con `workflow_dispatch`.

---

## Datos de prueba

Puedes probar con estos datos iniciales:

- Nombre: `Juan Pérez`
- Préstamo: `500`
- Pago al mes: `50`
- Fecha de inicio: hace 3 meses

Con eso verás meses vencidos y podrás simular pagos parciales o múltiples.

---

## Roadmap sugerido

- Exportación/importación de respaldos.
- Filtros y búsqueda por nombre/estado.
- Monedas configurables.
- Gráficas de cobro mensual.
- Bloqueo biométrico opcional.

---

## Licencia

Este proyecto se distribuye bajo la licencia **MIT**.  
Consulta el archivo [`LICENSE`](LICENSE).

---

## Autor

**Joshua Terrones**  
GitHub: [@JoshuaTerrones](https://github.com/JoshuaTerrones)

---

<p align="center">
  Hecho con ❤️ para simplificar el control de préstamos personales.
</p>
