package com.zxx.linkgpt.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "history_table")
data class BotHistoryData(
    @PrimaryKey(autoGenerate = false)
    var time: Calendar = Calendar.getInstance(),
    var name: String,
    var input: String? = null,
    var output: String? = null,
)
