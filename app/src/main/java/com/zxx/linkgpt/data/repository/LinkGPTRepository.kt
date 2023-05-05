package com.zxx.linkgpt.data.repository

import com.zxx.linkgpt.data.LinkGPTDao
import com.zxx.linkgpt.data.models.BotBriefData
import com.zxx.linkgpt.data.models.BotDetailData
import com.zxx.linkgpt.data.models.BotHistoryData
import java.util.Calendar

class LinkGPTRepository(private val LinkGPTDao: LinkGPTDao) {
    suspend fun deleteBot(name: String) {
        LinkGPTDao.deleteBot(name)
    }

    suspend fun deleteBotHistory(name: String) {
        LinkGPTDao.deleteBotHistory(name)
    }

    suspend fun newBot(botDetailData: BotDetailData) {
        LinkGPTDao.newBot(botDetailData)
    }

    suspend fun getBotList(): List<BotBriefData> {
        return LinkGPTDao.getBotList()
    }

    suspend fun insertHistory(botHistoryData: BotHistoryData) {
        LinkGPTDao.insertHistory(botHistoryData)
    }

    suspend fun completeChatOutput(name: String, output: String) {
        LinkGPTDao.completeChatOutput(name, output)
    }

    suspend fun changeChatInput(name: String, input: String, time: Calendar) {
        LinkGPTDao.changeChatInput(name, input, time)
    }

    suspend fun getValidHistory(name: String): List<BotHistoryData> {
        return LinkGPTDao.getValidHistory(name)
    }

    suspend fun getHistory(name: String): List<BotHistoryData> {
        return LinkGPTDao.getHistory(name)
    }

    suspend fun getDetail(name: String): BotDetailData {
        return LinkGPTDao.getDetail(name)
    }

    suspend fun updateDetail(detailData: BotDetailData) {
        LinkGPTDao.updateDetail(detailData)
    }
}
