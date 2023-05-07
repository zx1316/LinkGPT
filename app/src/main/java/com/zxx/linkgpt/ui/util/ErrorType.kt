package com.zxx.linkgpt.ui.util

enum class ErrorType {
    NONE,
    USER_NAME_EMPTY,
    USER_NAME_TOO_LONG,
    BOT_NAME_USER,
    BOT_NAME_EMPTY,
    BOT_NAME_DUPLICATE,
    BOT_NAME_TOO_LONG,
    SETTINGS_TOO_LONG,
    CHAT_TOO_LONG,
    HOST_EMPTY,
    PORT_INCORRECT
}