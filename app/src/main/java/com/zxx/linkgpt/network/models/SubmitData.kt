package com.zxx.linkgpt.network.models

import com.fasterxml.jackson.annotation.JsonProperty

data class SubmitData(
    @JsonProperty("user_name")
    var userName: String,
    @JsonProperty("bot")
    var bot: String,
    @JsonProperty("settings")
    var settings: String,
    @JsonProperty("history")
    var history: ArrayList<String> = ArrayList(),
    @JsonProperty("summary")
    var summary: String,
    @JsonProperty("update_summary")
    var updateSummary: Boolean,
    @JsonProperty("temperature")
    var temperature: Float,
    @JsonProperty("top_p")
    var topP: Float,
    @JsonProperty("presence_penalty")
    var presencePenalty: Float,
    @JsonProperty("frequency_penalty")
    var frequencyPenalty: Float
)
