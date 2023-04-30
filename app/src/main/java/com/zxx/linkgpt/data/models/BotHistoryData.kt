package com.zxx.linkgpt.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

@Entity(tableName = "history_table")
data class BotHistoryData(
    @PrimaryKey(autoGenerate = false)
    @JsonProperty("time")
    var time: Calendar = Calendar.getInstance(),
    @JsonIgnore
    var name: String,
    @JsonProperty("input")
    var input: String? = null,
    @JsonProperty("output")
    var output: String? = null,
)
