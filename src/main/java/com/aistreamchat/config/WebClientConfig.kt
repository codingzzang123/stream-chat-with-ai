package com.aistreamchat.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration

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
}