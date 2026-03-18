package com.aistreamchat.handler

import com.aistreamchat.domain.ChatRequest
import com.aistreamchat.domain.TokenChunk
import com.aistreamchat.service.ClaudeStreamService
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.http.MediaType
import org.springframework.http.codec.ServerSentEvent
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono
import java.util.UUID

private val log = KotlinLogging.logger {}
@Component
class SseChatHandler(
    private val claudeStreamService: ClaudeStreamService
) {

    fun streamChat(request: ServerRequest): Mono<ServerResponse> {
        val conversationId = request.queryParam("conversationId")
            .orElse(UUID.randomUUID().toString())

        log.info { "Stream chat for conversation $conversationId" }

        val sseStream = request.bodyToMono(ChatRequest::class.java)
            .doOnNext { log.info { "Stream Response: $it" } }
            .doOnError { e ->
                log.error("파싱 실패 원인: {}", e.message)
                log.error("스택트레이스: ", e)  // ← 이거 추가
            }
            .flatMapMany { chatRequest ->
                claudeStreamService.streamResponse(chatRequest)
                    .map { token ->
                        // 토큰 청크를 SSE 이벤트로 래핑
                        ServerSentEvent.builder(
                            TokenChunk(
                                token = token,
                                conversationId = conversationId,
                                done = false
                            )
                        )
                            .event("token")
                            .build()
                    }
                    // 스트림 끝에 done 이벤트 추가
                    .concatWith(
                        Mono.just(
                            ServerSentEvent.builder(
                                TokenChunk(token = "", conversationId = conversationId, done = true)
                            )
                                .event("done")
                                .build()
                        )
                    )
            }
            .doOnCancel { println("[$conversationId] SSE 연결 종료") }

        return ServerResponse.ok()
            .contentType(MediaType.TEXT_EVENT_STREAM)
            .body(sseStream, ServerSentEvent::class.java)
    }
}