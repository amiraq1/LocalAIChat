# MediaPipe Integration Checklist

This checklist defines the minimal executable path to replace the current
MediaPipe placeholders with a real implementation using the MediaPipe LLM
Inference API.

## 1. Build Configuration (Gradle)

- [ ] Add MediaPipe Tasks GenAI dependency to `app/build.gradle.kts`:
  ```kotlin
  implementation("com.google.mediapipe:tasks-genai:0.10.14")
  ```
- [ ] Ensure `minSdk` is at least 24 (current is 26, so we are safe).

## 2. Real Implementation (mediapipe package)

The following placeholder classes in `com.localaichat.data.backend.mediapipe` must be updated:

### [ ] `MediaPipeModelInitializer`
- **Goal**: Create a real `LlmInference` instance.
- **Action**:
  - Implement `initialize(model: RealModelHandle)`.
  - Use `LlmInference.LlmInferenceOptions.builder()`.
  - Set `.setModelPath(model.localPath)`.
  - Call `LlmInference.createFromOptions(context, options)`.
  - Store the resulting `LlmInference` instance in a thread-safe way (e.g., inside the `MediaPipeBackendSession`).

### [ ] `MediaPipePromptExecutor`
- **Goal**: Execute real inference and stream tokens.
- **Action**:
  - In `execute(request: RealGenerationRequest)`, call `llmInference.generateResponseAsync(prompt)`.
  - Use a `Flow` collector to bridge MediaPipe's listener callbacks to `RealGenerationEvent.Token`.
  - Map MediaPipe's `resultListener` to `emit(RealGenerationEvent.Token(...))`.
  - Emit `RealGenerationEvent.Completed` when the final result is received.

### [ ] `MediaPipeTeardown`
- **Goal**: Release native resources.
- **Action**:
  - Call `llmInference.close()` on the active instance.

### [ ] `MediaPipeModelValidator`
- **Goal**: Basic file check.
- **Action**:
  - (Already sufficient) verify `.bin` extension and file existence.

## 3. Availability Toggle

- [ ] Update `BackendManagerImpl.kt`:
  - Change `BackendType.MEDIAPIPE` status from `BackendStatus.Unavailable` to `BackendStatus.Available`.

## 4. Verification Flow

1. **Select Backend**: In Settings, choose "MediaPipe LLM".
2. **Compatibility Check**: Ensure the UI shows compatible models (ending in `.bin`).
3. **Load Model**: Trigger `loadModel`, which calls `MediaPipeModelInitializer`.
4. **Chat**: Send a message; `DynamicLlmEngine` will now route to `MediaPipeLocalLlmEngine`.
5. **Stream**: Verify tokens appear incrementally via `MediaPipePromptExecutor`.

## Sufficient Abstractions (No changes needed)

- `RealLlmEngineAdapter`: Correctly handles session lifecycle.
- `LlmEngine`: Correctly delegates to the dynamic implementation.
- `DynamicLlmEngine`: Already supports fallback and routing.
- `MediaPipeLocalLlmEngine`: Correctly bridges `RealGenerationEvent` to `Flow<String>`.
- `ObserveChatReadinessUseCase`: Correctly computes UI state from backend/model status.

## Extensions Needed (Future)

- `BackendCapabilities`: Add `supportsGpuAcceleration` or `maxBatchSize` if MediaPipe exposes them and the app needs to optimize based on device hardware.
- `RealModelHandle`: May need extra fields for MediaPipe-specific tuning parameters (e.g., `topK`, `temperature` mapping).

## Summary of Impacted Files

| File | Change Type | Reason |
| :--- | :--- | :--- |
| `app/build.gradle.kts` | Modification | Add `tasks-genai` library |
| `MediaPipeModelInitializer.kt` | Implementation | Instantiate `LlmInference` |
| `MediaPipePromptExecutor.kt` | Implementation | Call `generateResponseAsync` |
| `MediaPipeTeardown.kt` | Implementation | Call `LlmInference.close()` |
| `BackendManagerImpl.kt` | Modification | Enable MediaPipe in UI |
