package com.zxx.linkgpt.ui.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class TimeDisplayUtil {
    companion object {
        @JvmStatic
        val sdfHM = SimpleDateFormat("h:mm", Locale.CHINA)

        @JvmStatic
        val sdfE = SimpleDateFormat("E", Locale.CHINA)

        @JvmStatic
        val sdfYMD = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)

        @JvmStatic
        fun formatTime(calendar: Calendar): String {
            val cCalendar = Calendar.getInstance()
            val yCalendar = Calendar.getInstance()
            yCalendar.add(Calendar.DATE, -1)
            return if (cCalendar.get(Calendar.DATE) == calendar.get(Calendar.DATE)) {
                getPrefix(calendar.get(Calendar.HOUR_OF_DAY)) + sdfHM.format(calendar.time)
            } else if (yCalendar.get(Calendar.DATE) == calendar.get(Calendar.DATE)) {
                "昨天 " + getPrefix(calendar.get(Calendar.HOUR_OF_DAY)) + sdfHM.format(calendar.time)
            } else if (calendar.get(Calendar.WEEK_OF_YEAR) == cCalendar.get(Calendar.WEEK_OF_YEAR) && calendar.get(
                    Calendar.YEAR
                ) == cCalendar.get(Calendar.YEAR)
            ) {
                sdfE.format(calendar.time)
            } else {
                sdfYMD.format(calendar.time)
            }
        }

        @JvmStatic
        private fun getPrefix(h: Int): String {
            return if (h >= 18) "晚上"
            else if (h >= 12) "下午"
            else if (h >= 6) "上午"
            else "凌晨"
        }
    }
}

