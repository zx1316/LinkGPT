package com.zxx.linkgpt.data.models

import android.net.Uri
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
@Entity(tableName="detail_table")
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
    var summary: String = "",
    var summaryTime: Date = Date(0L),
    var image: Uri
): Parcelable