package com.zxx.linkgpt.network.models

import com.fasterxml.jackson.annotation.JsonProperty

data class ReplyData(
    @JsonProperty("status")
    var status: String,
    @JsonProperty("message")
    var message: String,
    @JsonProperty("new_summary")
    var newSummary: String,
    @JsonProperty("last_usage")
    var lastUsage: Int,
    @JsonProperty("today_usage")
    var todayUsage: Int,
    @JsonProperty("max_usage")
    var maxUsage: Int
)
