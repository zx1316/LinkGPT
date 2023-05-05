package com.zxx.linkgpt.ui.util

import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.zxx.linkgpt.R
import java.io.ByteArrayOutputStream

@Composable
fun MyErrorDialog(detail: String, callback: () -> Unit) {
    AlertDialog(
        onDismissRequest = callback,
        confirmButton = {
            TextButton(
                onClick = callback,
                content = { Text("确定") }
            )
        },
        title = { Text(text = "错误") },
        text = { Text(text = detail) }
    )
}

@Composable
fun MyAlertDialog(
    detail: String,
    cancelCallback: () -> Unit,
    confirmCallback: () -> Unit,
    confirmColor: Color = colors.primary
) {
    AlertDialog(
        onDismissRequest = cancelCallback,
        confirmButton = {
            TextButton(
                onClick = confirmCallback,
                content = { Text(text = "确定", color = confirmColor) }
            )
        },
        dismissButton = {
            TextButton(
                onClick = cancelCallback,
                content = { Text(text = "取消") }
            )
        },
        title = { Text(text = "警告") },
        text = { Text(text = detail) }
    )
}

@Composable
fun SingleLineInput(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = "",
    maxLength: Int = Int.MAX_VALUE,
    readOnly: Boolean = false,
    isError: Boolean = false
) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(text = title)
        TextField(
            value = value,
            onValueChange = {
                var len = 0
                var upper: Int = it.length
                for (i in it.indices) {
                    if (it[i] >= 128.toChar()) {
                        len += 2
                    } else {
                        len++
                    }
                    if (len > maxLength) {
                        upper = i
                        break
                    }
                }
                onValueChange(it.substring(0, upper))
            },
            maxLines = 1,
            singleLine = true,
            modifier = modifier.fillMaxWidth(),
            readOnly = readOnly,
            placeholder = { Text(text = placeholder) },
            isError = isError
        )
    }
}

@Composable
fun AvatarChooser(context: Context, uri: Uri, callback: (Uri) -> Unit, defaultPainter: Painter) {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { if (it.resultCode == Activity.RESULT_OK) callback(it.data?.data as Uri) }
    )
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(text = stringResource(id = R.string.avatar))
        Row {
            val imageModifier = Modifier.size(128.dp).clip(RoundedCornerShape(100))
            if (Uri.EMPTY.equals(uri)) {
                Image(
                    painter = defaultPainter,
                    contentDescription = null,
                    modifier = imageModifier
                )
            } else {
                tryReadBitmap(context.contentResolver, uri)?.asImageBitmap()?.let {
                    Image(
                        bitmap = it,
                        contentDescription = null,
                        modifier = imageModifier,
                        contentScale = ContentScale.Crop
                    )
                }
            }
            Spacer(modifier = Modifier.width(64.dp))
            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.height(128.dp)
            ) {
                Button(
                    onClick = {
                        val intent = Intent(Intent.ACTION_PICK)
                        intent.type = "image/*"
                        launcher.launch(intent)
                    },
                    content = { Text(text = stringResource(id = R.string.choose_avatar)) }
                )
                Button(
                    onClick = { callback(Uri.EMPTY) },
                    content = { Text(text = stringResource(id = R.string.default_avatar)) }
                )
            }
        }
    }
}

@Composable
fun Avatar(bytes: ByteArray?, defaultPainter: Painter, size: Dp, clickCallback: (() -> Unit)? = null) {
    val imageModifier = if (clickCallback == null) {
        Modifier.size(size).clip(RoundedCornerShape(100))
    } else {
        Modifier.size(size).clip(RoundedCornerShape(100)).clickable { clickCallback() }
    }
    if (bytes != null) {
        Image(
            bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size).asImageBitmap(),
            contentDescription = null,
            modifier = imageModifier,
            contentScale = ContentScale.Crop
        )
    } else {
        Image(
            painter = defaultPainter,
            contentDescription = null,
            modifier = imageModifier
        )
    }
}

fun saveBitmap(context: Context, uri: Uri, filename: String) {
    val bitmap = tryReadBitmap(context.contentResolver, uri)
    if (bitmap != null) {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        byteArrayOutputStream.close()
        context.openFileOutput(filename, Context.MODE_PRIVATE).use {
            it.write(byteArrayOutputStream.toByteArray())
        }
    }
}

private fun tryReadBitmap(contentResolver: ContentResolver, data: Uri): Bitmap? {
    return try {
        val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val source = ImageDecoder.createSource(contentResolver, data)
            ImageDecoder.decodeBitmap(source)
        } else {
            MediaStore.Images.Media.getBitmap(contentResolver, data)
        }
        ThumbnailUtils.extractThumbnail(bitmap, 256, 256)
    } catch (e: Exception) {
        null
    }
}

fun showToast(text: String, context: Context) {
    Toast.makeText(context, text, Toast.LENGTH_LONG).show()
}
