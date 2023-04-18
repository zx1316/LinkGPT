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
    var lastTokens: Int,
    var totalTokens: Int,
    var temperature: Double,
    var topP: Double,
    var presencePenalty: Double,
    var frequencyPenalty: Double,
    var personality: String,
    var summary: String,
    var summaryTime: Date,
    var image: Uri
): Parcelable