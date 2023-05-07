package com.zxx.linkgpt.ui

import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material.MaterialTheme.typography
import androidx.compose.material.Scaffold
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.zxx.linkgpt.R
import com.zxx.linkgpt.ui.util.AlertType
import com.zxx.linkgpt.ui.util.AvatarChooser
import com.zxx.linkgpt.ui.util.ErrorType
import com.zxx.linkgpt.ui.util.MyAlertDialog
import com.zxx.linkgpt.ui.util.MyErrorDialog
import com.zxx.linkgpt.ui.util.SingleLineInput
import com.zxx.linkgpt.ui.util.exceedLen
import com.zxx.linkgpt.ui.util.saveBitmap
import com.zxx.linkgpt.viewmodel.LinkGPTViewModel
import java.io.File
import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.sign

@Composable
fun AddOrConfigBot(
    vm: LinkGPTViewModel,
    onClickBack: () -> Unit,
    onClickDelete: () -> Unit = {},
    isConfig: Boolean
) {
    val context = LocalContext.current
    var name by rememberSaveable { mutableStateOf(if (isConfig) vm.detail.value.name else "") }
    var settings by rememberSaveable { mutableStateOf(if (isConfig) vm.detail.value.settings else "" ) }
    var temperature by rememberSaveable { mutableStateOf(if (isConfig) vm.detail.value.temperature else 1.0F) }
    var topP by rememberSaveable { mutableStateOf(if (isConfig) vm.detail.value.topP else 1.0F) }
    var presencePenalty by rememberSaveable { mutableStateOf(if (isConfig) vm.detail.value.presencePenalty else 0.0F) }
    var frequencyPenalty by rememberSaveable { mutableStateOf(if (isConfig) vm.detail.value.frequencyPenalty else 0.0F) }
    var expandAdvanced by rememberSaveable { mutableStateOf(false) }
    var alertType by rememberSaveable { mutableStateOf(AlertType.NONE) }
    var errorType by rememberSaveable { mutableStateOf(ErrorType.NONE) }
    val initUri = if ("$name.png" in context.fileList()) Uri.fromFile(File(context.filesDir.absolutePath + "/$name.png")) else Uri.EMPTY
    var uri by rememberSaveable { mutableStateOf(initUri) }
    var nameError by rememberSaveable{ mutableStateOf(false) }
    var settingsError by rememberSaveable { mutableStateOf(false) }
    val example1 = stringResource(id =  R.string.settings_example1)
    val example2 = stringResource(id =  R.string.settings_example2)
    val mayDecrease = stringResource(id = R.string.may_decrease_quality)
    val notRecommend = stringResource(id = R.string.change_both_not_recommended)

    when (errorType) {
        ErrorType.BOT_NAME_DUPLICATE -> MyErrorDialog(
            detail = stringResource(id = R.string.bot_name_already),
            callback = { errorType = ErrorType.NONE }
        )
        ErrorType.BOT_NAME_USER -> MyErrorDialog(
            detail = stringResource(id = R.string.bot_name_user),
            callback = { errorType = ErrorType.NONE }
        )
        ErrorType.BOT_NAME_EMPTY -> MyErrorDialog(
            detail = stringResource(id = R.string.bot_name_empty),
            callback = { errorType = ErrorType.NONE }
        )
        ErrorType.BOT_NAME_TOO_LONG -> MyErrorDialog(
            detail = stringResource(id = R.string.bot_name_too_long),
            callback = { errorType = ErrorType.NONE }
        )
        ErrorType.SETTINGS_TOO_LONG -> MyErrorDialog(
            detail = stringResource(id = R.string.settings_too_long),
            callback = { errorType = ErrorType.NONE }
        )
        else -> {}
    }

    when (alertType) {
        AlertType.DISCARD -> MyAlertDialog(
            detail = stringResource(id = R.string.discard_change_alert),
            cancelCallback = { alertType = AlertType.NONE },
            confirmCallback = onClickBack
        )
        AlertType.RESET -> MyAlertDialog(
            detail = stringResource(id = R.string.reset_alert),
            cancelCallback = { alertType = AlertType.NONE },
            confirmCallback = {
                vm.clearMemory()
                alertType = AlertType.NONE
            }
        )
        AlertType.DELETE -> MyAlertDialog(
            detail = stringResource(id = R.string.delete_alert),
            cancelCallback = { alertType = AlertType.NONE },
            confirmCallback = {
                vm.deleteBot()
                onClickDelete()
            },
            confirmColor = colors.error
        )
        else -> {}
    }

    Scaffold(
        topBar = {
            TopAppBar(
                backgroundColor = colors.primaryVariant,
                contentColor = Color.White,
                title = { Text(text = stringResource(id = if (isConfig) R.string.bot_config else R.string.add_bot)) },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            if (isConfig && (!initUri.equals(uri) || temperature != vm.detail.value.temperature || topP != vm.detail.value.topP || presencePenalty != vm.detail.value.presencePenalty || frequencyPenalty != vm.detail.value.frequencyPenalty)) {
                                alertType = AlertType.DISCARD
                            } else {
                                onClickBack()
                            }
                        },
                        content = {
                            Icon(
                                painter = painterResource(R.drawable.baseline_arrow_back_ios_24),
                                contentDescription = null,
                                tint = colors.secondary
                            )
                        }
                    )
                },
                actions = {
                    IconButton(
                        onClick = {
                            if (isConfig) {
                                vm.adjustBot(temperature, topP, presencePenalty, frequencyPenalty)
                            } else {
                                if ("" == name) {
                                    errorType = ErrorType.BOT_NAME_EMPTY
                                    nameError = true
                                } else if ("user" == name) {
                                    errorType = ErrorType.BOT_NAME_USER
                                    nameError = true
                                } else if (exceedLen(name, 1.0, 2.0, 24)) {
                                    errorType = ErrorType.BOT_NAME_TOO_LONG
                                    nameError = true
                                } else if (exceedLen(settings, 0.25, 2.0, 500)) {
                                    errorType = ErrorType.SETTINGS_TOO_LONG
                                    settingsError = true
                                } else {
                                    for (bot in vm.botList.value) {
                                        if (bot.name == name) {
                                            errorType = ErrorType.BOT_NAME_DUPLICATE
                                            nameError = true
                                            break
                                        }
                                    }
                                    if (errorType == ErrorType.NONE) {
                                        vm.addBot(
                                            name = name,
                                            settings = settings,
                                            temperature = rounding(temperature),
                                            topP = rounding(topP),
                                            presencePenalty = rounding(presencePenalty),
                                            frequencyPenalty = rounding(frequencyPenalty)
                                        )
                                    }
                                }
                            }
                            if (errorType == ErrorType.NONE) {
                                if (Uri.EMPTY.equals(uri)) {
                                    context.deleteFile("$name.png")
                                } else {
                                    saveBitmap(context, uri, "$name.png")
                                }
                                onClickBack()
                            }
                        },
                        content = {
                            Icon(
                                painter = painterResource(R.drawable.baseline_check_24),
                                tint = colors.secondary,
                                contentDescription = null
                            )
                        }
                    )
                }
            )
        },
        content = { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(paddingValues = paddingValues)
                    .padding(horizontal = 16.dp)
            ) {
                item {
                    SingleLineInput(
                        title = stringResource(id = R.string.bot_name),
                        value = name,
                        onValueChange = {
                            name = it
                            nameError = false
                        },
                        isError = nameError,
                        placeholder = stringResource(id = R.string.max_length)
                    )
                }
                item {
                    AvatarChooser(
                        context = context,
                        uri = uri,
                        callback = { uri = it },
                        defaultPainter = painterResource(id = R.drawable.default_bot)
                    )
                }
                item {
                    Column(modifier = Modifier.padding(vertical = 8.dp)) {
                        Text(text = stringResource(id = R.string.settings))
                        TextField(
                            value = settings,
                            onValueChange = {
                                settings = it
                                settingsError = false
                            },
                            isError = settingsError,
                            modifier = Modifier.fillMaxWidth().height(192.dp),
                            placeholder = {
                                if (!isConfig) {
                                    Text(text = stringResource(id = R.string.settings_placeholder))
                                }
                            },
                            readOnly = isConfig
                        )
                        if (!isConfig) {
                            Row(modifier = Modifier.fillMaxWidth()) {
                                Button(
                                    onClick = { settings = String.format(example1, name, vm.user.value, vm.user.value) },
                                    content = { Text(text = stringResource(id = R.string.settings_example_btn1)) }
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Button(
                                    onClick = { settings = String.format(example2, name, vm.user.value, vm.user.value, vm.user.value) },
                                    content = { Text(text = stringResource(id = R.string.settings_example_btn2)) }
                                )
                            }
                        }
                    }
                }
                item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = stringResource(id = R.string.advanced_settings))
                        IconButton(
                            onClick = { expandAdvanced = !expandAdvanced },
                            content = {
                                Icon(
                                    painter = painterResource(id = if (expandAdvanced) R.drawable.baseline_expand_less_24 else R.drawable.baseline_expand_more_24),
                                    contentDescription = null
                                )
                            }
                        )
                    }
                }
                item {
                    AnimatedVisibility(
                        visible = expandAdvanced,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        ParaAdjust(
                            paraName = stringResource(id = R.string.temperature),
                            value = temperature,
                            range = 0.0F..2.0F,
                            callback = { temperature = it },
                            alert = { if (temperature >= 1.505F) mayDecrease else null }
                        )
                    }
                }
                item {
                    AnimatedVisibility(
                        visible = expandAdvanced,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        ParaAdjust(
                            paraName = stringResource(id = R.string.top_p),
                            value = topP,
                            range = 0.0F..1.0F,
                            callback = { topP = it },
                            alert = { if (abs(temperature - 1.0F) >= 0.005F && 1.0F - topP >= 0.005F) notRecommend else null }
                        )
                    }
                }
                item {
                    AnimatedVisibility(
                        visible = expandAdvanced,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        ParaAdjust(
                            paraName = stringResource(id = R.string.presence_penalty),
                            value = presencePenalty,
                            range = -2.0F..2.0F,
                            callback = { presencePenalty = it },
                            alert = { if (presencePenalty >= 1.005F || presencePenalty <= -0.005F) mayDecrease else null }
                        )
                    }
                }
                item {
                    AnimatedVisibility(
                        visible = expandAdvanced,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        ParaAdjust(
                            paraName = stringResource(id = R.string.frequency_penalty),
                            value = frequencyPenalty,
                            range = -2.0F..2.0F,
                            callback = { frequencyPenalty = it },
                            alert = { if (frequencyPenalty >= 1.005F || frequencyPenalty <= -0.005F) mayDecrease else null }
                        )
                    }
                }
                item {
                    AnimatedVisibility(
                        visible = expandAdvanced,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        Row(modifier = Modifier.padding(vertical = 4.dp)) {
                            Button(
                                onClick = {
                                    temperature = 1.0F
                                    topP = 1.0F
                                    presencePenalty = 0.0F
                                    frequencyPenalty = 0.0F
                                },
                                content = { Text(text = stringResource(id = R.string.reset_para)) }
                            )
                            if (isConfig) {
                                Spacer(modifier = Modifier.width(8.dp))
                                Button(
                                    onClick = { alertType = AlertType.RESET },
                                    content = { Text(text = stringResource(id = R.string.reset)) }
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Button(
                                    colors = ButtonDefaults.buttonColors(backgroundColor = colors.error),
                                    onClick = { alertType = AlertType.DELETE },
                                    content = { Text(text = stringResource(id = R.string.delete)) }
                                )
                            }
                        }
                    }
                }
                item {
                    AnimatedVisibility(
                        visible = expandAdvanced,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        Text(
                            modifier = Modifier.padding(vertical = 8.dp),
                            text = stringResource(id = R.string.para_info),
                            style = typography.body2.copy(color = Color.Gray),
                        )
                    }
                }
            }
        }
    )
}

@Composable
fun ParaAdjust(
    paraName: String,
    value: Float,
    range: ClosedFloatingPointRange<Float>,
    callback: (Float) -> Unit,
    alert: () -> String?
) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = paraName, modifier = Modifier.width(120.dp))
            alert()?.let {
                Image(
                    painter = painterResource(id = R.drawable.baseline_warning_20),
                    contentDescription = null
                )
                Text(
                    text = it,
                    style = typography.body2.copy(color = Color.Gray)
                )
            }
        }
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Slider(
                value = value,
                onValueChange = callback,
                valueRange = range,
                modifier = Modifier.weight(1.0F)
            )
            Text(
                text = String.format(if (value < 0) "%.2f" else " %.2f", value),
                modifier = Modifier.padding(start = 8.dp).width(42.dp)
            )
        }
    }
}

fun rounding(x: Float): Float {
    return sign(x) * floor(abs(x) * 100F + 0.5F) * 0.01F
}
