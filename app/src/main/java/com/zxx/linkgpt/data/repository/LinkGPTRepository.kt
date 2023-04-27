package com.zxx.linkgpt.data.repository

import com.zxx.linkgpt.data.LinkGPTDao
import com.zxx.linkgpt.data.models.BotBriefData
import com.zxx.linkgpt.data.models.BotDetailData
import com.zxx.linkgpt.data.models.BotHistoryData
import java.util.*

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

    suspend fun adjustBot(
        name: String,
        temperature: Float,
        topP: Float,
        presencePenalty: Float,
        frequencyPenalty: Float,
    ) {
        LinkGPTDao.adjustBot(
            name,
            temperature,
            topP,
            presencePenalty,
            frequencyPenalty,
        )
    }

    suspend fun getBotList(): List<BotBriefData> {
        return LinkGPTDao.getBotList()
    }

    suspend fun insertHistory(botHistoryData: BotHistoryData) {
        LinkGPTDao.insertHistory(botHistoryData)
    }

    suspend fun completeHistory(name: String, output: String) {
        LinkGPTDao.completeHistory(name, output)
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

    suspend fun updateSummary(name: String, summary: String, time: Calendar) {
        LinkGPTDao.updateSummary(name, summary, time)
    }

    suspend fun updateTokens(name: String, lastUsage: Int, totalUsage: Int) {
        LinkGPTDao.updateTokens(name, lastUsage, totalUsage)
    }
}
