package com.zxx.linkgpt.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.zxx.linkgpt.R
import com.zxx.linkgpt.ui.theme.RoundShapes
import com.zxx.linkgpt.ui.util.ShowAlertDialog
import com.zxx.linkgpt.ui.util.ShowErrorDialog
import com.zxx.linkgpt.ui.util.tryReadBitmap
import com.zxx.linkgpt.viewmodel.LinkGPTViewModel
import java.io.ByteArrayOutputStream
import java.io.File

@Composable
fun Config(navController: NavController, vm: LinkGPTViewModel) {
    val context = LocalContext.current
    var name by rememberSaveable { mutableStateOf(vm.user.value) }
    var host by rememberSaveable { mutableStateOf(vm.host.value) }
    var port by rememberSaveable { mutableStateOf(vm.port.value.toString()) }
    var uri by rememberSaveable {
        mutableStateOf(
            if ("user.png" in context.fileList()) Uri.fromFile(File(context.filesDir.absolutePath + "/user.png"))
            else Uri.EMPTY
        )
    }
    var showError by rememberSaveable { mutableStateOf(false) }
    var errorDetail by rememberSaveable { mutableStateOf("") }
    var showChangeAlert by rememberSaveable { mutableStateOf(false) }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { activityResult ->
        if (activityResult.resultCode == Activity.RESULT_OK) {
            uri = activityResult.data?.data as Uri
        }
    }
    val saveConfig: () -> Unit = {
        val bitmap = tryReadBitmap(context.contentResolver, uri)
        if (bitmap != null) {
            val byteArrayOutputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
            byteArrayOutputStream.close()
            context.openFileOutput("user.png", Context.MODE_PRIVATE).use {
                it.write(byteArrayOutputStream.toByteArray())
            }
        }
        vm.setUserConfig(name, host, port.toInt())
        vm.checkServer()
        navController.popBackStack()
    }

    if (showError) {
        ShowErrorDialog(detail = errorDetail, callback = { showError = false })
    } else if (showChangeAlert) {
        ShowAlertDialog(
            detail = "检测到您正在更改用户名，这可能导致先前对话中提及您用户名的聊天特化型机器人的新回复出现混乱。确定要更改吗？",
            cancelCallback = { showChangeAlert = false },
            confirmCallback = saveConfig
        )
    }

    Column {
        TopAppBar(
            title = { Text(text = "设置") },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        painter = painterResource(R.drawable.baseline_arrow_back_ios_24),
                        contentDescription = null
                    )
                }
            },
            actions = {
                IconButton(onClick = {
                    val portInt = port.toIntOrNull()
                    if ("" == name) {
                        errorDetail = "用户名不能为空！"
                        showError = true
                    } else if ("" == host) {
                        errorDetail = "主机不能为空！"
                        showError = true
                    } else if (portInt == null || portInt < 1 || portInt > 65535) {
                        errorDetail = "请输入正确的端口！"
                        showError = true
                    } else if (name != vm.user.value && vm.user.value != "") {
                        showChangeAlert = true
                    } else {
                        saveConfig()
                    }
                }) {
                    Icon(
                        painter = painterResource(R.drawable.baseline_check_24),
                        contentDescription = null
                    )
                }
            }
        )

        LazyColumn(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
            item {
                Column(modifier = Modifier.padding(vertical = 8.dp)) {
                    Text(text = "用户名")
                    TextField(
                        value = name,
                        onValueChange = { name = it },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            item {
                Column(modifier = Modifier.padding(vertical = 8.dp)) {
                    Text(text = "头像")
                    Row {
                        val imageModifier = Modifier.size(128.dp).clip(RoundShapes.small)
                        if (Uri.EMPTY.equals(uri)) {
                            Image(
                                painter = painterResource(id = R.drawable.default_user),
                                contentDescription = null,
                                modifier = imageModifier
                            )
                        } else {
                            tryReadBitmap(context.contentResolver, uri)?.asImageBitmap()?.let {
                                Image(
                                    bitmap = it,
                                    contentDescription = null,
                                    modifier = imageModifier,
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(64.dp))
                        Column(
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier.height(128.dp)
                        ) {
                            Button(onClick = {
                                val intent = Intent(Intent.ACTION_PICK)
                                intent.type = "image/*"
                                launcher.launch(intent)
                            }) {
                                Text(text = "选择头像")
                            }
                            Button(onClick = {
                                uri = Uri.EMPTY
                                context.deleteFile("user.png")
                            }) {
                                Text(text = "默认头像")
                            }
                        }
                    }
                }
            }

            item {
                Column(modifier = Modifier.padding(vertical = 8.dp)) {
                    Text(text = "主机")
                    TextField(
                        value = host,
                        onValueChange = { host = it },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            item {
                Column(modifier = Modifier.padding(vertical = 8.dp)) {
                    Text(text = "端口")
                    TextField(
                        value = port,
                        onValueChange = { port = it },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}