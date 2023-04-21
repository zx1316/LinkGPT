package com.zxx.linkgpt.ui.navigation

import android.content.ContentResolver
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.zxx.linkgpt.ui.AddBot
import com.zxx.linkgpt.ui.ListBot
import com.zxx.linkgpt.viewmodel.LinkGPTViewModel

@Composable
fun MyNavHost(contentResolver: ContentResolver) {
    val vm: LinkGPTViewModel = viewModel()
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = RouteConfig.ROUTE_LIST) {
        composable(RouteConfig.ROUTE_LIST) {
            ListBot(navController = navController, vm = vm)
        }

        composable(RouteConfig.ROUTE_ADD) {
            AddBot(contentResolver = contentResolver, navController = navController, vm = vm)
        }
    }
}
