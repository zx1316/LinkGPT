package com.zxx.linkgpt

import android.app.Activity
import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@Composable
fun CreateBot(contentResolver: ContentResolver) {
    var name by rememberSaveable {
        mutableStateOf("")
    }

    var personality by rememberSaveable {
        mutableStateOf("")
    }

    var temperature by rememberSaveable {
        mutableStateOf(1.0F)
    }

    var topP by rememberSaveable {
        mutableStateOf(1.0F)
    }

    var presencePenalty by rememberSaveable {
        mutableStateOf(0.0F)
    }

    var frequencyPenalty by rememberSaveable {
        mutableStateOf(0.0F)
    }

    var expandAdvanced by rememberSaveable {
        mutableStateOf(false)
    }

    var uri by rememberSaveable {
        mutableStateOf(Uri.EMPTY)
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == Activity.RESULT_OK) {
            uri = it.data?.data as Uri
        }
    }
    Column {
        TopAppBar(
            title = { Text(text = "创建机器人") },
            navigationIcon = {
                IconButton(onClick = {}) {
                    Image(
                        painter = painterResource(R.drawable.baseline_arrow_back_ios_24),
                        contentDescription = null
                    )
                }
            },
            actions = {
                IconButton(onClick = {}) {
                    Image(
                        painter = painterResource(R.drawable.baseline_check_24),
                        contentDescription = null
                    )
                }
            })
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Column(modifier = Modifier.padding(vertical = 8.dp)) {
                    Text(text = "名称")
                    TextField(
                        value = name,
                        onValueChange = { name = it },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically, modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Column(modifier = Modifier.width(192.dp)) {
                        Text(text = "头像")
                        if (Uri.EMPTY.equals(uri)) {
                            Image(
                                painter = painterResource(id = R.drawable.default_bot),
                                contentDescription = null,
                                modifier = Modifier.size(128.dp)
                            )
                        } else {
                            tryReadBitmap(contentResolver, uri)?.asImageBitmap()?.let {
                                Image(
                                    bitmap = it,
                                    contentDescription = null,
                                    modifier = Modifier.size(128.dp),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                    }
                    Column {
                        Button(onClick = {
                            val intent = Intent(Intent.ACTION_PICK)
                            intent.type = "image/*"
                            launcher.launch(intent)
                        }) {
                            Text(text = "选择头像")
                        }
                        Button(onClick = { uri = Uri.EMPTY }) {
                            Text(text = "默认头像")
                        }
                    }
                }
            }

            item {
                Column(modifier = Modifier.padding(vertical = 8.dp)) {
                    Text(text = "设定")
                    TextField(
                        value = personality,
                        onValueChange = { personality = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(192.dp),
                        placeholder = {
                            Text(text = "如果想创建最多聊4096token的传统机器人，或者只是为了询问问题，请留空。如果想创建能无限对话的聊天特化型机器人，请填入用第三人称视角描述的设定。")
                        }
                    )
                }
            }

            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    Text(text = "高级设置")
                    IconButton(onClick = {
                        expandAdvanced = !expandAdvanced
                        if (!expandAdvanced) {
                            temperature = 1.0F
                            topP = 1.0F
                            presencePenalty = 0.0F
                            frequencyPenalty = 0.0F
                        }
                    }) {
                        Image(
                            painter = painterResource(id = if (expandAdvanced) R.drawable.baseline_expand_less_24 else R.drawable.baseline_expand_more_24),
                            contentDescription = null
                        )
                    }
                }
            }

            if (expandAdvanced) {
                item {
                    ParaAdjust(
                        paraName = "温度",
                        value = temperature,
                        range = 0.0F..2.0F,
                        callback = { temperature = it },
                        alert = { if (temperature > 1.5F) "可能会降低输出质量" else "" }
                    )
                }

                item {
                    ParaAdjust(
                        paraName = "顶端概率",
                        value = topP,
                        range = 0.0F..1.0F,
                        callback = { topP = it },
                        alert = { if (temperature != 1.0F && topP != 1.0F) "不建议同时调整温度和顶端概率" else "" }
                    )
                }

                item {
                    ParaAdjust(
                        paraName = "出现惩罚",
                        value = presencePenalty,
                        range = -2.0F..2.0F,
                        callback = { presencePenalty = it },
                        alert = { if (presencePenalty > 1.0F || presencePenalty < 0.0F) "可能会降低输出质量" else "" }
                    )
                }

                item {
                    ParaAdjust(
                        paraName = "频率惩罚",
                        value = frequencyPenalty,
                        range = -2.0F..2.0F,
                        callback = { frequencyPenalty = it },
                        alert = { if (frequencyPenalty > 1.0F || frequencyPenalty < 0.0F) "可能会降低输出质量" else "" }
                    )
                }
            }
        }
    }
}

@Composable
fun ParaAdjust(
    paraName: String,
    value: Float,
    range: ClosedFloatingPointRange<Float>,
    callback: (Float) -> Unit,
    alert: () -> String
) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = paraName, modifier = Modifier.width(116.dp))
            if ("" != alert()) {
                Image(
                    painter = painterResource(id = R.drawable.baseline_warning_20),
                    contentDescription = null
                )
                Text(
                    text = alert(),
                    style = MaterialTheme.typography.body2.copy(color = Color.Gray)
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
                text = String.format(if (value < 0) "%.2f" else " %.2f", value), modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .width(40.dp)
            )
        }
    }
}
