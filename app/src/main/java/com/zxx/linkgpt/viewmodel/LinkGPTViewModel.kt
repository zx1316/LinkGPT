package com.zxx.linkgpt.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.zxx.linkgpt.R
import com.zxx.linkgpt.data.LinkGPTDatabase
import com.zxx.linkgpt.data.models.BotBriefData
import com.zxx.linkgpt.data.models.BotDetailData
import com.zxx.linkgpt.data.models.BotHistoryData
import com.zxx.linkgpt.data.repository.LinkGPTRepository
import com.zxx.linkgpt.network.NetworkHandler
import com.zxx.linkgpt.ui.util.TimeDisplayUtil
import com.zxx.linkgpt.ui.util.showToast
import com.zxx.linkgpt.viewmodel.util.DisplayedHistoryData
import com.zxx.linkgpt.viewmodel.util.ServerFeedback
import com.zxx.linkgpt.viewmodel.util.ShowType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar

class LinkGPTViewModel(application: Application) : AndroidViewModel(application) {
    @SuppressLint("StaticFieldLeak")
    private val context = application.applicationContext
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("com.zxx.linkgpt.PREFERENCE_FILE_KEY", Context.MODE_PRIVATE)
    private val linkGPTDao = LinkGPTDatabase.getDatabase(application).linkGPTDao()
    private val repository: LinkGPTRepository = LinkGPTRepository(linkGPTDao)
    private val networkHandler = NetworkHandler()

    private val _botList = MutableStateFlow(ArrayList<BotBriefData>())
    private val _user = MutableStateFlow("")
    private val _host = MutableStateFlow("")
    private val _port = MutableStateFlow(80)
    private val _todayUsage = MutableStateFlow(0)
    private val _maxUsage = MutableStateFlow(0)
    private val _serverFeedback = MutableStateFlow(ServerFeedback.FAILED)
    private val _chattingWith = MutableStateFlow("")
    private val _detail = MutableStateFlow(BotDetailData())
    private val _displayedHistory = MutableStateFlow(ArrayList<DisplayedHistoryData>())

    val botList = _botList.asStateFlow()
    val user = _user.asStateFlow()
    val host = _host.asStateFlow()
    val port = _port.asStateFlow()
    val todayUsage = _todayUsage.asStateFlow()
    val maxUsage = _maxUsage.asStateFlow()
    val serverFeedback = _serverFeedback.asStateFlow()
    val chattingWith = _chattingWith.asStateFlow()
    val detail = _detail.asStateFlow()
    val displayedHistory = _displayedHistory.asStateFlow()

    fun addBot(
        name: String,
        setting: String,
        temperature: Float,
        topP: Float,
        presencePenalty: Float,
        frequencyPenalty: Float,
        useTemplate: Boolean
    ) {
        viewModelScope.launch {
            repository.newBot(
                BotDetailData(
                    name = name,
                    setting = setting,
                    temperature = temperature,
                    topP = topP,
                    presencePenalty = presencePenalty,
                    frequencyPenalty = frequencyPenalty,
                    useTemplate = useTemplate
                )
            )
            repository.insertHistory(
                BotHistoryData(
                    name = name,
                    output = context.getString(R.string.create_message)
                )
            )
            refreshBotList()
        }
    }

    fun updateUserConfig(name: String, host: String, port: Int) {
        _user.value = name
        _host.value = host
        _port.value = port
        refreshServerFeedback()
        sharedPreferences.edit()
            .putString("name", name)
            .putString("host", host)
            .putInt("port", port)
            .apply()
    }

    fun refreshServerFeedback() {
        viewModelScope.launch {
            _serverFeedback.value = ServerFeedback.REFRESHING
            val userDetail = networkHandler.checkUser(_user.value, _host.value, _port.value)
            if (userDetail == null) {
                showToast(context.getString(R.string.toast_reply_error) + networkHandler.getReason(), context)
                _serverFeedback.value = ServerFeedback.FAILED
            } else if (userDetail.authorized) {
                if (userDetail.todayUsage > userDetail.maxUsage) {
                    _serverFeedback.value = ServerFeedback.REACH_LIMIT
                } else {
                    _serverFeedback.value = ServerFeedback.OK
                }
                _todayUsage.value = userDetail.todayUsage
                _maxUsage.value = userDetail.maxUsage
            } else {
                _serverFeedback.value = ServerFeedback.UNAUTHORIZED
            }
        }
    }

    fun chat(input: String) {
        viewModelScope.launch {
            // avoid the change of _detail. Shallow copy is ok.
            val failedLastTime = _displayedHistory.value[_displayedHistory.value.size - 1].type != ShowType.BOT && "" == _chattingWith.value
            val detailCopy = _detail.value.copy()
            _chattingWith.value = detailCopy.name
            val calendar = Calendar.getInstance()
            if (failedLastTime) {
                repository.changeChatInput(detailCopy.name, input, calendar)
            } else {
                repository.insertHistory(BotHistoryData(name = detailCopy.name, input = input, time = calendar))
            }
            if (_detail.value.name == detailCopy.name) {
                refreshDisplayedHistory(detailCopy.name)
            }
            refreshBotList()
            val reply = networkHandler.getReply(
                host = _host.value,
                port = _port.value,
                user = _user.value,
                history = repository.getValidHistory(detailCopy.name),
                detail = detailCopy
            )
            if (reply == null) {
                showToast(context.getString(R.string.toast_reply_error) + networkHandler.getReason(), context)
                _serverFeedback.value = ServerFeedback.FAILED
            } else if ("unauthorized" == reply.status) {
                _serverFeedback.value = ServerFeedback.UNAUTHORIZED
            } else {
                _todayUsage.value = reply.todayUsage
                _maxUsage.value = reply.maxUsage
                if ("reach_limit" == reply.status) {
                    _serverFeedback.value = ServerFeedback.REACH_LIMIT
                } else {
                    _serverFeedback.value = ServerFeedback.OK
                    if ("ok" == reply.status) {
                        if ("" != reply.newSummary) {
                            detailCopy.summary = reply.newSummary
                        }
                        detailCopy.lastUsage = reply.lastUsage
                        detailCopy.totalUsage += reply.lastUsage
                        detailCopy.startTime = reply.startTime
                        detailCopy.summaryCutoff = reply.summaryCutoff
                        repository.updateDetail(detailCopy)
                        repository.completeChatOutput(detailCopy.name, reply.message)
                        if (_detail.value.name == detailCopy.name) {
                            refreshDetail(detailCopy.name)
                        }
                        refreshBotList()
                    } else {
                        showToast(context.getString(R.string.toast_reply_error) + reply.status, context)
                    }
                }
            }
            if (_detail.value.name == detailCopy.name) {
                refreshDisplayedHistory(detailCopy.name)
            }
            _chattingWith.value = ""
        }
    }

    fun adjustBot(temperature: Float, topP: Float, presencePenalty: Float, frequencyPenalty: Float) {
        viewModelScope.launch {
            _detail.value.temperature = temperature
            _detail.value.topP = topP
            _detail.value.presencePenalty = presencePenalty
            _detail.value.frequencyPenalty = frequencyPenalty
            repository.updateDetail(_detail.value)
        }
    }

    fun clearMemory() {
        viewModelScope.launch {
            _detail.value.summary = "None"
            val calendar = Calendar.getInstance()
            _detail.value.startTime = calendar
            _detail.value.summaryCutoff = calendar
            repository.updateDetail(_detail.value)
        }
    }

    fun deleteBot() {
        viewModelScope.launch {
            repository.deleteBot(_detail.value.name)
            repository.deleteBotHistory(_detail.value.name)
            context.deleteFile("${_detail.value.name}.png")
            refreshBotList()
        }
    }

    fun refreshDetail(name: String) {
        viewModelScope.launch {
            _detail.value = repository.getDetail(name)
        }
    }

    private fun refreshBotList() {
        viewModelScope.launch {
            _botList.value = repository.getBotList() as ArrayList
        }
    }

    fun refreshDisplayedHistory(name: String) {
        viewModelScope.launch {
            var timeStr = ""
            val historyArr = repository.getHistory(name)
            val displayedHistoryArr = ArrayList<DisplayedHistoryData>()
            for (dat in historyArr) {
                val newTimeStr = TimeDisplayUtil.formatTime(dat.time)
                if (newTimeStr != timeStr) {
                    displayedHistoryArr.add(DisplayedHistoryData(ShowType.TIME, newTimeStr))
                    timeStr = newTimeStr
                }
                if (dat.input != null) {
                    if (dat.output == null && "" == _chattingWith.value) {
                        displayedHistoryArr.add(DisplayedHistoryData(ShowType.USER_ERR, dat.input!!))
                    } else {
                        displayedHistoryArr.add(DisplayedHistoryData(ShowType.USER, dat.input!!))
                    }
                }
                if (dat.output != null) {
                    displayedHistoryArr.add(DisplayedHistoryData(ShowType.BOT, dat.output!!))
                }
            }
            _displayedHistory.value = displayedHistoryArr
        }
    }

    init {
        _user.value = sharedPreferences.getString("name", "")!!
        _host.value = sharedPreferences.getString("host", "")!!
        _port.value = sharedPreferences.getInt("port", 80)
        refreshBotList()
        refreshServerFeedback()
    }
}
