package com.zxx.linkgpt.data.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
@Entity(tableName = "history_table")
data class BotHistoryData(
    @PrimaryKey(autoGenerate = false)
    var time: Calendar = Calendar.getInstance(),
    var name: String,
    var input: String? = null,
    var output: String? = null,
) : Parcelable
