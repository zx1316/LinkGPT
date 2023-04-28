package com.zxx.linkgpt.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.zxx.linkgpt.ui.AddBot
import com.zxx.linkgpt.ui.Chat
import com.zxx.linkgpt.ui.UserConfig
import com.zxx.linkgpt.ui.ListBot
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
            AddBot(
                vm = vm,
                onClickBack = { navController.popBackStack() }
            )
        }

        composable(route = UserConfig.route) {
            UserConfig(
                vm = vm,
                onClickBack = { navController.popBackStack() }
            )
        }

//        composable(route = Chat.routeWithArgs, arguments = Chat.arguments) { navBackStackEntry ->
//            val accountType = navBackStackEntry.arguments?.getString(Chat.botArg)
//            accountType?.let {
//                Chat(vm = vm, bot = it)
//            }
//        }

        composable(route = Chat.route) {
            Chat(
                vm = vm,
                onClickBack = { navController.popBackStack() },
                onClickConfig = {}    // todo: complete botConfig view
            )
        }
    }
}
