# LocalAIChat

Single-activity Android app built with Kotlin and Jetpack Compose.

## Architecture

- MVVM for screen state and actions
- Repository pattern for data access
- Room for local chat history persistence
- DataStore for generation settings and selected model
- Type-safe Compose Navigation for the Chat, Models, and Settings screens
- Swap-ready `LlmEngine` interface for future on-device inference backends
- `ModelManager` layer that owns model readiness, initialization, failure, and active-model state
- `LocalModelRegistry` layer for local model catalog and placeholder load/unload/select actions
- `RealLlmEngineAdapter` boundary for a future Android on-device inference runtime
- `BackendCapabilities` types for backend capability negotiation
- Edge-to-edge activity setup with inset-aware Compose screens
- No XML layouts; only `AndroidManifest.xml`
- No DI framework; dependencies are wired in a plain `AppContainer`

## Project map

- `app/src/main/java/com/localaichat/ui`: Compose app shell, navigation, screens, and theme
- `app/src/main/java/com/localaichat/domain`: app models, repository contracts, and use cases
- `app/src/main/java/com/localaichat/data`: Room entities/DAO plus repository implementations
- `app/src/main/java/com/localaichat/data/backend/real`: placeholder package for future real on-device backend sessions
- `app/src/main/java/com/localaichat/di/AppContainer.kt`: simple manual dependency wiring
- `docs/real-inference-adapter.md`: future real-backend integration notes
- `docs/real-backend-package.md`: concrete package layout for future backend integrations

## Local model registry

- `LocalModelRegistry` provides a UI-facing local model catalog.
- `LocalModel` is a terminal-friendly data class that carries display name, path, size, install state, and selection flags.
- `LocalModelState` tracks `NotInstalled`, `Downloading`, `Installing`, `Installed`, `Loading`, `Ready`, and `Failed`.
- The default implementation is `LocalModelRegistryImpl`, which maps existing `ModelManager` state into a simple registry model.
- `Install`, `Load`, `Unload`, `Select`, and cancellation are placeholder actions only. No real backend is connected yet.

## Lifecycle abstractions

- `LocalModelInstallationWorkflow` is the install/download abstraction.
- `LocalModelLoadingWorkflow` is the load-to-memory abstraction.
- Both currently use placeholder implementations that emit progress and cancellation-friendly state updates.
- These abstractions are injected through `AppContainer`, so a real backend can replace them later without changing the screen or registry contract.

## Runtime model flow

1. `ModelRepository` stores model catalog metadata and selected model id.
2. `ModelManager` overlays runtime state on top of that metadata:
   loading, initializing, ready, and failed.
3. `LocalModelRegistry` drives install/load/unload/select workflows and updates readiness through the model lifecycle layers.
4. `SendMessageUseCase` only generates when the selected model is already ready.
5. `LlmEngine` remains the generation boundary and still defaults to `FakeLocalLlmEngine`.

## Model metadata

Each `ModelOption` now carries:

- `name`
- `sizeBytes`
- `localPath`
- `isInstalled`
- `status`

This is enough to plug in a real backend later without rewriting the UI contracts.

## LLM integration path

- Replace `FakeLocalLlmEngine` with a real engine implementation
- Keep `LlmEngine` stable so UI and repositories do not depend on a specific backend
- Use `ModelManager` to map backend-specific loading/init/error callbacks into app state
- Use `RealLlmEngineAdapter` as the low-level backend boundary for model init, teardown, streaming, cancellation, and error propagation
- Use `BackendCapabilities` and `BackendCapabilityNegotiator` to describe runtime features such as streaming, cancellation, context limits, and supported model formats
- Use the `data/backend/real` package for backend-specific sessions, model validation, initialization, prompt execution, cancellation, and teardown
- Existing chat flow already supports streaming chunks, cancellation cleanup, and surfaced failure state
- Chat generation is gated on a selected local model being both selected and ready
- The chat UI shows both the selected model and the currently active loaded model
- The chat screen uses explicit conversation states: idle, no model selected, model not ready, generating, cancelled, and failed

## Build

Set your Android SDK path before building:

```properties
sdk.dir=/absolute/path/to/Android/Sdk
```

Place that in `local.properties`, or export `ANDROID_HOME`.
