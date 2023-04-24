package com.zxx.linkgpt.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "detail_table")
data class BotDetailData(
    @PrimaryKey(autoGenerate = false)
    var name: String,
    var lastTokens: Int = 0,
    var totalTokens: Int = 0,
    var temperature: Float,
    var topP: Float,
    var presencePenalty: Float,
    var frequencyPenalty: Float,
    var settings: String,
    var summary: String = "None",
    var summaryTime: Calendar = Calendar.getInstance(),
)
