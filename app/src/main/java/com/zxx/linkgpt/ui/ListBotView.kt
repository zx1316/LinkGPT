package com.zxx.linkgpt.ui

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.zxx.linkgpt.R
import com.zxx.linkgpt.data.models.BotBriefData
import com.zxx.linkgpt.ui.navigation.RouteConfig
import com.zxx.linkgpt.ui.theme.LinkGPTTypography
import com.zxx.linkgpt.ui.theme.RoundShapes
import com.zxx.linkgpt.ui.util.TimeDisplayUtil
import com.zxx.linkgpt.viewmodel.LinkGPTViewModel
import com.zxx.linkgpt.viewmodel.ServerFeedback
import java.io.FileNotFoundException

@Composable
fun ListBot(navController: NavController, vm: LinkGPTViewModel) {
    val botList by vm.botList.collectAsState()
    val chatWith by vm.chattingWith.collectAsState()
    val name by vm.user.collectAsState()
    val serverFeedback by vm.serverFeedback.collectAsState()
    val todayUsage by vm.todayUsage.collectAsState()
    val maxUsage by vm.maxUsage.collectAsState()
    val context = LocalContext.current

    Column {
        TopAppBar(
            navigationIcon = {
                Spacer(modifier = Modifier.width(14.dp))
                val imageModifier = Modifier
                    .size(40.dp)
                    .clip(RoundShapes.small)
                    .clickable { navController.navigate(RouteConfig.ROUTE_USER_CONFIG) }
                var bytes = ByteArray(0)
                try {
                    bytes = context.openFileInput("user.png").readBytes()
                } catch (_: FileNotFoundException) {}
                if (bytes.isNotEmpty()) {
                    Image(
                        bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size).asImageBitmap(),
                        contentDescription = null,
                        modifier = imageModifier,
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.default_user),
                        contentDescription = null,
                        modifier = imageModifier,
                    )
                }
            },
            actions = {
                if (serverFeedback == ServerFeedback.REFRESHING) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.padding(12.dp).size(24.dp)
                    )
                } else {
                    IconButton(onClick = { vm.checkServer() }) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_refresh_24),
                            contentDescription = null
                        )
                    }
                }

                IconButton(onClick = { navController.navigate(RouteConfig.ROUTE_ADD) }) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_add_24),
                        contentDescription = null,
                    )
                }
            },
            title = {
                Column {
                    Text(
                        text = if ("" == name) "请点击头像以设置基本信息" else name,
                        style = LinkGPTTypography.h5.copy(color = Color.White)
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (serverFeedback != ServerFeedback.REFRESHING) {
                            Icon(
                                painter = painterResource(id = R.drawable.baseline_circle_12),
                                contentDescription = null,
                                tint = when (serverFeedback) {
                                    ServerFeedback.OK -> Color(0, 255, 128)
                                    ServerFeedback.REACH_LIMIT -> Color(255, 192, 0)
                                    else -> Color(255, 64, 96)
                                }
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                        }
                        Text(
                            text = when (serverFeedback) {
                                ServerFeedback.REFRESHING -> "正在从服务器拉取状态..."
                                ServerFeedback.FAILED -> "无法连接至服务器"
                                ServerFeedback.UNAUTHORIZED -> "未授权的用户"
                                else -> String.format("今日使用情况：%d/%d", todayUsage, maxUsage)
                            },
                            style = LinkGPTTypography.body2.copy(
                                color = Color.White,
                                fontSize = 10.sp
                            )
                        )
                    }
                }
            }
        )

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(items = botList) { briefData ->
                BotCard(briefData = briefData, chatWith = chatWith)
            }
        }
    }
}

@Composable
fun BotCard(briefData: BotBriefData, chatWith: String?) {
    val context = LocalContext.current
    Row(modifier = Modifier
        .fillMaxWidth()
        .clickable {

        }
        .padding(vertical = 8.dp, horizontal = 16.dp)
    ) {
        val imageModifier = Modifier.size(56.dp).clip(RoundShapes.small)
        var bytes = ByteArray(0)
        try {
            bytes = context.openFileInput(briefData.name + ".png").readBytes()
        } catch (_: FileNotFoundException) {}
        if (bytes.isNotEmpty()) {
            Image(
                bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size).asImageBitmap(),
                contentDescription = null,
                modifier = imageModifier,
                contentScale = ContentScale.Crop
            )
        } else {
            Image(
                painter = painterResource(id = R.drawable.default_bot),
                contentDescription = null,
                modifier = imageModifier
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(verticalArrangement = Arrangement.Center, modifier = Modifier.height(56.dp)) {
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = briefData.name,
                    modifier = Modifier.weight(1.0F),
                    style = LinkGPTTypography.h5,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )
                Text(
                    text = TimeDisplayUtil.formatTime(briefData.time),
                    style = LinkGPTTypography.body2,
                    modifier = Modifier.padding(vertical = 2.dp)
                )
            }
            Text(
                text = if (briefData.output != null) briefData.output!! else if (briefData.name == chatWith) "回复中..." else "发生了错误，请重试。",
                style = LinkGPTTypography.body1,
                modifier = Modifier.padding(vertical = 2.dp)
            )
        }
    }
}
