package com.zxx.linkgpt.ui

import android.net.Uri
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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

@Composable
fun UserConfig(
    vm: LinkGPTViewModel,
    onClickBack: () -> Unit
) {
    val context = LocalContext.current
    var name by rememberSaveable { mutableStateOf(vm.user.value) }
    var host by rememberSaveable { mutableStateOf(vm.host.value) }
    var port by rememberSaveable { mutableStateOf(vm.port.value.toString()) }
    val initUri = if ("user.png" in context.fileList()) Uri.fromFile(File(context.filesDir.absolutePath + "/user.png")) else Uri.EMPTY
    var uri by rememberSaveable { mutableStateOf(initUri) }
    var errorType by rememberSaveable { mutableStateOf(ErrorType.NONE) }
    var alertType by rememberSaveable { mutableStateOf(AlertType.NONE) }
    var nameError by rememberSaveable { mutableStateOf(false) }
    var portError by rememberSaveable { mutableStateOf(false) }
    var hostError by rememberSaveable { mutableStateOf(false) }
    val saveConfig: () -> Unit = {
        if (Uri.EMPTY.equals(uri)) {
            context.deleteFile("user.png")
        } else {
            saveBitmap(context, uri, "user.png")
        }
        vm.updateUserConfig(name, host, port.toInt())
        onClickBack()
    }

    when (errorType) {
        ErrorType.USER_NAME_EMPTY -> MyErrorDialog(
            detail = stringResource(id = R.string.user_name_empty),
            callback = { errorType = ErrorType.NONE }
        )
        ErrorType.USER_NAME_TOO_LONG -> MyErrorDialog(
            detail = stringResource(id = R.string.user_name_too_long),
            callback = { errorType = ErrorType.NONE }
        )
        ErrorType.HOST_EMPTY -> MyErrorDialog(
            detail = stringResource(id = R.string.host_empty),
            callback = { errorType = ErrorType.NONE }
        )
        ErrorType.PORT_INCORRECT -> MyErrorDialog(
            detail = stringResource(id = R.string.port_incorrect),
            callback = { errorType = ErrorType.NONE }
        )
        else -> {}
    }

    if (alertType == AlertType.CHANGE_USER_NAME) {
        MyAlertDialog(
            detail = stringResource(id = R.string.change_user_name_alert),
            cancelCallback = { alertType = AlertType.NONE },
            confirmCallback = saveConfig
        )
    }
    else if (alertType == AlertType.DISCARD) {
        MyAlertDialog(
            detail = stringResource(id = R.string.discard_change_alert),
            cancelCallback = { alertType = AlertType.NONE },
            confirmCallback = onClickBack
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                backgroundColor = colors.primaryVariant,
                contentColor = Color.White,
                title = { Text(text = stringResource(id = R.string.user_config)) },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            if (!initUri.equals(uri) || name != vm.user.value || host != vm.host.value || port != vm.port.value.toString()) {
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
                            val portInt = port.toIntOrNull()
                            if ("" == name) {
                                errorType = ErrorType.USER_NAME_EMPTY
                                nameError = true
                            } else if (exceedLen(name, 1.0, 2.0, 24)) {
                                errorType = ErrorType.USER_NAME_TOO_LONG
                                nameError = true
                            } else if ("" == host) {
                                errorType = ErrorType.HOST_EMPTY
                                hostError = true
                            } else if (portInt == null || portInt < 1 || portInt > 65535) {
                                errorType = ErrorType.PORT_INCORRECT
                                portError = true
                            } else if (name != vm.user.value && vm.user.value != "") {
                                alertType = AlertType.CHANGE_USER_NAME
                            } else {
                                saveConfig()
                            }
                        },
                        content = {
                            Icon(
                                painter = painterResource(R.drawable.baseline_check_24),
                                contentDescription = null,
                                tint = colors.secondary
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
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp)
            ) {
                item {
                    SingleLineInput(
                        title = stringResource(id = R.string.user_name),
                        value = name,
                        onValueChange = {
                            name = it
                            nameError = false
                        },
                        placeholder = stringResource(id = R.string.max_length),
                        isError = nameError
                    )
                }
                item {
                    AvatarChooser(
                        context = context,
                        uri = uri,
                        callback = { uri = it },
                        defaultPainter = painterResource(id = R.drawable.default_user)
                    )
                }
                item {
                    SingleLineInput(
                        title = stringResource(id = R.string.host),
                        value = host,
                        onValueChange = {
                            host = it
                            hostError = false
                        },
                        isError = hostError
                    )
                }
                item {
                    SingleLineInput(
                        title = stringResource(id = R.string.port),
                        value = port,
                        onValueChange = {
                            port = it
                            portError = false
                        },
                        isError = portError
                    )
                }
            }
        }
    )
}
