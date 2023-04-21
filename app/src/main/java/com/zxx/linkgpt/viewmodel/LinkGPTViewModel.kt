package com.zxx.linkgpt.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.zxx.linkgpt.data.LinkGPTDatabase
import com.zxx.linkgpt.data.models.BotBriefData
import com.zxx.linkgpt.data.models.BotDetailData
import com.zxx.linkgpt.data.models.BotHistoryData
import com.zxx.linkgpt.data.repository.LinkGPTRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlin.collections.ArrayList

class LinkGPTViewModel(application: Application) : AndroidViewModel(application) {
    private val linkGPTDao = LinkGPTDatabase.getDatabase(application).linkGPTDao()
    private val repository: LinkGPTRepository = LinkGPTRepository(linkGPTDao)

    val botList = MutableStateFlow(ArrayList<BotBriefData>())
    var chatWith = MutableLiveData("")

    fun addBot(
        name: String,
        useDefaultImage: Boolean,
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
                    useDefaultImage = useDefaultImage,
                    settings = settings,
                    temperature = temperature,
                    topP = topP,
                    presencePenalty = presencePenalty,
                    frequencyPenalty = frequencyPenalty
                )
            )
            repository.insertHistory(BotHistoryData(
                name = name,
                output = "机器人已创建，快来聊天吧！",
            ))
            botList.value = repository.getBotList() as ArrayList
        }
    }

    suspend fun check(name: String): Boolean {
        val list = repository.getNameList()
        if (list.contains(name)) {
            return false
        }
        return true
    }

    init {
        viewModelScope.launch {
            botList.value = repository.getBotList() as ArrayList
        }
    }
}
