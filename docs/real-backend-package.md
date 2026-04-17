# Real Backend Package Structure

Dedicated package for future on-device inference integrations:

- `app/src/main/java/com/localaichat/data/backend/real`

## Purpose

This package is the concrete integration area for future backend-specific code.

It does not replace:

- `LlmEngine`
- `RealLlmEngineAdapter`
- `ModelManager`
- `LocalModelRegistry`

Instead, it gives those higher-level boundaries a clean place to delegate to
once a real Android runtime is chosen.

## Package contents

### `RealBackendSession`

Top-level session abstraction for one initialized runtime.

Responsibilities:

- model file validation
- model initialization
- prompt execution
- token streaming
- cancellation
- teardown

### `RealBackendModelFileValidator`

Validates a local model file before initialization.

Examples of future checks:

- file exists
- file permissions are correct
- expected format signature is present
- backend-specific compatibility checks pass

### `RealBackendModelInitializer`

Creates runtime resources for a validated model.

Examples:

- tokenizer startup
- mmap / file opening
- JNI session creation
- delegate / accelerator initialization

### `RealBackendPromptExecutor`

Owns prompt submission, token streaming, and generation cancellation.

This keeps prompt execution independent from installation or UI state.

### `RealBackendTeardown`

Releases all resources associated with the live runtime.

## Placeholder implementation

`PlaceholderRealBackendSession` wires together no-op placeholder subcomponents.

It exists only so the package structure is concrete now and can be replaced
later with:

- `LlamaCppBackendSession`
- `MediaPipeBackendSession`
- `OnnxBackendSession`
- or another backend-specific session type

## Intended future wiring

1. `LocalModelRegistry`
   Handles install/load/select UX.

2. `ModelManager`
   Decides which model is selected and ready.

3. Real backend coordinator or real `LlmEngine`
   Creates and owns a `RealBackendSession`.

4. Session lifecycle
   - `validateModelFile()`
   - `initializeModel()`
   - `executePrompt()`
   - `cancel()`
   - `teardown()`

## Current status

- No real backend is connected
- `FakeLocalLlmEngine` remains the default runtime
- This package contains architecture and placeholders only
