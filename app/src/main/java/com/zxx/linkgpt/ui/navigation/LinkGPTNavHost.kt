package com.zxx.linkgpt.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.zxx.linkgpt.ui.AddBot
import com.zxx.linkgpt.ui.Config
import com.zxx.linkgpt.ui.ListBot
import com.zxx.linkgpt.viewmodel.LinkGPTViewModel

@Composable
fun LinkGPTNavHost() {
    val vm: LinkGPTViewModel = viewModel()
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = RouteConfig.ROUTE_LIST) {
        composable(RouteConfig.ROUTE_LIST) {
            ListBot(navController = navController, vm = vm)
        }

        composable(RouteConfig.ROUTE_ADD) {
            AddBot(navController = navController, vm = vm)
        }

        composable(RouteConfig.ROUTE_USER_CONFIG) {
            Config(navController = navController, vm = vm)
        }
    }
}
