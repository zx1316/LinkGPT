package com.zxx.linkgpt.data.models

import java.util.Calendar

data class BotBriefData(
    var name: String,
    var output: String?,
    var time: Calendar,
)
