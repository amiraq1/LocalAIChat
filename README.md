# LocalAIChat

LocalAIChat is an Android application built with Kotlin and Jetpack Compose that explores a clean, extensible architecture for on-device AI chat.

The project is structured to support multiple local inference backends while keeping UI, chat flow, prompt rendering, model lifecycle, and backend integration concerns clearly separated.

## Features

- Modern Android app built with Kotlin and Jetpack Compose
- MVVM architecture with clean layering
- Single-activity Compose app with type-safe navigation
- Chat interface with:
  - streaming response UI
  - stop generation action
  - explicit chat readiness states
  - surfaced failure and cancellation states
- Local model management with:
  - install, load, select, and unload workflow
  - runtime lifecycle tracking
  - progress reporting
  - selected-model vs active-loaded-model separation
- Backend selection flow with:
  - persisted backend choice
  - availability reporting
  - compatibility checks
- Prompt templating layer using rendered prompts
- Room persistence for local chat history
- DataStore-backed app settings and model selection state
- Placeholder support for future real local inference backends
- Fake backend available as the safe default runtime

## Project Goals

This project focuses on building a robust Android foundation for local AI inference by solving the surrounding product and engineering concerns first:

- backend selection
- model compatibility
- model lifecycle
- chat readiness
- progress tracking
- prompt rendering
- future backend integration

Rather than coupling the app directly to one inference engine, LocalAIChat is designed so multiple backend paths can be added cleanly over time.

## Current Backend Status

### Available

- Fake Backend
  - default runtime
  - useful for UI, state, and architecture development

### Placeholder / Planned

- MediaPipe backend
  - architecture path prepared
  - execution stubs and integration notes added
  - real dependency integration not yet completed

- Additional backend paths
  - the project structure is intentionally flexible enough to support future engines such as ONNX Runtime, llama.cpp, or similar runtimes later

## Architecture Overview

The app separates responsibilities into distinct layers:

- UI Layer
  - Compose screens
  - screen state rendering
  - user interaction handling

- Presentation Layer
  - ViewModels
  - state coordination
  - readiness and lifecycle-driven UI decisions

- Domain Layer
  - use cases
  - backend and model abstractions
  - prompt rendering contracts
  - compatibility and readiness logic

- Data / Integration Layer
  - repositories
  - backend managers
  - model registry and lifecycle coordinators
  - placeholder backend implementations

## Core Concepts

### Chat Readiness

Chat generation is only allowed when all required conditions are satisfied, including:

- a backend is selected
- the backend is available
- a compatible model is selected
- the selected model is loaded and ready
- generation is not already in progress

This keeps the UI honest about when inference can actually begin.

### Backend Selection

Backends are modeled explicitly rather than hidden behind a single hardcoded engine. The app tracks:

- selected backend
- backend availability
- capability information
- model compatibility with the active backend

This makes it possible to add real inference runtimes later without rewriting the UI flow.

### Model Lifecycle

The model lifecycle is treated as a first-class concern. The app distinguishes between metadata, selection state, install state, and runtime readiness.

Relevant states include:

- not installed
- downloading
- installing
- installed
- loading
- ready
- failed

### Prompt Rendering

Prompt generation is routed through a prompt-formatting layer instead of embedding raw string assembly directly in the chat UI or generation use case.

Current prompt formatter implementations include:

- Plain prompt formatter
- Gemma prompt formatter
- Llama 3 prompt formatter

This creates a clean path for model-specific prompt formatting when real local runtimes are introduced.

## Technical Highlights

- `LlmEngine` is the generation boundary used by the chat flow
- `DynamicLlmEngine` can route generation based on the selected backend
- `FakeLocalLlmEngine` remains the safe default implementation
- `ModelManager` owns runtime loading, initialization, readiness, and failure state
- `LocalModelRegistry` provides a UI-facing model catalog and workflow surface
- `BackendManager` exposes selectable backends, availability, and runtime metadata
- `ModelCompatibilityChecker` gates models against backend requirements
- `AppContainer` wires dependencies without a DI framework

## Project Map

- `app/src/main/java/com/localaichat/ui`
  Compose app shell, screens, navigation, and theme
- `app/src/main/java/com/localaichat/domain`
  domain models, repository contracts, and use cases
- `app/src/main/java/com/localaichat/data`
  repository implementations, Room storage, prompt formatters, backend selection, and model lifecycle wiring
- `app/src/main/java/com/localaichat/data/backend/real`
  shared abstractions for future real backend integrations
- `app/src/main/java/com/localaichat/data/backend/mediapipe`
  MediaPipe-specific placeholders and execution stubs
- `app/src/main/java/com/localaichat/di/AppContainer.kt`
  manual dependency wiring
- `docs/`
  backend architecture notes, integration plans, prompt templating docs, and MediaPipe implementation checklists

## Backend Integration Notes

The repository already contains planning and placeholder architecture for future real on-device inference support, including:

- backend adapter boundaries
- capability negotiation types
- prompt execution abstractions
- model initialization and teardown hooks
- MediaPipe integration plans and checklist documents

This means the project is not just a demo UI. It is an architectural foundation intended to make a real backend integration incremental instead of invasive.

## Build

Set your Android SDK path before building:

```properties
sdk.dir=/absolute/path/to/Android/Sdk
```

Place that in `local.properties`, or export `ANDROID_HOME`.

Then build with Gradle:

```sh
./gradlew assembleDebug
```
