package com.zxx.linkgpt.viewmodel

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.zxx.linkgpt.data.LinkGPTDatabase
import com.zxx.linkgpt.data.models.BotBriefData
import com.zxx.linkgpt.data.models.BotDetailData
import com.zxx.linkgpt.data.models.BotHistoryData
import com.zxx.linkgpt.data.repository.LinkGPTRepository
import com.zxx.linkgpt.network.NetworkHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar

class LinkGPTViewModel(application: Application) : AndroidViewModel(application) {
    private val sharedPreferences: SharedPreferences =
        application.applicationContext.getSharedPreferences(
            "com.zxx.linkgpt.PREFERENCE_FILE_KEY",
            Context.MODE_PRIVATE
        )
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
    private val _history = MutableStateFlow(ArrayList<BotHistoryData>())

    val botList = _botList.asStateFlow()
    val user = _user.asStateFlow()
    val host = _host.asStateFlow()
    val port = _port.asStateFlow()
    val todayUsage = _todayUsage.asStateFlow()
    val maxUsage = _maxUsage.asStateFlow()
    val serverFeedback = _serverFeedback.asStateFlow()
    val chattingWith = _chattingWith.asStateFlow()
    val history = _history.asStateFlow()

    fun addBot(
        name: String,
        settings: String,
        temperature: Float,
        topP: Float,
        presencePenalty: Float,
        frequencyPenalty: Float
    ) {
        viewModelScope.launch {
            repository.newBot(
                BotDetailData(
                    name = name,
                    settings = settings,
                    temperature = temperature,
                    topP = topP,
                    presencePenalty = presencePenalty,
                    frequencyPenalty = frequencyPenalty
                )
            )
            repository.insertHistory(
                BotHistoryData(
                    name = name,
                    output = "机器人已创建，快来聊天吧！",
                )
            )
            getBotList()
        }
    }

    fun setUserConfig(name: String, host: String, port: Int) {
        _user.value = name
        _host.value = host
        _port.value = port
        checkServer()
        sharedPreferences.edit()
            .putString("name", name)
            .putString("host", host)
            .putInt("port", port)
            .apply()
    }

    fun checkServer() {
        if ("" == _host.value) {
            _serverFeedback.value = ServerFeedback.FAILED
        } else {
            viewModelScope.launch {
                _serverFeedback.value = ServerFeedback.REFRESHING
                val userDetail = networkHandler.checkUser(_user.value, _host.value, _port.value)
                if (userDetail == null) {
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
    }

    fun chat(bot: String, input: String) {
        viewModelScope.launch {
            _chattingWith.value = bot
            repository.insertHistory(BotHistoryData(name = bot, input = input))
            getBotList()
            _history.value = repository.getHistory(bot) as ArrayList
            val detail = repository.getDetail(bot)
            val reply = networkHandler.getReply(_host.value, _port.value, _user.value, repository.getValidHistory(bot), detail)
            if (reply == null) {
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
                    if ("OK" == reply.status) {
                        if ("" != reply.newSummary) {
                            repository.updateSummary(bot, reply.newSummary, Calendar.getInstance())
                        }
                        repository.updateTokens(bot, reply.lastUsage, reply.lastUsage + detail.totalUsage)
                        repository.completeHistory(bot, reply.message)
                        getBotList()
                    }
                }
            }
            _chattingWith.value = ""
        }
    }

    fun getHistory(name: String) {
        viewModelScope.launch {
            _history.value = repository.getHistory(name) as ArrayList
        }
    }

    private fun getBotList() {
        viewModelScope.launch {
            _botList.value = repository.getBotList() as ArrayList
        }
    }

    init {
        _user.value = sharedPreferences.getString("name", "")!!
        _host.value = sharedPreferences.getString("host", "")!!
        _port.value = sharedPreferences.getInt("port", 80)
        getBotList()
        checkServer()
    }
}
