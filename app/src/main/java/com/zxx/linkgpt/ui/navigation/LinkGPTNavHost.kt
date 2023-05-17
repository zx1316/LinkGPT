package com.zxx.linkgpt.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.zxx.linkgpt.ui.AddOrConfigBot
import com.zxx.linkgpt.ui.Chat
import com.zxx.linkgpt.ui.ListBot
import com.zxx.linkgpt.ui.UserConfig
import com.zxx.linkgpt.viewmodel.LinkGPTViewModel

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun LinkGPTNavHost(
    navController: NavHostController = rememberAnimatedNavController(),
    vm: LinkGPTViewModel = viewModel()
) {
    val left = AnimatedContentTransitionScope.SlideDirection.Left
    val right = AnimatedContentTransitionScope.SlideDirection.Right
    val duration = 400

    AnimatedNavHost(navController = navController, startDestination = ListBot.route) {
        composable(
            route = ListBot.route,
            exitTransition = {
                slideOutOfContainer(
                    towards = if (targetState.destination.route == UserConfig.route) right else left,
                    animationSpec = tween(duration)
                )
            },
            popEnterTransition = {
                slideIntoContainer(
                    towards = if (initialState.destination.route == UserConfig.route) left else right,
                    animationSpec = tween(duration)
                )
            }
        ) {
            ListBot(
                vm = vm,
                onClickAdd = { navController.navigate(AddBot.route) },
                onClickConfig = { navController.navigate(UserConfig.route) },
                onClickChat = { navController.navigate(Chat.route) }
            )
        }

        composable(
            route = AddBot.route,
            enterTransition = { slideIntoContainer(towards = left, animationSpec = tween(duration)) },
            exitTransition = { slideOutOfContainer(towards = right, animationSpec = tween(duration)) }
        ) {
            AddOrConfigBot(
                vm = vm,
                onClickBack = { navController.popBackStack() },
                isConfig = false
            )
        }

        composable(
            route = BotConfig.route,
            enterTransition = { slideIntoContainer(towards = left, animationSpec = tween(duration)) },
            exitTransition = { slideOutOfContainer(towards = right, animationSpec = tween(duration)) }
        ) {
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

        composable(
            route = UserConfig.route,
            enterTransition = { slideIntoContainer(towards = right, animationSpec = tween(duration)) },
            exitTransition = { slideOutOfContainer(towards = left, animationSpec = tween(duration)) }
        ) {
            UserConfig(
                vm = vm,
                onClickBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Chat.route,
            enterTransition = { slideIntoContainer(towards = left, animationSpec = tween(duration)) },
            popEnterTransition = { slideIntoContainer(towards = right, animationSpec = tween(duration)) },
            exitTransition = { slideOutOfContainer(towards = left, animationSpec = tween(duration)) },
            popExitTransition = { slideOutOfContainer(towards = right, animationSpec = tween(duration)) }
        ) {
            Chat(
                vm = vm,
                onClickBack = { navController.popBackStack() },
                onClickConfig = { navController.navigate(BotConfig.route) }
            )
        }
    }
}
