package com.zxx.linkgpt.ui.navigation

import android.content.ContentResolver
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.zxx.linkgpt.ui.AddBot
import com.zxx.linkgpt.ui.ListBot

@Composable
fun MyNavHost(contentResolver: ContentResolver) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = RouteConfig.ROUTE_LIST) {
        composable(RouteConfig.ROUTE_LIST) {
            ListBot(contentResolver = contentResolver, navController = navController)
        }

        composable(RouteConfig.ROUTE_ADD) {
            AddBot(contentResolver = contentResolver, navController = navController)
        }
    }
}