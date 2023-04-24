package com.zxx.linkgpt.network.models

import com.fasterxml.jackson.annotation.JsonProperty

data class UserDetailData(
    @JsonProperty("valid")
    var valid: Boolean = false,
    @JsonProperty("today_usage")
    var todayUsage: Int = 0,
    @JsonProperty("max_usage")
    var maxUsage: Int = 0
)
