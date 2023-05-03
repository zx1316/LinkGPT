package com.zxx.linkgpt

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.zxx.linkgpt.ui.navigation.LinkGPTNavHost
import com.zxx.linkgpt.ui.theme.LinkGPTTheme
import com.zxx.linkgpt.viewmodel.LinkGPTViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // A hack way to fix the navigation empty screen bug in Xiaomi phones.
        // https://stackoverflow.com/questions/71363125/compose-navhost-start-the-white-screen
        lifecycleScope.launch {
            delay(100)
            window.setBackgroundDrawableResource(android.R.color.transparent)
        }
        setContent {
            LinkGPTTheme {
                val navController = rememberNavController()
                val vm: LinkGPTViewModel = viewModel()
                LinkGPTNavHost(navController = navController, vm = vm)
            }
        }
    }
}
