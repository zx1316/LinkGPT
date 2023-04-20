package com.zxx.linkgpt.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.*
import com.zxx.linkgpt.data.LinkGPTDatabase
import com.zxx.linkgpt.data.models.BotBriefData
import com.zxx.linkgpt.data.models.BotDetailData
import com.zxx.linkgpt.data.repository.LinkGPTRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class TestViewModel(application: Application): AndroidViewModel(application) {
    private val linkGPTDao = LinkGPTDatabase.getDatabase(application).linkGPTDao()
    private val repository: LinkGPTRepository = LinkGPTRepository(linkGPTDao)

    val botList = MutableStateFlow(ArrayList<BotBriefData>())

    private fun updateBotList() {
        viewModelScope.launch {
            botList.value = repository.getBotList() as ArrayList<BotBriefData>
        }
    }

    fun addBot(
        name: String,
        image: Uri,
        settings: String,
        temperature: Float,
        topP: Float,
        presencePenalty: Float,
        frequencyPenalty: Float
    ) {
        viewModelScope.launch {
            repository.newBot(BotDetailData(
                name = name,
                image = image,
                settings = settings,
                temperature = temperature,
                topP = topP,
                presencePenalty = presencePenalty,
                frequencyPenalty = frequencyPenalty
            ))
        }
        updateBotList()
    }

    init {
        updateBotList()
    }
}