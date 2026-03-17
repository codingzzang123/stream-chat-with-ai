package com.aistreamchat.domain

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

data class ClaudeRequest(
    var model:String,
    var messages: List<ClaudeMessage>,
    @JsonProperty("max_tokens")
    var token: Int,
    var stream: Boolean = true  //?
)


data class ClaudeMessage(
    var role:String,
    var content:String
)


@JsonIgnoreProperties(ignoreUnknown = true)
data class ClaudeStreamEvent(
    var type: String,
    var content: ClaudeStream
)


@JsonIgnoreProperties(ignoreUnknown = true)
data class ClaudeStream(
    var type:String? = null,
    var text:String? = null
)