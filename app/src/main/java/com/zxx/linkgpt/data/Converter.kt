package com.zxx.linkgpt.data

import android.net.Uri
import androidx.room.TypeConverter
import java.util.*

class Converter {
    @TypeConverter
    fun fromDate(value: Date): Long {
        return value.time
    }

    @TypeConverter
    fun toDate(value: Long): Date {
        return Date(value)
    }

    @TypeConverter
    fun fromUri(value: Uri): String {
        return value.toString()
    }

    @TypeConverter
    fun toUri(value: String): Uri {
        return Uri.parse(value)
    }
}