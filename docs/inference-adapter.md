# Inference Adapter Boundary

This project defines a low-level boundary for on-device backend runtimes.

The files below define the boundary for a real Android on-device backend:

- `domain/repository/InferenceAdapter.kt`
- `domain/repository/BackendCapabilityNegotiator.kt`
- `domain/model/InferenceContract.kt`
- `domain/model/BackendCapabilities.kt`
- `data/backend/real/*`

## Purpose

The adapter boundary separates backend-specific runtime work from app-facing chat
logic.

`LlmEngine` remains the app-facing generation contract.

`InferenceAdapter` is the lower-level backend contract for a future local
runtime such as:

- llama.cpp JNI
- MediaPipe LLM Inference
- ONNX Runtime

## Data Flow

1. **`LocalModelRegistry`**: User selects/loads a model.
2. **`ModelManager`**: Coordinates model lifecycle.
3. **`InferenceAdapter`**: The active adapter (e.g., MediaPipe) is initialized.
4. **`AdapterLlmEngine`**: A generic bridge that maps `LlmEngine.streamReply` to `InferenceAdapter.streamInference`.
5. **`SendMessageUseCase`**: Calls the engine with a `RenderedPrompt`.

## Why this split exists

By using a generic `AdapterLlmEngine`, we avoid duplicating chat-to-backend 
mapping logic for every new runtime. Every real backend only needs to implement 
the `InferenceAdapter` interface.
