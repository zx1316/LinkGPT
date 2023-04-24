package com.zxx.linkgpt.ui.util

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable

@Composable
fun ShowErrorDialog(detail: String, callback: () -> Unit) {
    AlertDialog(
        onDismissRequest = callback,
        confirmButton = {
            TextButton(
                onClick = callback,
            ) {
                Text(text = "确定")
            }
        },
        title = { Text(text = "错误") },
        text = { Text(text = detail) }
    )
}

@Composable
fun ShowAlertDialog(detail: String, cancelCallback: () -> Unit, confirmCallback: () -> Unit) {
    AlertDialog(
        onDismissRequest = cancelCallback,
        confirmButton = {
            TextButton(
                onClick = confirmCallback,
            ) {
                Text(text = "确定")
            }
        },
        dismissButton = {
            TextButton(
                onClick = cancelCallback
            ) {
                Text(text = "取消")
            }
        },
        title = { Text(text = "警告") },
        text = { Text(text = detail) }
    )
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
        null
    }
}

fun showToast(text: String, context: Context) {
    Toast.makeText(context, text, Toast.LENGTH_LONG).show()
}
