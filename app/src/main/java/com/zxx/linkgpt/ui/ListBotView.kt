package com.zxx.linkgpt.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material.MaterialTheme.typography
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zxx.linkgpt.R
import com.zxx.linkgpt.data.models.BotBriefData
import com.zxx.linkgpt.ui.theme.StatusGreen
import com.zxx.linkgpt.ui.theme.StatusRed
import com.zxx.linkgpt.ui.theme.StatusYellow
import com.zxx.linkgpt.ui.theme.Typography
import com.zxx.linkgpt.ui.util.Avatar
import com.zxx.linkgpt.ui.util.TimeDisplayUtil
import com.zxx.linkgpt.viewmodel.LinkGPTViewModel
import com.zxx.linkgpt.viewmodel.util.ServerFeedback
import java.io.FileNotFoundException

@Composable
fun ListBot(
    vm: LinkGPTViewModel,
    onClickAdd: () -> Unit,
    onClickConfig: () -> Unit,
    onClickChat: () -> Unit
) {
    val botList by vm.botList.collectAsStateWithLifecycle()
    val chatWith by vm.chattingWith.collectAsStateWithLifecycle()
    val name by vm.user.collectAsStateWithLifecycle()
    val serverFeedback by vm.serverFeedback.collectAsStateWithLifecycle()
    val todayUsage by vm.todayUsage.collectAsStateWithLifecycle()
    val maxUsage by vm.maxUsage.collectAsStateWithLifecycle()
    val context = LocalContext.current

    Scaffold (
        topBar = {
            TopAppBar(
                contentColor = Color.White,
                backgroundColor = colors.primaryVariant,
                navigationIcon = {
                    Spacer(modifier = Modifier.width(14.dp))
                    var bytes: ByteArray? = null
                    try {
                        bytes = context.openFileInput("user.png").readBytes()
                    } catch (_: FileNotFoundException) {}
                    Avatar(
                        bytes = bytes,
                        defaultPainter = painterResource(id = R.drawable.default_user),
                        size = 40.dp,
                        clickCallback = onClickConfig
                    )
                },
                actions = {
                    if (serverFeedback == ServerFeedback.REFRESHING) {
                        CircularProgressIndicator(
                            color = colors.secondary,
                            modifier = Modifier.padding(12.dp).size(24.dp)
                        )
                    } else {
                        IconButton(
                            onClick = { vm.refreshServerFeedback() },
                            content = {
                                Icon(
                                    painter = painterResource(id = R.drawable.baseline_refresh_24),
                                    contentDescription = null,
                                    tint = colors.secondary
                                )
                            }
                        )
                    }
                    IconButton(
                        onClick = onClickAdd,
                        content = {
                            Icon(
                                painter = painterResource(id = R.drawable.baseline_add_24),
                                contentDescription = null,
                                tint = colors.secondary
                            )
                        }
                    )
                },
                title = {
                    Column {
                        Text(
                            text = if ("" == name) stringResource(id = R.string.startup_tips) else name,
                            style = Typography.h5,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (serverFeedback != ServerFeedback.REFRESHING) {
                                Icon(
                                    painter = painterResource(id = R.drawable.baseline_circle_12),
                                    contentDescription = null,
                                    tint = when (serverFeedback) {
                                        ServerFeedback.OK -> StatusGreen
                                        ServerFeedback.REACH_LIMIT -> StatusYellow
                                        else -> StatusRed
                                    }
                                )
                                Spacer(modifier = Modifier.width(2.dp))
                            }
                            Text(
                                text = when (serverFeedback) {
                                    ServerFeedback.REFRESHING -> stringResource(id = R.string.connecting_server)
                                    ServerFeedback.FAILED -> stringResource(id = R.string.connection_failed)
                                    ServerFeedback.UNAUTHORIZED -> stringResource(id = R.string.unauthorized)
                                    else -> String.format(stringResource(id = R.string.usage_detail), todayUsage, maxUsage)
                                },
                                style = typography.overline
                            )
                        }
                    }
                }
            )
        },
        content = {
            LazyColumn(modifier = Modifier
                .fillMaxSize()
                .padding(it)) {
                items(items = botList) { briefData ->
                    BotCard(
                        briefData = briefData,
                        chatWith = chatWith,
                        callback = {
                            vm.refreshDetail(briefData.name)
                            vm.refreshDisplayedHistory(briefData.name)
                            onClickChat()
                        }
                    )
                }
            }
        }
    )
}

@Composable
fun BotCard(briefData: BotBriefData, chatWith: String?, callback: () -> Unit) {
    val context = LocalContext.current
    Row(modifier = Modifier
        .fillMaxWidth()
        .clickable { callback() }
        .padding(vertical = 8.dp, horizontal = 16.dp)
    ) {
        var bytes: ByteArray? = null
        try {
            bytes = context.openFileInput(briefData.name + ".png").readBytes()
        } catch (_: FileNotFoundException) {}
        Avatar(bytes = bytes, defaultPainter = painterResource(id = R.drawable.default_bot), size = 56.dp)
        Spacer(modifier = Modifier.width(12.dp))
        Column(verticalArrangement = Arrangement.Center, modifier = Modifier.height(56.dp)) {
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = briefData.name,
                    modifier = Modifier.weight(1.0F),
                    style = Typography.h5,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )
                Text(
                    text = TimeDisplayUtil.formatTime(briefData.time),
                    style = Typography.caption.copy(color = Color.Gray),
                    modifier = Modifier.padding(vertical = 2.dp)
                )
            }
            Text(
                text = if (briefData.output != null) briefData.output!!
                       else if (briefData.name == chatWith) stringResource(id = R.string.replying)
                       else stringResource(id = R.string.reply_error),
                style = Typography.body2.copy(color = Color.Gray),
                modifier = Modifier.padding(vertical = 2.dp),
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )
        }
    }
}
