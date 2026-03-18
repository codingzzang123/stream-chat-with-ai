package com.aistreamchat.config

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import org.springframework.boot.http.codec.CodecCustomizer
import org.springframework.core.codec.StringDecoder
import org.springframework.http.codec.json.Jackson2JsonDecoder
import org.springframework.http.codec.json.Jackson2JsonEncoder

@Configuration
class WebClientConfig {

    /*
        초기화 이후에 값이 변할 수 있는 변수에는 lateinit을,
        처음 초기화 된 직후부터 계속 read-only로만 쓰이는 변수에는 by lazy를 사용하는 것이 좋다.
     */
    @Value("\${anthropic.api-key}")
    private lateinit var API_KEY :String

    @Value("\${anthropic.base-url}")
    private lateinit var BASE_URL :String

    @Value("\${anthropic.model}")
    private lateinit var MODEL:String

    @Value("\${anthropic.version}")
    private lateinit var VERSION: String

    @Value("\${anthropic.max-tokens}")
    private var MAX_TOKENS :Int=50

    @Bean
    fun claudeWebClient() : WebClient = WebClient.builder()
        .baseUrl(BASE_URL)
        .defaultHeader("content-type", "application/json")
        .defaultHeader("x-api-key", API_KEY)
        .defaultHeader("anthropic-version", VERSION)
        .codecs { configurer ->
            configurer.defaultCodecs().maxInMemorySize(2 * 1024 * 1024)
            // SSE 스트림을 String으로 읽기 위한 설정 추가
            configurer.defaultCodecs().serverSentEventDecoder(
                StringDecoder.textPlainOnly()
            ) }
        .build()


    @Bean
    fun objectMapper(): ObjectMapper = ObjectMapper()
        .registerModule(KotlinModule.Builder().build())
        .registerModule(JavaTimeModule())
        .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
}