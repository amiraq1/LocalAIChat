package com.localaichat.domain.model

data class ModelOption(
    val id: String,
    val name: String,
    val description: String,
    val contextWindow: Int,
    val capabilities: String,
    val sizeBytes: Long,
    val localPath: String,
    val isInstalled: Boolean,
    val status: ModelStatus = ModelStatus.NotLoaded,
    val compatibility: ModelCompatibility = ModelCompatibility.Compatible,
)
