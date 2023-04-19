package com.zxx.linkgpt

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zxx.linkgpt.data.models.BotBriefData
import com.zxx.linkgpt.viewmodel.TestViewModel
import com.zxx.linkgpt.ui.theme.LinkGPTTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LinkGPTTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
//                    Greeting(contentResolver)
                    CreateBot(contentResolver)
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
fun BotListView(vm: TestViewModel = viewModel()) {
    val botList by vm.botList.collectAsState()
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(items = botList) { briefData ->
            BotCard(briefData = briefData)
        }
    }
}

@Composable
fun BotCard(briefData: BotBriefData) {
    Text(text = briefData.name, modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp))
}