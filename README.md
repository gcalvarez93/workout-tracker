# Workout Tracker

An Android workout tracking app built as part of the CastroDev portfolio. Create and manage your workout routines, track your training sessions with exercise sets and reps, monitor your weekly analytics and body weight evolution.

---

## 📱 Screenshots

<p align="center">
  <img src="screenshots/login_light.png" alt="Login screen" width="23%" />
  <img src="screenshots/routines_light.png" alt="Routines list" width="23%" />
  <img src="screenshots/routine_detail_light.png" alt="Routine detail with exercises" width="23%" />
  <img src="screenshots/profile_dark.png" alt="User profile in dark mode" width="23%" />
</p>
<p align="center">
  <img src="screenshots/session_dark.png" alt="Active training session in dark mode" width="23%" />
  <img src="screenshots/analytics_dark.png" alt="Weekly analytics in dark mode" width="23%" />
</p>

---

## ✨ Features

- 💪 **Routines** — Create and manage workout routines with custom exercises, sets, reps, weight and rest time
- ✏️ **Edit routines** — Update name, description and exercises at any time
- ▶️ **Sessions** — Start training sessions from a routine and complete them marking sets as done
- 📋 **Session history** — View all past completed sessions
- 📊 **Weekly analytics** — Total sessions, volume and duration per week with navigation between weeks
- ⚖️ **Body weight tracking** — Log your daily weight and track your evolution over time
- 🌙 **Dark mode** — Automatic dark/light mode based on system settings
- 👤 **Profile** — Edit name, manage notifications; language follows system settings
- 🌍 **Multilingual** — English and Spanish support

---

## 🛠️ Tech Stack

| Layer | Technology |
|---|---|
| Language | Kotlin |
| UI Framework | Jetpack Compose |
| State Management | ViewModel + StateFlow |
| Dependency Injection | Hilt |
| Authentication | Firebase Auth (Email) |
| Backend | .NET 10 REST API (Clean Architecture) |
| Database | Cloud Firestore (behind the API) |
| Networking | Retrofit + OkHttp |
| Logging | Timber |
| Testing | JUnit4, MockK, coroutines-test |
| Infrastructure | Google Cloud Run |

---

## 🏗️ Architecture

The app follows **Clean Architecture** per feature. The presentation layer depends on domain abstractions only; Hilt wires the data implementations.

```
workouttracker/
├── core/
│   ├── error/         # AppError: typed errors, no Android code
│   ├── network/       # API config, token provider, interceptor, 401 authenticator
│   ├── ui/            # AppError → localized message (@StringRes)
│   ├── util/          # safeCall (Result + logging) and exception → AppError mapping
│   └── theme/         # Colors, typography, Material3 theme
├── di/                # Hilt modules (network, Firebase, bindings)
├── features/
│   ├── auth/          # Login, register
│   ├── routines/      # Routine CRUD with exercises
│   ├── sessions/      # Session tracking with set completion
│   ├── analytics/     # Weekly summary and body weight
│   ├── bodyweight/    # Body weight logging
│   └── profile/       # User profile & settings
└── navigation/        # App navigation & bottom tabs
```

Each feature follows the pattern:

```
Feature/
├── data/
│   ├── datasource/    # Retrofit API service & remote data source
│   ├── model/         # DTO models & request/response
│   └── repository/    # Repository implementations
├── domain/
│   ├── entity/        # Domain entities
│   ├── repository/    # Abstract repository interfaces
│   └── usecase/       # Business logic use cases
└── presentation/
    ├── screen/        # Composable screens
    ├── viewmodel/     # ViewModels with StateFlow
    └── components/    # Reusable UI components
```

---

## 🧭 Technical Decisions

- **Clean Architecture per feature.** Each feature has its own `data / domain / presentation`. The domain layer has no Android, Retrofit or Firebase code, so use cases and repository contracts can be tested on the JVM.
- **Hilt for dependency injection.** ViewModels receive their use cases through the constructor and never import the `data` package. Each repository interface is bound to its implementation in a single module (`BindingsModule`), so tests swap in fakes without touching production code.
- **ViewModel + StateFlow with sealed UI states.** Each screen exposes a `Loading / Success / Error` state, so the UI cannot show data that has not loaded. The error state holds a `@StringRes` id, never the exception text.
- **`Result` with a `safeCall` helper instead of `runCatching`.** Repositories return `Result<T>` and log failures with Timber. `safeCall` re-throws `CancellationException`, which `runCatching` would swallow and turn into a failure when a coroutine is cancelled.
- **Typed errors, translated once.** `safeCall` logs the original exception and returns it as an `AppError` (network, timeout, server, session expired, no permission, invalid credentials, email already in use…). Retrofit and Firebase exceptions are mapped in a single place (`ErrorMapping.kt`), so nothing above the repositories depends on them. `FirebaseAuthWeakPasswordException` extends `FirebaseAuthInvalidCredentialsException`, so the weak-password check has to come first; a test pins that order.
- **Users never see technical text.** Each `AppError` maps to a localized string (English and Spanish); the technical detail goes to Timber only.
- **A failed write does not wipe the screen.** Creating or editing a routine, deleting it, logging a weight and updating the profile keep the data that was already loaded when they fail. The error appears inside the sheet or dialog that was open (which stays open, so nothing typed is lost) or in a snackbar, and the save button is disabled while a request is in flight to avoid double submits. Starting a session that fails shows a snackbar; completing one keeps the session screen open so it can be retried.
- **The domain owns its types.** Completing a session sends domain objects (`CompletedExercise`), and the data layer maps them to the API request. Before, the domain and the ViewModel imported request classes from `data`.
- **One Retrofit client, configured once.** A singleton OkHttp client adds the Firebase token through an interceptor, with 30 s connect, read and write timeouts. Request and response bodies are logged only in debug builds and the `Authorization` header is always redacted.
- **An expired session signs the user out.** On a 401 an OkHttp `Authenticator` forces a Firebase token refresh and retries the request once. If the server still answers 401, or there is no session, it emits a `SessionEvents` notice; `AppNavigation` collects it, signs out and returns to the login screen with an explanatory message. Losing the connection during the refresh is a network error, not a sign-out. A 403 is a separate "no permission" error and does not end the session.
- **HTTP errors are not ignored.** Retrofit does not throw for `Response<Unit>` calls with an error status, so update, delete and complete calls check the response and fail explicitly.
- **Use cases per operation.** Most only delegate to the repository; creating a routine also validates the name (a typed validation error with its own message) before calling it.
- **Shared .NET 10 API.** Business rules live on the server and the app only talks to it with the user's Firebase token. One backend serves every app in this portfolio.
- **Tests with fakes and MockK.** Use cases, repositories, remote data sources, all the ViewModels, `safeCall`, the error mapping, the auth interceptor and the 401 authenticator are covered with in-memory fakes of the domain interfaces. MockK is used where the collaborator is a concrete class or the Retrofit service, and to stand in for Firebase exceptions, whose constructors do not run in JVM tests. Snackbars, dialogs and sheets are not covered by UI tests.
- **Google sign-in is wired but has no screen.** The data source, repository, use case and ViewModel support signing in with a Google ID token, but the login screen only offers email and password, so it is not user-facing yet.

### Known limitations

Deliberate technical debt, documented rather than hidden:

- **Lists are reloaded through the `Loading` state.** After a successful create, edit or delete the list is reloaded and, while it loads, replaced by a spinner. If that reload fails, the `Error` state replaces the list even though the write succeeded. A failed write on its own does keep the list, as described above.
- **No retry button on load errors.** The full-screen error state only shows the message.
- **Release builds are not minified.** `isMinifyEnabled` is off, so R8 does not shrink or obfuscate the release build.
- **No automated UI tests.** Snackbars, dialogs and sheets are checked by hand.
- **Shared code is copied, not shared.** `AppError`, `safeCall`, the error mapping and the network layer also exist in recipe-manager; there is no shared module, so a fix has to be applied in both.

### Other projects in this portfolio

| Project | Platform | Language |
|---|---|---|
| [finance-tracker](https://github.com/gcalvarez93/finance-tracker) | Flutter (iOS + Android) | Dart |
| [habit-tracker](https://github.com/gcalvarez93/habit-tracker) | Flutter (iOS + Android) | Dart |
| [task-manager](https://github.com/gcalvarez93/task-manager) | iOS | Swift |
| [task-manager-kmp](https://github.com/gcalvarez93/task-manager-kmp) | Android + iOS | Kotlin Multiplatform + Swift |
| [budget-scanner](https://github.com/gcalvarez93/budget-scanner) | iOS | Swift |
| [recipe-manager](https://github.com/gcalvarez93/recipe-manager) | Android | Kotlin |
| [workout-tracker](https://github.com/gcalvarez93/workout-tracker) | Android | Kotlin |
| [castrodev-api](https://github.com/gcalvarez93/castrodev-api) | Shared backend | C# (.NET 10) |

---

## 🚀 Getting Started

### Prerequisites

- Android Studio Hedgehog or later
- Android SDK 26+
- Firebase project configured
- API running at `api.castrodev.com` or locally

### Installation

```bash
# Clone the repository
git clone https://github.com/gcalvarez93/workout-tracker.git

# Open in Android Studio
```

Then:

1. Add your `google-services.json` to the `app/` folder
2. Sync Gradle dependencies
3. Build and run with the Run button or `Shift + F10`

### Running Tests

```bash
./gradlew test
```

---

## 🔗 Related

- [CastroDev API](https://github.com/gcalvarez93/castrodev-api) — Shared .NET 10 backend

---

## 📄 License

MIT © Gabriel Castro

---

---

# Workout Tracker

App Android de seguimiento de entrenamientos desarrollada como parte del portfolio de CastroDev. Crea y gestiona tus rutinas de entrenamiento, registra tus sesiones con los ejercicios completados, y monitoriza tu progreso semanal y tu evolución de peso corporal.

---

## ✨ Funcionalidades

- 💪 **Rutinas** — Crea y gestiona rutinas con ejercicios personalizados, series, repeticiones, peso y tiempo de descanso
- ✏️ **Editar rutinas** — Actualiza nombre, descripción y ejercicios en cualquier momento
- ▶️ **Sesiones** — Inicia sesiones de entrenamiento desde una rutina y complétalas marcando las series
- 📋 **Historial de sesiones** — Consulta todas las sesiones completadas
- 📊 **Análisis semanal** — Total de sesiones, volumen y duración por semana con navegación entre semanas
- ⚖️ **Peso corporal** — Registra tu peso diario y sigue tu evolución en el tiempo
- 🌙 **Modo oscuro** — Modo oscuro/claro automático según la configuración del sistema
- 👤 **Perfil** — Edita tu nombre y gestiona notificaciones; el idioma sigue los ajustes del sistema
- 🌍 **Multiidioma** — Soporte para español e inglés

---

## 🛠️ Stack Tecnológico

| Capa | Tecnología |
|---|---|
| Lenguaje | Kotlin |
| UI Framework | Jetpack Compose |
| Estado | ViewModel + StateFlow |
| Inyección de dependencias | Hilt |
| Autenticación | Firebase Auth (Email) |
| Backend | API REST .NET 10 (Clean Architecture) |
| Base de datos | Cloud Firestore (tras la API) |
| Red | Retrofit + OkHttp |
| Logging | Timber |
| Testing | JUnit4, MockK, coroutines-test |
| Infraestructura | Google Cloud Run |

---

## 🧭 Decisiones Técnicas

- **Clean Architecture por feature.** Cada feature tiene su propio `data / domain / presentation`. La capa de dominio no contiene código de Android, Retrofit ni Firebase, así que los casos de uso y los contratos de repositorio se prueban en la JVM.
- **Hilt para la inyección de dependencias.** Los ViewModels reciben sus casos de uso por constructor y nunca importan el paquete `data`. Cada interfaz de repositorio se enlaza con su implementación en un único módulo (`BindingsModule`), así que los tests sustituyen las implementaciones por fakes sin tocar el código de producción.
- **ViewModel + StateFlow con estados de UI sellados.** Cada pantalla expone un estado `Loading / Success / Error`, y la UI no puede mostrar datos que no se han cargado. El estado de error guarda el id de un `@StringRes`, nunca el texto de la excepción.
- **`Result` con un helper `safeCall` en lugar de `runCatching`.** Los repositorios devuelven `Result<T>` y registran los fallos con Timber. `safeCall` relanza `CancellationException`, que `runCatching` se tragaría y convertiría en fallo al cancelar una corrutina.
- **Errores tipados, traducidos una sola vez.** `safeCall` registra la excepción original y la devuelve como un `AppError` (red, timeout, servidor, sesión caducada, sin permiso, credenciales incorrectas, correo ya en uso…). Las excepciones de Retrofit y Firebase se traducen en un único sitio (`ErrorMapping.kt`), así que nada por encima de los repositorios depende de ellas. `FirebaseAuthWeakPasswordException` hereda de `FirebaseAuthInvalidCredentialsException`, por lo que la comprobación de contraseña débil debe ir primero; un test fija ese orden.
- **El usuario nunca ve texto técnico.** Cada `AppError` se asocia a un texto localizado (inglés y español); el detalle técnico va solo a Timber.
- **Un fallo al escribir no vacía la pantalla.** Crear o editar una rutina, borrarla, registrar un peso y actualizar el perfil conservan los datos ya cargados si fallan. El error aparece dentro de la hoja o el diálogo abierto (que sigue abierto, así que no se pierde lo escrito) o en un snackbar, y el botón de guardar se desactiva mientras hay una petición en curso para evitar dobles envíos. Si falla al empezar una sesión se avisa con un snackbar; si falla al completarla, la pantalla de la sesión sigue abierta para reintentar.
- **El dominio es dueño de sus tipos.** Al completar una sesión se envían objetos del dominio (`CompletedExercise`) y la capa data los traduce a la petición de la API. Antes, el dominio y el ViewModel importaban clases de petición desde `data`.
- **Un único cliente Retrofit, configurado una vez.** Un cliente OkHttp singleton añade el token de Firebase mediante un interceptor, con timeouts de 30 s de conexión, lectura y escritura. Los cuerpos de peticiones y respuestas solo se registran en builds de debug y la cabecera `Authorization` siempre se oculta.
- **Una sesión caducada cierra la sesión.** Ante un 401, un `Authenticator` de OkHttp fuerza la renovación del token de Firebase y reintenta la petición una vez. Si el servidor sigue respondiendo 401, o no hay sesión, emite un aviso por `SessionEvents`; `AppNavigation` lo recoge, cierra la sesión y vuelve al login con un mensaje explicativo. Perder la conexión durante la renovación es un error de red, no cierra la sesión. Un 403 es un error aparte de «sin permiso» y no termina la sesión.
- **Los errores HTTP no se ignoran.** Retrofit no lanza excepción en las llamadas `Response<Unit>` con estado de error, así que actualizar, borrar y completar comprueban la respuesta y fallan de forma explícita.
- **Casos de uso por operación.** La mayoría solo delega en el repositorio; crear una rutina también valida el nombre (un error de validación tipado con su propio mensaje) antes de llamarlo.
- **API compartida en .NET 10.** Las reglas de negocio viven en el servidor y la app solo habla con él usando el token de Firebase del usuario. Un solo backend sirve a todas las apps de este portfolio.
- **Tests con fakes y MockK.** Casos de uso, repositorios, datasources remotos, todos los ViewModels, `safeCall`, el mapeo de errores, el interceptor de auth y el autenticador de 401 están cubiertos con fakes en memoria de las interfaces del dominio. MockK se usa cuando el colaborador es una clase concreta o el servicio Retrofit, y para simular las excepciones de Firebase, cuyos constructores no se ejecutan en tests de JVM. Los snackbars, diálogos y hojas no tienen tests de UI.
- **El inicio de sesión con Google está preparado pero sin pantalla.** El datasource, el repositorio, el caso de uso y el ViewModel admiten entrar con un ID token de Google, pero la pantalla de login solo ofrece correo y contraseña, así que todavía no es visible para el usuario.

### Limitaciones conocidas

Deuda técnica asumida a propósito, documentada en lugar de ocultada:

- **Las listas se recargan con el estado `Loading`.** Tras crear, editar o borrar con éxito se recarga la lista y, mientras carga, se sustituye por un indicador de carga. Si esa recarga falla, el estado `Error` reemplaza la lista aunque la escritura haya salido bien. Una escritura fallida por sí sola sí conserva la lista, como se describe arriba.
- **Sin botón de reintento en los errores de carga.** El estado de error a pantalla completa solo muestra el mensaje.
- **Las builds de release no están minificadas.** `isMinifyEnabled` está desactivado, así que R8 no reduce ni ofusca la build de release.
- **Sin tests de UI automáticos.** Los snackbars, diálogos y hojas se comprueban a mano.
- **Código compartido copiado, no compartido.** `AppError`, `safeCall`, el mapeo de errores y la capa de red existen también en recipe-manager; no hay un módulo común, así que una corrección hay que aplicarla en los dos.

### Otros proyectos del portfolio

| Proyecto | Plataforma | Lenguaje |
|---|---|---|
| [finance-tracker](https://github.com/gcalvarez93/finance-tracker) | Flutter (iOS + Android) | Dart |
| [habit-tracker](https://github.com/gcalvarez93/habit-tracker) | Flutter (iOS + Android) | Dart |
| [task-manager](https://github.com/gcalvarez93/task-manager) | iOS | Swift |
| [task-manager-kmp](https://github.com/gcalvarez93/task-manager-kmp) | Android + iOS | Kotlin Multiplatform + Swift |
| [budget-scanner](https://github.com/gcalvarez93/budget-scanner) | iOS | Swift |
| [recipe-manager](https://github.com/gcalvarez93/recipe-manager) | Android | Kotlin |
| [workout-tracker](https://github.com/gcalvarez93/workout-tracker) | Android | Kotlin |
| [castrodev-api](https://github.com/gcalvarez93/castrodev-api) | Backend compartido | C# (.NET 10) |

---

## 🚀 Instalación

### Requisitos previos

- Android Studio Hedgehog o posterior
- Android SDK 26+
- Proyecto Firebase configurado
- API disponible en `api.castrodev.com` o en local

### Pasos

```bash
# Clonar el repositorio
git clone https://github.com/gcalvarez93/workout-tracker.git

# Abrir en Android Studio
```

A continuación:

1. Añade tu `google-services.json` a la carpeta `app/`
2. Sincroniza las dependencias de Gradle
3. Compila y ejecuta con el botón Run o `Shift + F10`

### Tests

```bash
./gradlew test
```

---

## 🔗 Relacionado

- [CastroDev API](https://github.com/gcalvarez93/castrodev-api) — Backend compartido en .NET 10

---

## 📄 Licencia

MIT © Gabriel Castro
