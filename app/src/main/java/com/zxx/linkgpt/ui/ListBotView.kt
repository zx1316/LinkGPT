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
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.zxx.linkgpt.R
import com.zxx.linkgpt.data.models.BotBriefData
import com.zxx.linkgpt.ui.navigation.RouteConfig
import com.zxx.linkgpt.ui.theme.LinkGPTTypography
import com.zxx.linkgpt.ui.theme.RoundShapes
import com.zxx.linkgpt.viewmodel.LinkGPTViewModel

@Composable
fun ListBot(navController: NavController, vm: LinkGPTViewModel) {
    val botList by vm.botList.collectAsState()
    val chatWith by vm.chatWith.observeAsState()

    Column {
        TopAppBar(
            title = {},
            navigationIcon = {
                IconButton(onClick = {

                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_settings_24),
                        contentDescription = null
                    )
                }},
            actions = {
                IconButton(onClick = {
                    navController.navigate(RouteConfig.ROUTE_ADD)
                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_add_24),
                        contentDescription = null,
                    )
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
        if (briefData.useDefaultImage) {
            Image(
                painter = painterResource(id = R.drawable.default_bot),
                contentDescription = null,
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundShapes.small)
            )
        } else {
            val bytes = context.openFileInput(briefData.name + ".png").readBytes()
            Image(
                bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size).asImageBitmap(),
                contentDescription = null,
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundShapes.small),
                contentScale = ContentScale.Crop
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
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
                    text = timeFormatter(briefData.time),
                    style = LinkGPTTypography.body2,
                    modifier = Modifier.padding(vertical = 2.dp)
                )
            }
            Text(
                text = if (briefData.output != null) briefData.output!! else if (briefData.name == chatWith) "回复中..." else "发生了错误，请重试。",
                style = LinkGPTTypography.body1,
            )
        }
    }
}
