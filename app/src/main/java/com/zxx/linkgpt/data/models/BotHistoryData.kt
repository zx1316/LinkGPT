package com.zxx.linkgpt.data.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
@Entity(tableName="history_table")
data class BotHistoryData (
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var name: String,
    var input: String,
    var output: String,
    var time: Date,
): Parcelable