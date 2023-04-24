package com.zxx.linkgpt.network.models

import com.fasterxml.jackson.annotation.JsonProperty

data class SubmitData(
    var user: String,
    var bot: String,
    var prompt: ArrayList<MessageData>,
    var temperature: Float,
    @JsonProperty("top_p")
    var topP: Float,
    @JsonProperty("presence_penalty")
    var presencePenalty: Float,
    @JsonProperty("frequency_penalty")
    var frequencyPenalty: Float
)
