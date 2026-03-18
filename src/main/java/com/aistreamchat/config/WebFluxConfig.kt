//package com.aistreamchat.config
//
//import com.fasterxml.jackson.databind.ObjectMapper
//import org.springframework.context.annotation.Configuration
//import org.springframework.http.codec.ServerCodecConfigurer
//import org.springframework.http.codec.json.Jackson2JsonDecoder
//import org.springframework.http.codec.json.Jackson2JsonEncoder
//import org.springframework.web.reactive.config.WebFluxConfigurer
//
//@Configuration
//class WebFluxConfig(
//    private val objectMapper: ObjectMapper
//) : WebFluxConfigurer {
//
//    override fun configureHttpMessageCodecs(configurer: ServerCodecConfigurer) {
//        val decoder = Jackson2JsonDecoder(objectMapper)
//        val encoder = Jackson2JsonEncoder(objectMapper)
//
//        configurer.customCodecs().register(decoder)
//        configurer.customCodecs().register(encoder)
//    }
//}