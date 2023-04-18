package com.zxx.linkgpt.data.repository

import android.net.Uri
import androidx.lifecycle.LiveData
import com.zxx.linkgpt.data.LinkGPTDao
import com.zxx.linkgpt.data.models.BotBriefData
import com.zxx.linkgpt.data.models.BotDetailData
import com.zxx.linkgpt.data.models.BotHistoryData
import java.util.*

class LinkGPTRepository(private val LinkGPTDao: LinkGPTDao) {
    suspend fun deleteBot(name: String) {
        LinkGPTDao.deleteBot(name)
    }

    suspend fun newBot(botDetailData: BotDetailData) {
        LinkGPTDao.newBot(botDetailData)
    }

    suspend fun adjustBot(name: String, temperature: Double, topP: Double, presencePenalty: Double, frequencyPenalty: Double, image: Uri) {
        LinkGPTDao.adjustBot(name, temperature, topP, presencePenalty, frequencyPenalty, image)
    }

    suspend fun getBotList(): LiveData<List<BotBriefData>> {
        return LinkGPTDao.getBotList()
    }

    suspend fun insertHistory(botHistoryData: BotHistoryData) {
        LinkGPTDao.insertHistory(botHistoryData)
    }

    suspend fun getValidHistory(name: String): LiveData<List<BotHistoryData>> {
        return LinkGPTDao.getValidHistory(name)
    }

    suspend fun getDetail(name: String): LiveData<BotDetailData> {
        return LinkGPTDao.getDetail(name)
    }

    suspend fun updateSummary(name: String, summary: String, time: Date) {
        LinkGPTDao.updateSummary(name, summary, time)
    }
}