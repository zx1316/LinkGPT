package com.zxx.linkgpt.ui

import android.content.ContentResolver
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.zxx.linkgpt.R
import com.zxx.linkgpt.data.models.BotBriefData
import com.zxx.linkgpt.ui.navigation.RouteConfig
import com.zxx.linkgpt.viewmodel.TestViewModel

@Composable
fun ListBot(contentResolver: ContentResolver, navController: NavController) {
    val vm: TestViewModel = viewModel()
    val botList by vm.botList.collectAsState()

    Column {
        TopAppBar(
            title = {},
            navigationIcon = {},
            actions = {
                IconButton(onClick = {
                    navController.navigate(RouteConfig.ROUTE_ADD)
                }) {
                    Image(
                        painter = painterResource(id = R.drawable.baseline_add_24),
                        contentDescription = null,
                    )
                }
            }
        )
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(items = botList) { briefData ->
                BotCard(contentResolver = contentResolver, briefData = briefData)
            }
        }
    }
}

@Composable
fun BotCard(contentResolver: ContentResolver, briefData: BotBriefData) {
    Text(text = briefData.name, modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 16.dp))
}
