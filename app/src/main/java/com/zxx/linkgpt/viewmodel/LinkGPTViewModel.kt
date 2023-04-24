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
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

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
    private val _serverFeedback = MutableStateFlow(ServerFeedback.REFRESHING)
    private val _chattingWith = MutableStateFlow("")

    val botList = _botList.asStateFlow()
    val user = _user.asStateFlow()
    val host = _host.asStateFlow()
    val port = _port.asStateFlow()
    val todayUsage = _todayUsage.asStateFlow()
    val maxUsage = _maxUsage.asStateFlow()
    val serverFeedback = _serverFeedback.asStateFlow()
    var chattingWith = _chattingWith.asStateFlow()

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
            updateList()
        }
    }

    private fun updateList() {
        viewModelScope.launch {
            _botList.value = repository.getBotList() as ArrayList
        }
    }

    fun setUserConfig(name: String, host: String, port: Int) {
        sharedPreferences.edit()
            .putString("name", name)
            .putString("host", host)
            .putInt("port", port)
            .apply()
        _user.value = name
        _host.value = host
        _port.value = port
    }

    fun checkServer() {
        viewModelScope.launch {
            _serverFeedback.value = ServerFeedback.REFRESHING
            delay(3000)                                 // simulate accessing network
            _serverFeedback.value = ServerFeedback.OK
            _maxUsage.value = 50000
            _todayUsage.value = 654


            // real network
            /*
            val userDetail = networkHandler.checkUser(_user.value, _host.value, _port.value)
            if (userDetail == null) {
                _serverFeedback.value = ServerFeedback.FAILED
            } else if (userDetail.valid) {
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
            */
        }
    }

    init {
        updateList()
        _user.value = sharedPreferences.getString("name", "")!!
        _host.value = sharedPreferences.getString("host", "")!!
        _port.value = sharedPreferences.getInt("port", 80)
        checkServer()
    }
}
