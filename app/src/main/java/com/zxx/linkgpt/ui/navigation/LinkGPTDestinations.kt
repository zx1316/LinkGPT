package com.zxx.linkgpt.ui.navigation

interface LinkGPTDestination {
    val route: String
}

object ListBot: LinkGPTDestination {
    override val route = "list_bot"
}

object AddBot: LinkGPTDestination {
    override val route = "add_bot"
}

object UserConfig: LinkGPTDestination {
    override val route = "user_config"
}

object BotConfig: LinkGPTDestination {
    override val route = "bot_config"
}

object Chat: LinkGPTDestination {
    override val route: String = "chat"
}
