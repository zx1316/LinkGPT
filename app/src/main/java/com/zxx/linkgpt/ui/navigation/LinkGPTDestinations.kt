package com.zxx.linkgpt.ui.navigation

interface MyDestination {
    val route: String
}

object ListBot: MyDestination {
    override val route = "list_bot"
}

object AddBot: MyDestination {
    override val route = "add_bot"
}

object UserConfig: MyDestination {
    override val route = "user_config"
}