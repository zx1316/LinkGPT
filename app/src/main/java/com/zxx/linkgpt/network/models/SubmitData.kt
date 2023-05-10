package com.zxx.linkgpt.network.models

import com.fasterxml.jackson.annotation.JsonProperty
import com.zxx.linkgpt.data.models.BotHistoryData
import java.util.Calendar

data class SubmitData(
    @JsonProperty("user_name")
    var userName: String,
    @JsonProperty("bot")
    var bot: String,
    @JsonProperty("settings")
    var settings: String,
    @JsonProperty("use_template")
    var useTemplate: Boolean,
    @JsonProperty("history")
    var history: ArrayList<BotHistoryData> = ArrayList(),
    @JsonProperty("summary")
    var summary: String,
    @JsonProperty("summary_cutoff")
    var summaryCutoff: Calendar,
    @JsonProperty("temperature")
    var temperature: Float,
    @JsonProperty("top_p")
    var topP: Float,
    @JsonProperty("presence_penalty")
    var presencePenalty: Float,
    @JsonProperty("frequency_penalty")
    var frequencyPenalty: Float
)
