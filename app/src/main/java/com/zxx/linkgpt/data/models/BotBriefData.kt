package com.zxx.linkgpt.data.models

import android.net.Uri
import android.os.Parcelable
import androidx.room.Entity
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
@Entity(tableName="brief_table")
data class BotBriefData(
    var name: String,
    var output: String,
    var time: Date,
    var image: Uri,
    var personality: String
): Parcelable
