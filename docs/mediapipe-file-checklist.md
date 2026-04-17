# MediaPipe Real Integration File Checklist

This document maps the real MediaPipe integration steps to specific files in the
codebase. Follow this list when moving from placeholders to a functional runtime.

## 1. Foundation & Dependencies

- [ ] **`app/build.gradle.kts`**
  - **Change**: Add `implementation("com.google.mediapipe:tasks-genai:<version>")`.
  - **Reason**: Pull in the native LLM Inference SDK.

## 2. Core Runtime Implementation (mediapipe package)

- [ ] **`MediaPipeBackendSession.kt`**
  - **Change**: Add a private property to hold the `LlmInference` instance.
  - **Reason**: This session owns the lifecycle of the native object.

- [ ] **`MediaPipeModelInitializer.kt`**
  - **Change**: Replace simulation logic with `LlmInference.createFromOptions()`.
  - **Reason**: Instantiate the actual native engine using the `.bin` file path and Android Context.

- [ ] **`MediaPipePromptExecutor.kt`**
  - **Change**: Use `llmInference.generateResponseAsync(request.prompt.text)`. 
  - **Change**: Bridge MediaPipe's `LlmInference.ResultListener` to emit `InferenceEvent.Token`.
  - **Reason**: Execute real on-device inference and stream tokens back to the UI.

- [ ] **`MediaPipeTeardown.kt`**
  - **Change**: Call `llmInference.close()` and nullify the reference.
  - **Reason**: Prevent native memory leaks and release GPU resources.

## 3. Wiring & UI Enabling

- [ ] **`data/repository/BackendManagerImpl.kt`**
  - **Change**: Update `BackendType.MEDIAPIPE` status from `Unavailable` to `Available`.
  - **Reason**: Surface the MediaPipe option to the user in the Settings screen.

## 4. Optional / Refinement Pass

- [ ] **`data/backend/mediapipe/MediaPipeInferenceAdapter.kt`**
  - **Change**: Refine `getBackendCapabilities()` to detect GPU support dynamically via MediaPipe's internal checks.
  - **Reason**: Provide more accurate capability info to the UI.

- [ ] **`domain/model/InferenceContract.kt`** (if needed)
  - **Change**: Add fields to `InferenceRequest` for MediaPipe-specific options (e.g., `topK`, `temperature`).
  - **Reason**: Allow fine-tuning of the model behavior.

## Summary of Changes by Role

| Responsibility | Target File |
| :--- | :--- |
| **Dependencies** | `app/build.gradle.kts` |
| **Object Lifetime** | `MediaPipeBackendSession.kt` |
| **Initialization** | `MediaPipeModelInitializer.kt` |
| **Execution** | `MediaPipePromptExecutor.kt` |
| **Cancellation** | `MediaPipePromptExecutor.kt` |
| **Cleanup** | `MediaPipeTeardown.kt` |
| **UI Visibility** | `BackendManagerImpl.kt` |
