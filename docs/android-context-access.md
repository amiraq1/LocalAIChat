# Android Context Access for Native Backends

This document describes how the application provides the Android `Context` to
native backend runtimes while keeping the domain and core abstractions
backend-agnostic.

## Why Context is Needed

Native Android inference libraries often require a `Context` for:
- Initializing runtime environments (e.g., MediaPipe `LlmInference`).
- Accessing assets or internal storage paths.
- Verifying hardware capabilities (e.g., GPU availability).

## Implementation: `AndroidBackendContext`

To avoid leaking `android.content.Context` into the `domain` layer or the
generic `RealLlmEngineAdapter` interface, we use a wrapper interface in the
data layer:

```kotlin
// com.localaichat.data.backend.real.AndroidBackendContext
interface AndroidBackendContext {
    val applicationContext: Context
}
```

### Entry Point: `AppContainer`

The `Context` enters the system at the highest level of the data layer:

1. `AppContainer` receives the `ApplicationContext` during startup.
2. It creates an `AndroidBackendContextProvider`.
3. It injects this provider into specific backend adapters (e.g., `MediaPipeLlmEngineAdapter`).

### Propagation Flow

1. **`AppContainer`** -> `MediaPipeLlmEngineAdapter(context)`
2. **`MediaPipeLlmEngineAdapter`** -> `MediaPipeBackendSession(model, context)`
3. **`MediaPipeBackendSession`** -> `MediaPipeModelInitializer(context)`
4. **`MediaPipeModelInitializer`** -> Calls `LlmInference.createFromOptions(context.applicationContext, ...)`

## Benefits

- **Backend-Agnostic Domain**: The `domain` package remains 100% pure Kotlin and does not depend on the Android framework.
- **Safe Dependency Injection**: Only classes that *explicitly* require a `Context` (like native initializers) receive it.
- **Easy Testing**: The `AndroidBackendContext` can be mocked in unit tests without starting a full Robolectric or Android instrumentation environment.
