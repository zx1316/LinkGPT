package com.zxx.linkgpt.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.zxx.linkgpt.ui.AddOrConfigBot
import com.zxx.linkgpt.ui.Chat
import com.zxx.linkgpt.ui.ListBot
import com.zxx.linkgpt.ui.UserConfig
import com.zxx.linkgpt.viewmodel.LinkGPTViewModel

@Composable
fun LinkGPTNavHost(navController: NavHostController, vm: LinkGPTViewModel) {
    NavHost(
        navController = navController,
        startDestination = ListBot.route
    ) {
        composable(route = ListBot.route) {
            ListBot(
                vm = vm,
                onClickAdd = { navController.navigate(AddBot.route) },
                onClickConfig = { navController.navigate(UserConfig.route) },
                onClickChat = { navController.navigate(Chat.route) }
            )
        }

        composable(route = AddBot.route) {
            AddOrConfigBot(
                vm = vm,
                onClickBack = { navController.popBackStack() },
                onClickDelete = {
                    navController.popBackStack()
                    navController.popBackStack()
                },
                isConfig = false
            )
        }

        composable(route = BotConfig.route) {
            AddOrConfigBot(
                vm = vm,
                onClickBack = { navController.popBackStack() },
                onClickDelete = {
                    navController.popBackStack()
                    navController.popBackStack()
                },
                isConfig = true
            )
        }

        composable(route = UserConfig.route) {
            UserConfig(
                vm = vm,
                onClickBack = { navController.popBackStack() }
            )
        }

        composable(route = Chat.route) {
            Chat(
                vm = vm,
                onClickBack = { navController.popBackStack() },
                onClickConfig = { navController.navigate(BotConfig.route) }
            )
        }
    }
}
