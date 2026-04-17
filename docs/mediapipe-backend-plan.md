# MediaPipe Backend Adapter Plan

This document describes the architecture for a future MediaPipe-based LLM
inference backend in the `LocalAIChat` app.

## Architecture Overview

The MediaPipe backend is implemented in the `com.localaichat.data.backend.mediapipe`
package. It follows the standard `InferenceAdapter` and `RealBackendSession`
abstractions.

### Core Components

- **`MediaPipeInferenceAdapter`**
  Implements `InferenceAdapter`. Manages the lifecycle of a
  `MediaPipeBackendSession`. It handles model initialization, prompt streaming,
  and teardown by delegating to the active MediaPipe session.

- **`MediaPipeBackendSession`**
  Implements `RealBackendSession`. Wires together MediaPipe-specific
  subcomponents for file validation, runtime initialization, prompt execution,
  and resource cleanup.

- **`AdapterLlmEngine`**
  A generic bridge (in `data.repository`) that connects `MediaPipeInferenceAdapter`
  to the high-level `LlmEngine` interface used by the chat flow.

## Operational Flows

1. **Initialization**: `InferenceAdapter.initializeModel` creates the session.
2. **Inference**: `InferenceAdapter.streamInference` consumes an `InferenceRequest` containing a `RenderedPrompt`.
3. **Cleanup**: `InferenceAdapter.teardownModel` releases the session and native resources.

## Current Status
- Architecture and placeholder classes are in place in the `mediapipe` package.
- `FakeLocalLlmEngine` remains the default engine.
- `AdapterLlmEngine` is ready to bridge MediaPipe to the chat layer.
