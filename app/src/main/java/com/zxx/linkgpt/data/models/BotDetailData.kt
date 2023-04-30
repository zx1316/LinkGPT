package com.zxx.linkgpt.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "detail_table")
data class BotDetailData(
    @PrimaryKey(autoGenerate = false)
    var name: String = "",
    var lastUsage: Int = 0,
    var totalUsage: Int = 0,
    var temperature: Float = 1.0F,
    var topP: Float = 1.0F,
    var presencePenalty: Float = 0.0F,
    var frequencyPenalty: Float = 0.0F,
    var settings: String = "",
    var summary: String = "None",
    var startTime: Calendar = Calendar.getInstance(),
    var summaryCutoff: Calendar = Calendar.getInstance(),
)
