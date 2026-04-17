# Backend Architecture Audit

This document reviews the current architecture for gaps and risks before
integrating real on-device inference backends (MediaPipe, llama.cpp, etc.).

## 1. Audit Summary

The current architecture is **Sufficient** for:
- Backend selection and persistence.
- Unified chat readiness (backend availability + model compatibility).
- Dynamic routing of generation requests.
- Basic session lifecycle (Init -> Execute -> Teardown).

The following **Gaps and Risks** must be addressed during integration:

## 2. Meaningful Gaps

### A. Android Context in Initializers
- **Risk**: Native backends like MediaPipe `LlmInference.createFromOptions()` require an Android `Context`.
- **Gap**: `MediaPipeModelInitializer` and `RealBackendSession` currently do not accept a `Context` in their `initialize` methods.
- **Fix**: Update the `RealBackendSession.initializeModel` or provide a `Context` through the constructor/DI.

### B. Prompt Templating Layer
- **Risk**: Every model (Gemma, Llama, Phi) requires a specific prompt template (e.g., `<start_of_turn>user\n...`).
- **Gap**: The backend package lacks a dedicated `PromptFormatter` or `TemplateEngine` to convert `List<ChatMessage>` into the raw string expected by the native runtime.
- **Fix**: Introduce a `PromptFormatter` interface inside `com.localaichat.data.backend.real` that can be customized per model/backend.

### C. Initialization Progress Tracking
- **Risk**: Loading a 2GB model into VRAM/RAM takes significant time. Users need progress feedback.
- **Gap**: `RealLlmEngineAdapter.initializeModel` is a simple `suspend` function with no way to report progress (0-100%).
- **Fix**: Update the interface to return a `Flow<InitializationEvent>` or accept a progress callback.

### D. Native Threading Model
- **Risk**: Native LLM runtimes are often single-threaded and can crash if called from multiple threads or can block the main thread.
- **Gap**: `MediaPipePromptExecutor` does not yet explicitly define a `SingleThreadContext` or `Dispatchers.Default` enforcement for native calls.
- **Fix**: Ensure `MediaPipePromptExecutor` uses a dedicated dispatcher for native interactions.

### E. Cancellation Granularity
- **Risk**: Canceling a native C++ inference call via MediaPipe/JNI can be tricky and may not happen instantly.
- **Gap**: `RealLlmEngineAdapter.cancelGeneration` is implemented, but the plumbing to effectively "interrupt" a native loop in `PromptExecutor` needs careful design to avoid resource leaks.

## 3. Sufficient Components (No Action Needed)

- **`DynamicLlmEngine`**: Effectively handles fallback and routing.
- **`ModelCompatibilityChecker`**: Sufficient for filtering models by extension.
- **`BackendManager`**: Correctly abstracts availability and selection.
- **`ObserveChatReadinessUseCase`**: Successfully unifies all states into a single UI-facing contract.

## 4. Recommendations

1. **Inject Context**: Pass the `ApplicationContext` to `MediaPipeLlmEngineAdapter` via DI (`AppContainer`).
2. **Add Formatter**: Create `MediaPipePromptFormatter` to handle Gemma-specific tokens.
3. **Refine Progress**: If MediaPipe allows progress callbacks during load, upgrade the `initializeModel` signature.
