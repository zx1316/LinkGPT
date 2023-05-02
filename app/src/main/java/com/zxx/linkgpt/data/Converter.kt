package com.zxx.linkgpt.data

import androidx.room.TypeConverter
import java.util.Calendar
import java.util.Date

class Converter {
    @TypeConverter
    fun fromCalendar(value: Calendar): Long {
        return value.timeInMillis
    }

    @TypeConverter
    fun toCalendar(value: Long): Calendar {
        val calendar = Calendar.getInstance()
        calendar.time = Date(value)
        return calendar
    }
}
