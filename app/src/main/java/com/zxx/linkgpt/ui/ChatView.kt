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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material.MaterialTheme.shapes
import androidx.compose.material.MaterialTheme.typography
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextFieldColors
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zxx.linkgpt.R
import com.zxx.linkgpt.ui.theme.BottomBarWhite
import com.zxx.linkgpt.ui.theme.ErrorRed
import com.zxx.linkgpt.ui.theme.MessageBlack
import com.zxx.linkgpt.ui.theme.MessageBlue
import com.zxx.linkgpt.ui.util.Avatar
import com.zxx.linkgpt.viewmodel.LinkGPTViewModel
import com.zxx.linkgpt.viewmodel.util.ServerFeedback
import com.zxx.linkgpt.viewmodel.util.ShowType
import java.io.FileNotFoundException

@Composable
fun Chat(
    vm: LinkGPTViewModel,
    onClickBack: () -> Unit,
    onClickConfig: () -> Unit,
) {
    val detail by vm.detail.collectAsState()
    val displayedHistory by vm.displayedHistory.collectAsState()
    val chattingWith by vm.chattingWith.collectAsState()
    val serverFeedback by vm.serverFeedback.collectAsState()
    var input by rememberSaveable { mutableStateOf("") }
    val context = LocalContext.current
    val listState by vm.listState.collectAsState()
    var botBytes: ByteArray? = null
    var userBytes: ByteArray? = null
    try {
        botBytes = context.openFileInput(detail.name + ".png").readBytes()
    } catch (_: FileNotFoundException) {}
    try {
        userBytes = context.openFileInput("user.png").readBytes()
    } catch (_: FileNotFoundException) {}

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
                                style = typography.body2.copy(fontSize = 10.sp)
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
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.Bottom
                ) {
                    MyOutlinedTextField(
                        value = input,
                        onValueChange = { input = it },
                        maxLines = 8,
                        modifier = Modifier
                            .weight(1.0F)
                            .padding(bottom = 4.dp),
                        shape = RoundedCornerShape(24.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Button(
                        onClick = {
                            vm.chat(input)
                            input = ""
                        },
                        content = { Text(text = stringResource(id = R.string.send)) },
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.buttonColors(
                            disabledBackgroundColor = colors.primary.copy(alpha = 0.5F),
                            disabledContentColor = colors.onPrimary
                        ),
                        enabled = "" != input && "" == chattingWith && serverFeedback == ServerFeedback.OK
                    )
                }
            }
        },
        content = {
            Surface(modifier = Modifier
                .padding(it)
                .fillMaxSize()) {
                LazyColumn(state = listState) {
                    items(items = displayedHistory) { displayedHistoryData ->
                        when (displayedHistoryData.type) {
                            ShowType.TIME -> Text(
                                text = displayedHistoryData.str,
                                textAlign = TextAlign.Center,
                                style = typography.body2.copy(color = Color.Gray, fontSize = 12.sp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp)
                            )
                            ShowType.BOT -> SelectionContainer {
                                BotMessage(message = displayedHistoryData.str, bitmapBytes = botBytes)
                            }
                            else -> SelectionContainer {
                                UserMessage(
                                    message = displayedHistoryData.str,
                                    bitmapBytes = userBytes,
                                    showRetry = displayedHistoryData.type == ShowType.USER_ERR,
                                    retryCallback = {
                                        if (serverFeedback == ServerFeedback.OK) {
                                            vm.chat(displayedHistoryData.str)
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    )
}

@Composable
fun BotMessage(message: String, bitmapBytes: ByteArray?) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
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
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.padding(end = 48.dp)
        ) {
            Text(
                text = message,
                style = typography.body1.copy(fontSize = 17.sp, color = colors.onSecondary),
                modifier = Modifier.padding(vertical = 8.dp, horizontal = 12.dp)
            )
        }
    }
}

@Composable
fun UserMessage(message: String, bitmapBytes: ByteArray?, showRetry: Boolean, retryCallback: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Row(
            modifier = Modifier.weight(1.0F),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.Bottom
        ) {
            if (showRetry) {
                Spacer(modifier = Modifier.width(16.dp))
                IconButton(
                    onClick = retryCallback,
                    content = {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_error_24),
                            contentDescription = null,
                            tint = ErrorRed
                        )
                    },
                    modifier = Modifier.size(32.dp)
                )
            } else {
                Spacer(modifier = Modifier.width(48.dp))
            }
            Surface(
                color = if (isSystemInDarkTheme()) MessageBlack else MessageBlue,
                shape = RoundedCornerShape(8.dp),
            ) {
                Text(
                    text = message,
                    style = typography.body1.copy(fontSize = 17.sp, color = Color.White),
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

@Composable
fun MyOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = LocalTextStyle.current,
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = false,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    minLines: Int = 1,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    shape: Shape = shapes.small,
    colors: TextFieldColors = TextFieldDefaults.outlinedTextFieldColors()
) {
    val textColor = textStyle.color.takeOrElse {
        colors.textColor(enabled).value
    }
    val mergedTextStyle = textStyle.merge(TextStyle(color = textColor))

    @OptIn(ExperimentalMaterialApi::class)
    BasicTextField(
        value = value,
        modifier = if (label != null)
            modifier
                .semantics(mergeDescendants = true) {}
                .padding(top = 8.dp)
         else modifier
            .background(colors.backgroundColor(enabled).value, shape)
            .defaultMinSize(
                minWidth = TextFieldDefaults.MinWidth,
                minHeight = 24.dp
            ),
        onValueChange = onValueChange,
        enabled = enabled,
        readOnly = readOnly,
        textStyle = mergedTextStyle,
        cursorBrush = SolidColor(colors.cursorColor(isError).value),
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        interactionSource = interactionSource,
        singleLine = singleLine,
        maxLines = maxLines,
        minLines = minLines,
        decorationBox = @Composable { innerTextField ->
            TextFieldDefaults.OutlinedTextFieldDecorationBox(
                value = value,
                visualTransformation = visualTransformation,
                innerTextField = innerTextField,
                placeholder = placeholder,
                label = label,
                leadingIcon = leadingIcon,
                trailingIcon = trailingIcon,
                singleLine = singleLine,
                enabled = enabled,
                isError = isError,
                interactionSource = interactionSource,
                colors = colors,
                border = {
                    TextFieldDefaults.BorderBox(
                        enabled,
                        isError,
                        interactionSource,
                        colors,
                        shape
                    )
                },
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
            )
        }
    )
}
