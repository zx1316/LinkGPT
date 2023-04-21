package com.zxx.linkgpt.ui

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.*

class UIUtil {
    companion object {
        @JvmStatic
        val sdfHM = SimpleDateFormat("h:mm", Locale.CHINA)
        @JvmStatic
        val sdfE = SimpleDateFormat("E", Locale.CHINA)
        @JvmStatic
        val sdfYMD = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)
    }
}

fun tryReadBitmap(contentResolver: ContentResolver, data: Uri): Bitmap? {
    return try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val source = ImageDecoder.createSource(contentResolver, data)
            ImageDecoder.decodeBitmap(source)
        } else {
            MediaStore.Images.Media.getBitmap(contentResolver, data)
        }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

fun showToast(text: String, context: Context) {
    Toast.makeText(context, text, Toast.LENGTH_LONG).show()
}

fun timeFormatter(calendar: Calendar): String {
    val cCalendar = Calendar.getInstance()
    val yCalendar = Calendar.getInstance()
    val dbyCalendar = Calendar.getInstance()
    yCalendar.add(Calendar.DATE, -1)
    dbyCalendar.add(Calendar.DATE, -2)
    return if (cCalendar.get(Calendar.DATE) == calendar.get(Calendar.DATE)) {
        getPrefix(calendar.get(Calendar.HOUR_OF_DAY)) + UIUtil.sdfHM.format(calendar.time)
    } else if (yCalendar.get(Calendar.DATE) == calendar.get(Calendar.DATE)) {
        "昨天 " + getPrefix(calendar.get(Calendar.HOUR_OF_DAY)) + UIUtil.sdfHM.format(calendar.time)
    } else if (dbyCalendar.get(Calendar.DATE) == calendar.get(Calendar.DATE)) {
        "前天 " + getPrefix(calendar.get(Calendar.HOUR_OF_DAY)) + UIUtil.sdfHM.format(calendar.time)
    } else if (calendar.get(Calendar.WEEK_OF_YEAR) == cCalendar.get(Calendar.WEEK_OF_YEAR) && calendar.get(Calendar.YEAR) == cCalendar.get(Calendar.YEAR)) {
        UIUtil.sdfE.format(calendar.time)
    } else {
        UIUtil.sdfYMD.format(calendar.time)
    }
}

private fun getPrefix(h: Int): String {
    return if (h >= 18) "晚上"
    else if (h >= 12) "下午"
    else if (h >= 6) "上午"
    else "凌晨"
}
