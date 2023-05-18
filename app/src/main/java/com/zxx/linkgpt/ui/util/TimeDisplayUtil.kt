package com.zxx.linkgpt.ui.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class TimeDisplayUtil {
    companion object {
        @JvmStatic
        private val sdfHM = SimpleDateFormat("h:mm", Locale.CHINA)
        @JvmStatic
        private val sdfE = SimpleDateFormat("E", Locale.CHINA)
        @JvmStatic
        private val sdfYMD = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)

        @JvmStatic
        fun formatTime(calendar: Calendar, detail: Boolean): String {
            val calendar1 = Calendar.getInstance()
            calendar1.set(Calendar.HOUR_OF_DAY, 0)
            calendar1.set(Calendar.MINUTE, 0)
            calendar1.set(Calendar.SECOND, 0)
            calendar1.set(Calendar.MILLISECOND, 0)
            val diffTime = calendar1.timeInMillis - calendar.timeInMillis
            return if (diffTime <= 0L) {
                getHMWithPrefix(calendar)
            } else if (diffTime <= 86400000L) {
                "昨天 " + getHMWithPrefix(calendar)
            } else if (diffTime <= 518400000L) {
                sdfE.format(calendar.time) + " " + getHMWithPrefix(calendar)
            } else {
                sdfYMD.format(calendar.time) + if (detail) " " + getHMWithPrefix(calendar) else ""
            }
        }

        @JvmStatic
        private fun getHMWithPrefix(calendar: Calendar): String {
            val h = calendar.get(Calendar.HOUR_OF_DAY)
            return (if (h >= 18) "晚上" else if (h >= 12) "下午" else if (h >= 6) "上午" else "凌晨") + sdfHM.format(calendar.time)
        }
    }
}

