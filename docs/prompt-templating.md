# Prompt Templating Layer

This document describes the abstraction layer for converting conversation history
into raw text prompts for local LLMs.

## Overview

Different LLMs require specific formatting tokens to distinguish between user,
assistant, and system messages. The `PromptFormatter` interface provides a
pluggable strategy to handle these differences without polluting the chat
domain or UI logic.

## Core Abstraction

### `PromptFormatter`
The interface located in `com.localaichat.domain.repository`.

```kotlin
interface PromptFormatter {
    fun format(history: List<ChatMessage>, systemPrompt: String? = null): String
}
```

## Available Formatters

- **`GemmaPromptFormatter`**: Designed for Google Gemma 1/2. Uses `<start_of_turn>` and `<end_of_turn>`.
- **`Llama3PromptFormatter`**: Designed for Meta Llama 3. Uses `<|start_header_id|>` and `<|begin_of_text|>`.
- **`PlainPromptFormatter`**: Simple "User: / Assistant: " text concatenation.

## Backend Usage

When a real backend session is initialized (e.g., in `MediaPipeBackendSession`),
it should select an appropriate `PromptFormatter` based on the loaded model.

### Integration in MediaPipe

Inside `MediaPipePromptExecutor`:

```kotlin
// Inside execute(request)
val formatter = getFormatterForModel(request.model.id)
val rawPrompt = formatter.format(request.history, "You are a helpful assistant.")

// Call MediaPipe native inference
llmInference.generateResponseAsync(rawPrompt)
```

## Future Extensibility

New model architectures can be supported by:
1. Implementing the `PromptFormatter` interface.
2. Registering the new formatter in the backend runtime that supports that model.
