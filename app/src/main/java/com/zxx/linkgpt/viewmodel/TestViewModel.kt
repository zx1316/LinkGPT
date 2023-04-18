package com.zxx.linkgpt.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.zxx.linkgpt.data.LinkGPTDatabase
import com.zxx.linkgpt.data.models.BotBriefData
import com.zxx.linkgpt.data.repository.LinkGPTRepository
import kotlinx.coroutines.flow.MutableStateFlow

class TestViewModel(application: Application): AndroidViewModel(application) {
    private val linkGPTDao = LinkGPTDatabase.getDatabase(application).linkGPTDao()
    private val repository: LinkGPTRepository = LinkGPTRepository(linkGPTDao)

    val botList = MutableStateFlow(ArrayList<BotBriefData>())

    private fun updateBotList() {
        botList.value = repository.getBotList() as ArrayList<BotBriefData>
    }

    init {
        updateBotList()
    }
}