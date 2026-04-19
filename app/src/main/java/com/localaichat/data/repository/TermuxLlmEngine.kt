package com.localaichat.data.repository

import com.localaichat.domain.model.GenerationConfig
import com.localaichat.domain.model.ModelOption
import com.localaichat.domain.model.RenderedPrompt
import com.localaichat.domain.repository.LlmEngine
import com.localaichat.domain.repository.SettingsRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.sse.EventSource
import okhttp3.sse.EventSourceListener
import okhttp3.sse.EventSources
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

class TermuxLlmEngine(
    private val settingsRepository: SettingsRepository? = null,
    private val fallbackUrl: String = "http://192.168.1.100:8080/v1/chat/completions",
) : LlmEngine {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(120, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    override fun streamReply(
        renderedPrompt: RenderedPrompt,
        config: GenerationConfig,
        model: ModelOption,
    ): Flow<String> = callbackFlow {
        val serverUrl = settingsRepository?.getServerUrl() ?: fallbackUrl

        val requestBody = """
            {
              "model": "${escapeJson(model.name)}",
              "messages": [
                {
                  "role": "user",
                  "content": "${escapeJson(renderedPrompt.text)}"
                }
              ],
              "temperature": ${config.temperature},
              "max_tokens": ${config.maxTokens},
              "stream": true
            }
        """.trimIndent().toRequestBody("application/json".toMediaType())

        val request = Request.Builder()
            .url(serverUrl)
            .header("Accept", "text/event-stream")
            .post(requestBody)
            .build()

        val listener = object : EventSourceListener() {
            override fun onEvent(
                eventSource: EventSource,
                id: String?,
                type: String?,
                data: String,
            ) {
                if (data == "[DONE]") {
                    channel.close()
                    eventSource.cancel()
                    return
                }
                try {
                    val json = JSONObject(data)
                    val choices = json.optJSONArray("choices")
                    if (choices != null && choices.length() > 0) {
                        val delta = choices.getJSONObject(0).optJSONObject("delta")
                        if (delta != null && delta.has("content")) {
                            val content = delta.optString("content", "")
                            if (content.isNotEmpty() && content != "null") {
                                trySend(content)
                            }
                        }
                    }
                } catch (_: Exception) {
                    // Ignore parse errors for partial chunks.
                }
            }

            override fun onClosed(eventSource: EventSource) {
                channel.close()
            }

            override fun onFailure(
                eventSource: EventSource,
                t: Throwable?,
                response: Response?,
            ) {
                val message = buildString {
                    append("Connection to llama.cpp server failed")
                    append(" ($serverUrl)")
                    response?.let { append(" — HTTP ${it.code}") }
                    t?.message?.let {
                        append(": ")
                        append(it)
                    }
                }
                channel.close(IOException(message, t))
            }
        }

        val eventSource = EventSources.createFactory(client).newEventSource(request, listener)
        awaitClose { eventSource.cancel() }
    }
}

private fun escapeJson(value: String): String = buildString {
    value.forEach { char ->
        when (char) {
            '\\' -> append("\\\\")
            '"' -> append("\\\"")
            '\n' -> append("\\n")
            '\r' -> append("\\r")
            '\t' -> append("\\t")
            else -> append(char)
        }
    }
}
