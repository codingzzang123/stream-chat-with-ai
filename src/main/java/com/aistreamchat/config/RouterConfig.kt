package com.aistreamchat.config

import com.aistreamchat.handler.SseChatHandler;
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.router

@Configuration
class RouterConfig(
    private val sseChatHandler:SseChatHandler
) {

    @Bean
    fun chatRouter(): RouterFunction<ServerResponse> = router {
        "/api/chat".nest {
            // POST /api/chat/stream?conversationId=xxx
            POST("/stream", sseChatHandler::streamChat)
        }
    }
}