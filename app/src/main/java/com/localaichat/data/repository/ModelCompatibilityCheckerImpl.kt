package com.localaichat.data.repository

import com.localaichat.domain.model.BackendType
import com.localaichat.domain.model.ModelCompatibility
import com.localaichat.domain.model.ModelOption
import com.localaichat.domain.repository.ModelCompatibilityChecker

/**
 * Implementation of [ModelCompatibilityChecker] with rules for current backends.
 */
class ModelCompatibilityCheckerImpl : ModelCompatibilityChecker {

    override fun checkCompatibility(model: ModelOption, backendType: BackendType): ModelCompatibility {
        return when (backendType) {
            BackendType.FAKE -> {
                // Fake backend is always compatible with any model for testing.
                ModelCompatibility.Compatible
            }

            BackendType.MEDIAPIPE -> {
                // MediaPipe requires models in .bin format.
                if (model.localPath.endsWith(".bin")) {
                    ModelCompatibility.Compatible
                } else {
                    ModelCompatibility.Incompatible(
                        "MediaPipe requires models in .bin format. This model is not compatible."
                    )
                }
            }

            BackendType.ONNX_RUNTIME -> {
                // Placeholder rule for ONNX.
                if (model.localPath.endsWith(".onnx")) {
                    ModelCompatibility.Compatible
                } else {
                    ModelCompatibility.Incompatible(
                        "ONNX Runtime requires .onnx models."
                    )
                }
            }

            BackendType.LLAMA_CPP -> {
                // Placeholder rule for llama.cpp.
                if (model.localPath.endsWith(".gguf")) {
                    ModelCompatibility.Compatible
                } else {
                    ModelCompatibility.Incompatible(
                        "llama.cpp requires .gguf models."
                    )
                }
            }
        }
    }
}
