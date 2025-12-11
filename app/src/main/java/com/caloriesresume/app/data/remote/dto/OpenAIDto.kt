package com.caloriesresume.app.data.remote.dto

import com.google.gson.annotations.SerializedName

data class OpenAIRequest(
    val model: String,
    val messages: List<Message>,
    val temperature: Double = 0.7,
    @SerializedName("max_tokens")
    val maxTokens: Int? = null
)

data class Message(
    val role: String,
    val content: String
)

data class OpenAIResponse(
    val id: String?,
    val choices: List<Choice>?,
    val usage: Usage?
)

data class Choice(
    val message: Message?,
    val finishReason: String?
)

data class Usage(
    val promptTokens: Int?,
    val completionTokens: Int?,
    val totalTokens: Int?
)


