package com.localaichat.domain.model

data class LocalModel(
    val id: String,
    val displayName: String,
    val description: String,
    val sizeBytes: Long,
    val localPath: String?,
    val isInstalled: Boolean,
    val state: LocalModelState,
    val isSelected: Boolean,
    val isActive: Boolean,
)
