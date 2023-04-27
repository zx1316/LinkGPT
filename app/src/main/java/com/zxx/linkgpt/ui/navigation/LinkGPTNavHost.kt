package com.zxx.linkgpt.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.zxx.linkgpt.ui.AddBot
import com.zxx.linkgpt.ui.Config
import com.zxx.linkgpt.ui.ListBot
import com.zxx.linkgpt.viewmodel.LinkGPTViewModel

@Composable
fun MyNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val vm: LinkGPTViewModel = viewModel()
    NavHost(
        navController = navController,
        startDestination = ListBot.route,
        modifier = modifier
    ) {
        composable(route = ListBot.route) {
            ListBot(
                vm = vm,
                onClickAdd = { navController.navigate(AddBot.route) },
                onClickConfig = { navController.navigate(UserConfig.route) }
            )
        }

        composable(route = AddBot.route) {
            AddBot(
                vm = vm,
                onClickBack = { navController.popBackStack() }
            )
        }

        composable(route = UserConfig.route) {
            Config(
                vm = vm,
                onClickBack = { navController.popBackStack() }
            )
        }
    }
}