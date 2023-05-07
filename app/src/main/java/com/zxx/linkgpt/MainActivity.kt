package com.zxx.linkgpt

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.zxx.linkgpt.ui.navigation.LinkGPTNavHost
import com.zxx.linkgpt.ui.theme.LinkGPTTheme
import com.zxx.linkgpt.viewmodel.LinkGPTViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        // A hack way to fix the navigation empty screen bug in Xiaomi phones.
        // https://stackoverflow.com/questions/71363125/compose-navhost-start-the-white-screen
        lifecycleScope.launch {
            delay(100)
            window.setBackgroundDrawableResource(android.R.color.transparent)
        }
        setContent {
            LinkGPTTheme {
                @OptIn(ExperimentalAnimationApi::class)
                val navController = rememberAnimatedNavController()
                val vm: LinkGPTViewModel = viewModel()
                LinkGPTNavHost(navController = navController, vm = vm)
            }
        }
    }
}
