package com.zxx.linkgpt.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material.MaterialTheme.shapes
import androidx.compose.material.MaterialTheme.typography
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zxx.linkgpt.R
import com.zxx.linkgpt.ui.theme.BottomBarWhite
import com.zxx.linkgpt.ui.theme.MessageBlack
import com.zxx.linkgpt.ui.theme.MessageBlue
import com.zxx.linkgpt.ui.util.Avatar
import com.zxx.linkgpt.ui.util.ErrorType
import com.zxx.linkgpt.ui.util.MyErrorDialog
import com.zxx.linkgpt.ui.util.exceedLen
import com.zxx.linkgpt.viewmodel.LinkGPTViewModel
import com.zxx.linkgpt.viewmodel.util.ShowType
import java.io.FileNotFoundException

@Composable
fun Chat(
    vm: LinkGPTViewModel,
    onClickBack: () -> Unit,
    onClickConfig: () -> Unit,
) {
    val detail by vm.detail.collectAsStateWithLifecycle()
    val displayedHistory by vm.displayedHistory.collectAsStateWithLifecycle()
    val chattingWith by vm.chattingWith.collectAsStateWithLifecycle()
    var input by rememberSaveable { mutableStateOf("") }
    var errorType by rememberSaveable { mutableStateOf(ErrorType.NONE) }
    var inputError by rememberSaveable { mutableStateOf(false) }
    val listState = rememberLazyListState(0)
    val context = LocalContext.current
    var botBytes: ByteArray? = null
    var userBytes: ByteArray? = null
    try {
        botBytes = context.openFileInput(detail.name + ".png").readBytes()
    } catch (_: FileNotFoundException) {}
    try {
        userBytes = context.openFileInput("user.png").readBytes()
    } catch (_: FileNotFoundException) {}

    if (errorType == ErrorType.CHAT_TOO_LONG) {
        MyErrorDialog(
            detail = stringResource(id = R.string.chat_too_long),
            callback = { errorType = ErrorType.NONE }
        )
    }

    LaunchedEffect(displayedHistory) {
        listState.animateScrollToItem(displayedHistory.size)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                backgroundColor = colors.primaryVariant,
                contentColor = Color.White,
                navigationIcon = {
                    IconButton(
                        onClick = onClickBack,
                        content = {
                            Icon(
                                painter = painterResource(id = R.drawable.baseline_arrow_back_ios_24),
                                contentDescription = null,
                                tint = colors.secondary
                            )
                        }
                    )
                },
                actions = {
                    IconButton(
                        onClick = onClickConfig,
                        content = {
                            Icon(
                                painter = painterResource(id = R.drawable.baseline_menu_24),
                                contentDescription = null,
                                tint = colors.secondary
                            )
                        }
                    )
                },
                title = {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier.weight(1.0F),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Text(
                                text = detail.name,
                                style = typography.h5
                            )
                            Text(
                                text = if (chattingWith == detail.name) stringResource(id = R.string.replying)
                                       else String.format(stringResource(id = R.string.token_format), detail.lastUsage, detail.totalUsage),
                                style = typography.overline
                            )
                        }
                        Spacer(modifier = Modifier.width(20.dp))
                    }
                }
            )
        },
        bottomBar = {
            Surface(color = if (isSystemInDarkTheme()) Color.Black else BottomBarWhite) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 4.dp),
                    verticalAlignment = Alignment.Bottom
                ) {
                    // modify default outlinedTextField
                    val textStyle = typography.body1.copy(fontSize = 17.sp)
                    val outlinedTextFieldColors = TextFieldDefaults.outlinedTextFieldColors()
                    val interactionSource = remember { MutableInteractionSource() }
                    @OptIn(ExperimentalMaterialApi::class)
                    BasicTextField(
                        value = input,
                        modifier = Modifier
                            .weight(1.0F)
                            .padding(bottom = 4.dp)
                            .background(
                                outlinedTextFieldColors.backgroundColor(true).value,
                                shapes.large
                            )
                            .defaultMinSize(
                                minWidth = TextFieldDefaults.MinWidth,
                                minHeight = 24.dp
                            ),
                        onValueChange = {
                            input = it
                            inputError = false
                        },
                        textStyle = textStyle.merge(
                            TextStyle(
                                color = textStyle.color.takeOrElse {
                                    outlinedTextFieldColors.textColor(enabled = true).value
                                }
                            )
                        ),
                        cursorBrush = SolidColor(outlinedTextFieldColors.cursorColor(inputError).value),
                        maxLines = 8,
                        interactionSource = interactionSource,
                        decorationBox = {
                            TextFieldDefaults.OutlinedTextFieldDecorationBox(
                                value = input,
                                visualTransformation = VisualTransformation.None,
                                innerTextField = it,
                                singleLine = false,
                                enabled = true,
                                interactionSource = interactionSource,
                                colors = outlinedTextFieldColors,
                                isError = inputError,
                                border = {
                                    TextFieldDefaults.BorderBox(
                                        enabled = true,
                                        isError = inputError,
                                        interactionSource = interactionSource,
                                        colors = outlinedTextFieldColors,
                                        shape = shapes.large
                                    )
                                },
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                            )
                        }
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Button(
                        onClick = {
                            if (exceedLen(input, 0.25, 2.0, 2000)) {
                                errorType = ErrorType.CHAT_TOO_LONG
                                inputError = true
                            } else {
                                vm.chat(input)
                                input = ""
                            }
                        },
                        content = { Text(text = stringResource(id = R.string.send), style = typography.body1) },
                        shape = shapes.large,
                        colors = ButtonDefaults.buttonColors(
                            disabledBackgroundColor = colors.primary.copy(alpha = 0.5F),
                            disabledContentColor = colors.onPrimary
                        ),
                        enabled = "" != input && "" == chattingWith
                    )
                }
            }
        },
        content = {
            Surface(modifier = Modifier.padding(it).fillMaxSize()) {
                LazyColumn(state = listState) {
                    items(items = displayedHistory) { displayedHistoryData ->
                        when (displayedHistoryData.type) {
                            ShowType.TIME -> Text(
                                text = displayedHistoryData.str,
                                textAlign = TextAlign.Center,
                                style = typography.caption.copy(color = Color.Gray),
                                modifier = Modifier.fillMaxWidth().padding(8.dp)
                            )
                            ShowType.BOT -> SelectionContainer {
                                BotMessage(message = displayedHistoryData.str, bitmapBytes = botBytes)
                            }
                            else -> SelectionContainer {
                                UserMessage(
                                    message = displayedHistoryData.str,
                                    bitmapBytes = userBytes,
                                    showRetry = displayedHistoryData.type == ShowType.USER_ERR,
                                    retryCallback = { vm.chat(displayedHistoryData.str) }
                                )
                            }
                        }
                    }
                    item { Spacer(modifier = Modifier.height(0.dp)) }
                }
            }
        }
    )
}

@Composable
fun BotMessage(message: String, bitmapBytes: ByteArray?) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Avatar(
            bytes = bitmapBytes,
            defaultPainter = painterResource(id = R.drawable.default_bot),
            size = 40.dp
        )
        Spacer(modifier = Modifier.width(8.dp))
        Surface(
            color = colors.secondaryVariant,
            shape = shapes.medium,
            modifier = Modifier.padding(end = 48.dp)
        ) {
            Text(
                text = message,
                style = typography.body1.copy(color = colors.onSecondary, fontSize = 17.sp),
                modifier = Modifier.padding(vertical = 8.dp, horizontal = 12.dp)
            )
        }
    }
}

@Composable
fun UserMessage(message: String, bitmapBytes: ByteArray?, showRetry: Boolean, retryCallback: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Row(
            modifier = Modifier.weight(1.0F),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.Bottom
        ) {
            if (showRetry) {
                IconButton(
                    onClick = retryCallback,
                    content = {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_error_24),
                            contentDescription = null,
                            tint = colors.error
                        )
                    },
                    modifier = Modifier.size(width =  48.dp, height = 39.dp)
                )
            } else {
                Spacer(modifier = Modifier.width(48.dp))
            }
            Surface(
                color = if (isSystemInDarkTheme()) MessageBlack else MessageBlue,
                shape = shapes.medium,
            ) {
                Text(
                    text = message,
                    style = typography.body1.copy(color = Color.White, fontSize = 17.sp),
                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 12.dp)
                )
            }
        }
        Spacer(modifier = Modifier.width(8.dp))
        Avatar(
            bytes = bitmapBytes,
            defaultPainter = painterResource(id = R.drawable.default_user),
            size = 40.dp
        )
    }
}
