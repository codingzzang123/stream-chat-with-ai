package com.aistreamchat.service

import com.aistreamchat.domain.ChatRequest
import com.aistreamchat.domain.ClaudeMessage
import com.aistreamchat.domain.ClaudeRequest
import com.aistreamchat.domain.ClaudeStreamEvent
import com.fasterxml.jackson.databind.ObjectMapper
import io.github.oshai.kotlinlogging.KotlinLogging
import lombok.extern.slf4j.Slf4j
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.core.io.buffer.DataBufferUtils
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

private val log = KotlinLogging.logger {}

@Service
@Slf4j
class ClaudeStreamService (
    private val claudeWebClient: WebClient,
    private val mockStreamService: MockStreamService,
    private val objectMapper: ObjectMapper,
    @Value("\${anthropic.api-key}") private val apiKey: String,
    @Value("\${anthropic.model}") private val model: String,
    @Value("\${anthropic.max-tokens}") private val maxTokens: Int
) {

    fun streamResponse(request: ChatRequest): Flux<String> {
        // API 키 없으면 Mock으로 대체
        if (apiKey == "mock") {
            log.info { "This is mock.. $request" }
            return mockStreamService.streamTokens(request.message)
        }

        log.info { "This is AI Claude.. $request" }
        return callClaudeApi(request)
    }


    private fun callClaudeApi(request: ChatRequest): Flux<String> {
        val messages = buildMessages(request)

        val claudeRequest = ClaudeRequest(
            model = model,
            messages = messages,
            maxTokens = maxTokens,
            stream = true
        )
//        return claudeWebClient.post()
//            .uri("/v1/messages")
//            .contentType(MediaType.APPLICATION_JSON)
//            .accept(MediaType.TEXT_EVENT_STREAM)
//            .bodyValue(claudeRequest)
//            .retrieve()
//            .bodyToFlux(DataBuffer::class.java)  // ← raw 바이트로 읽기
//            .map { buffer ->
//                val bytes = ByteArray(buffer.readableByteCount())
//                buffer.read(bytes)
//                DataBufferUtils.release(buffer)
//                String(bytes, Charsets.UTF_8)
//            }
//            .doOnNext { line -> log.debug { "Claude 원본 응답: $line" } }
//            .mapNotNull { line -> parseToken(line) }
//            .filter { it.isNotEmpty() }
//            .doOnError { e -> log.error { "Claude API 에러: ${e.message}" } }

        return claudeWebClient.post()
            .uri("/v1/messages")
            .accept(MediaType.TEXT_EVENT_STREAM)
            .bodyValue(claudeRequest)
            .retrieve()
            .bodyToFlux(String::class.java)  // ← Kotlin 확장함수 말고 이걸로
            .doOnNext { line -> log.debug { "Claude 원본 응답: $line" } }
            .mapNotNull { line -> parseToken(line) }
            .filter { it.isNotEmpty() }
            .doOnError { e -> log.error { "Claude API 에러: ${e.message}" } }
    }

    /**
     * Claude SSE 응답 파싱
     * "data: {...}" 형태에서 실제 토큰 텍스트만 추출
     */
    private fun parseToken(line: String): String? {
        // data: 접두사 제거 (있으면 제거, 없으면 그대로)
        val json = if (line.startsWith("data: ")) {
            line.removePrefix("data: ").trim()
        } else {
            line.trim()
        }

        if (json.isEmpty() || json == "[DONE]") return null

        return try {
            val event = objectMapper.readValue(json, ClaudeStreamEvent::class.java)
            if (event.type == "content_block_delta" && event.delta?.type == "text_delta") {
                event.delta?.text
            } else null
        } catch (e: Exception) {
            null
        }
    }

    private fun buildMessages(request: ChatRequest): List<ClaudeMessage> {
        val history = request.history.map {
            ClaudeMessage(role = it.role.name, content = it.content)
        }
        val current = ClaudeMessage(role = "user", content = request.message)
        return history + current
    }
}