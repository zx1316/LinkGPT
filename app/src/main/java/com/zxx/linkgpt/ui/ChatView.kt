package com.zxx.linkgpt.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.zxx.linkgpt.viewmodel.LinkGPTViewModel

// complete this composable function.
// bot详细信息在detail里，类型为botDetailData，历史记录在history里，类型为ArrayList<BotHistoryData>。
// 要在TopAppBar显示bot的名称、lastUsage和totalUsage，左边显示返回的IconButton，右边显示跳转到bot配置界面的IconButton，跳转后可以进行参数调节、清空记忆和删除操作。
// 返回按钮直接调用onClickBack，跳转按钮直接调用onClickConfig。
// 使用LazyColumn显示聊天记录。效果可以先参照ChatGPT的，但是要显示时间，TimeDisplayUtil中有时间处理函数，如果连续几条记录处理后的时间字符串相同，只显示第一条的时间。
// 使用BottomAppBar显示发送栏，左边文本框右边按钮，按钮用文字的还是图标的看情况，怎么好看怎么来。文本框有个属性可以设置最大行数，可以做到qq的效果。
// 如果chattingWith != "" || serverFeedback != ServerFeedback.OK || "" == 文本框消息，发送按钮要禁用。
// 最后要用一个Column把TopAppBar、LazyColumn和BottomAppBar装起来。
// 当最新的历史记录记录的output是null时，如果chattingWith == detail.name，说明正在回复；如果chattingWith == ""，说明回复出现了错误。
// 要显示正在回复提示和出错提示，如果出错还要在最新的记录旁显示重新发送按钮。出错状态下可以强行发送新消息，覆盖出错记录，具体后台逻辑我会想办法搞。
// 调用vm.chat(输入, failedFlag)与bot开始新一轮对话，无论上次是否出错。
// 按重新发送按钮其实是调用vm.chat(出错的输入, failedFlag(实际上是true))
// 可能用到的矢量图标放res/drawable里了。有返回、发送、菜单等，可以点开自己看，也可以“右键drawable->New->Vector Asset”添加矢量图。
// 代码怎么写可以参考我写的那几个UI文件，那几个组件的用法都能找到，然后一些代码（如显示头像的代码）可以直接照搬过来233333。
@Composable
fun Chat(
    vm: LinkGPTViewModel,
    onClickBack: () -> Unit,
    onClickConfig: () -> Unit,
) {
    val detail by vm.detail.collectAsState()
    val history by vm.history.collectAsState()
    val chattingWith by vm.chattingWith.collectAsState()
    val serverFeedback by vm.serverFeedback.collectAsState()
    // failedFlag should be updated by something when history or chattingWith changes, but I'm not sure.
    var failedFlag = history.isNotEmpty() && history[history.size - 1].output == null && "" == chattingWith
    // Test UI
    Column {
        Text(text = detail.name)
        if (history.isNotEmpty()) {
            history[0].output?.let { Text(text = it) }
        }
    }
    // Your UI
}