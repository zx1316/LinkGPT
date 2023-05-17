package com.zxx.linkgpt.network.models

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

data class ReplyData(
    @JsonProperty("status")
    var status: String,
    @JsonProperty("message")
    var message: String,
    @JsonProperty("summary")
    var summary: String,
    @JsonProperty("start_time")
    var startTime: Calendar,
    @JsonProperty("summary_cutoff")
    var summaryCutoff: Calendar,
    @JsonProperty("last_usage")
    var lastUsage: Int,
    @JsonProperty("today_usage")
    var todayUsage: Int,
    @JsonProperty("max_usage")
    var maxUsage: Int
)
