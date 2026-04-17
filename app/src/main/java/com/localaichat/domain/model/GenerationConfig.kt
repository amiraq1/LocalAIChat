package com.localaichat.domain.model

data class GenerationConfig(
    val maxTokens: Int = 512,
    val temperature: Float = 0.7f,
)
