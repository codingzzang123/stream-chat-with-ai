package com.aistreamchat.service

import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import java.time.Duration

@Component
class MockStreamService {

    private val responses = listOf(
        "안녕하세요! ",
        "저는 StreamChat ",
        "AI 어시스턴트입니다. ",
        "WebFlux SSE로 ",
        "토큰을 하나씩 ",
        "스트리밍하고 있어요. ",
        "이렇게 실시간으로 ",
        "응답이 흘러오는 게 ",
        "보이시나요? 😊"
    )

    fun streamTokens(message: String): Flux<String> =
        Flux.fromIterable(responses)
            .delayElements(Duration.ofMillis(120))
}