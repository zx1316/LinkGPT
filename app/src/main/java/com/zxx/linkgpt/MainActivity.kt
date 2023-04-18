package com.zxx.linkgpt

import android.app.Activity
import android.content.ContentResolver
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.zxx.linkgpt.ui.theme.LinkGPTTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LinkGPTTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting(contentResolver)
                }
            }
        }
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

@Composable
fun Greeting(contentResolver: ContentResolver) {
    var uri by rememberSaveable {
        mutableStateOf(Uri.EMPTY)
    }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == Activity.RESULT_OK) {
            uri = it.data?.data as Uri
        }
    }
    Column(modifier = Modifier.fillMaxWidth()) {
        Button(onClick = {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            launcher.launch(intent);
        }) {
            Text(text = "Select an image")
        }
        if (!Uri.EMPTY.equals(uri)) {
            tryReadBitmap(contentResolver, uri)?.asImageBitmap()?.let {
                Image(
                    bitmap = it,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(400.dp),
                    contentScale = ContentScale.Fit
                )
            }
        }
    }
}