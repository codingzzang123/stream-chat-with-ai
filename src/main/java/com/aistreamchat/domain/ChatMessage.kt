package com.aistreamchat.domain

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonSetter
import com.fasterxml.jackson.annotation.Nulls
import java.time.Instant

enum class Role { user, assistant}

data class ChatMessage(
    var role: Role,
    var content: String,
    var timestamp: Instant = Instant.now()
)

data class ChatRequest(
    var message: String,
    var conversationId: String? = null, // null 이면 새 대화
    @JsonSetter(nulls = Nulls.AS_EMPTY)
    var history: List<ChatMessage> = emptyList()
)

data class TokenChunk(
    var token: String,
    var conversationId: String,
    var done: Boolean = false   // 스트림 종료 신호
)