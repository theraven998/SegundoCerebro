# Segundo Cerebro

App Android nativa para tomar notas siguiendo el método **Building a Second Brain (BASB)** de Tiago Forte: organización **P.A.R.A.** y flujo **C.O.D.E.**. Diseño minimalista cálido (estilo papel), 100% offline y local.

<p align="center"><em>Captura · Organiza · Destila · Expresa</em></p>

## Método

**P.A.R.A.** — toda nota vive en una de cuatro categorías:

| | |
|---|---|
| **Proyectos** | Esfuerzos con fecha y meta |
| **Áreas** | Responsabilidades a mantener |
| **Recursos** | Temas de interés futuro |
| **Archivo** | Inactivo, por si acaso |

**C.O.D.E.** — el flujo de trabajo:

- **Capture** — captura rápida sin fricción a la Bandeja de entrada.
- **Organize** — mueve la nota a un contenedor P.A.R.A.
- **Distill** — niveles *Capturado / Resaltado / Resumido / Esencial* + idea clave.
- **Express** — favoritos y búsqueda full-text.

## Funcionalidades

- Captura rápida a bandeja de entrada y organización posterior.
- Contenedores P.A.R.A. con contador reactivo.
- Editor de notas con título, cuerpo, idea clave, etiquetas y niveles de destilado.
- Imágenes adjuntas desde la galería (selector del sistema, sin permisos).
- Búsqueda full-text (título, contenido, resumen, etiquetas).
- Favoritos.
- Backup: exportar / importar toda la información en JSON.
- Modo claro y oscuro.

## Stack

- **Kotlin** + **Jetpack Compose** (Material 3)
- **Room** (persistencia local, KSP)
- **Navigation Compose**
- **Coil** (imágenes)
- **kotlinx.serialization** (backup JSON)
- Arquitectura MVVM, inyección de dependencias manual.
- minSdk 26 · compileSdk 36 · JDK 17

## Compilar

```bash
git clone https://github.com/theraven998/SegundoCerebro.git
cd SegundoCerebro
./gradlew :app:assembleDebug
```

APK en `app/build/outputs/apk/debug/app-debug.apk`. Requiere el Android SDK; `local.properties` apunta a tu instalación (`sdk.dir=...`).

## Privacidad

Todo se almacena localmente en el dispositivo. Sin red, sin cuentas, sin telemetría.

## Licencia

MIT.
