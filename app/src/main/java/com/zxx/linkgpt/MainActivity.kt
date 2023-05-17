package com.zxx.linkgpt

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.knuddels.jtokkit.Encodings
import com.knuddels.jtokkit.api.Encoding
import com.knuddels.jtokkit.api.EncodingType
import com.zxx.linkgpt.ui.navigation.LinkGPTNavHost
import com.zxx.linkgpt.ui.theme.LinkGPTTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    companion object {
        @JvmStatic
        lateinit var enc: Encoding
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        // A hack way to fix the navigation empty screen bug in Xiaomi Android 12? phones.
        // https://stackoverflow.com/questions/71363125/compose-navhost-start-the-white-screen
        lifecycleScope.launch {
            delay(100)
            window.setBackgroundDrawableResource(android.R.color.transparent)
        }
        setContent {
            LinkGPTTheme {
                var flag by rememberSaveable { mutableStateOf(false) }
                val scope = rememberCoroutineScope()
                LaunchedEffect(null) {
                    scope.launch(Dispatchers.IO) {
                        enc = Encodings.newDefaultEncodingRegistry().getEncoding(EncodingType.CL100K_BASE)
                        flag = true
                    }
                }
                if (flag) {
                    LinkGPTNavHost()
                } else {
                    Surface(
                        color = colors.background,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Column(
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator(modifier = Modifier.padding(8.dp))
                            Text(text = stringResource(id = R.string.init))
                        }
                    }
                }
            }
        }
    }
}
