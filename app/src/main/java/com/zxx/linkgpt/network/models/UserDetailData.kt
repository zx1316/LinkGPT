package com.zxx.linkgpt.network.models

import com.fasterxml.jackson.annotation.JsonProperty

data class UserDetailData(
    @JsonProperty("authorized")
    var authorized: Boolean = false,
    @JsonProperty("today_usage")
    var todayUsage: Int = 0,
    @JsonProperty("max_usage")
    var maxUsage: Int = 0
)
