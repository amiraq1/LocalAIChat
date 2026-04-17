package com.localaichat.domain.model

sealed interface GenerationEvent {
    data class Started(val model: ModelOption) : GenerationEvent
    data class Delta(val chunk: String, val accumulatedText: String) : GenerationEvent
    data class Completed(val messageId: Long, val fullText: String) : GenerationEvent
    data class Failed(val messageId: Long, val reason: String) : GenerationEvent
    data class Rejected(val reason: String) : GenerationEvent
}
