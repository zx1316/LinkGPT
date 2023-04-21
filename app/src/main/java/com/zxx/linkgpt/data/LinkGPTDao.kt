package com.zxx.linkgpt.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.zxx.linkgpt.data.models.BotBriefData
import com.zxx.linkgpt.data.models.BotDetailData
import com.zxx.linkgpt.data.models.BotHistoryData
import java.util.*

@Dao
interface LinkGPTDao {
    @Query("DELETE FROM detail_table WHERE name = :name")
    suspend fun deleteBot(name: String)

    @Query("DELETE FROM history_table WHERE name = :name")
    suspend fun deleteBotHistory(name: String)

    @Insert
    suspend fun newBot(botDetailData: BotDetailData)

    @Query("UPDATE detail_table SET temperature = :temperature, topP = :topP, presencePenalty = :presencePenalty, frequencyPenalty = :frequencyPenalty, useDefaultImage = :useDefaultImage WHERE name = :name")
    suspend fun adjustBot(
        name: String,
        temperature: Float,
        topP: Float,
        presencePenalty: Float,
        frequencyPenalty: Float,
        useDefaultImage: Boolean
    )

    @Query(
        "SELECT detail_table.name, history_table.output, history_table.time, detail_table.useDefaultImage " +
                "FROM detail_table LEFT OUTER JOIN (" +
                "history_table JOIN (" +
                "SELECT name, MAX(time) AS max_time FROM history_table GROUP BY name" +
                ") tmp ON history_table.name = tmp.name AND history_table.time = tmp.max_time" +
                ") ON detail_table.name = history_table.name ORDER BY history_table.time DESC"
    )
    suspend fun getBotList(): List<BotBriefData>

    @Query("SELECT name FROM detail_table")
    suspend fun getNameList(): List<String>

    @Insert
    suspend fun insertHistory(botHistoryData: BotHistoryData)

    @Query("SELECT * FROM history_table WHERE name = :name and time > (SELECT summaryTime from detail_table WHERE name = :name) and input IS NOT NULL and output IS NOT NULL")
    suspend fun getValidHistory(name: String): List<BotHistoryData>

    @Query("SELECT * FROM detail_table WHERE name = :name")
    suspend fun getDetail(name: String): List<BotDetailData>

    @Query("UPDATE detail_table SET summary = :summary, summaryTime = :time WHERE name = :name")
    suspend fun updateSummary(name: String, summary: String, time: Calendar)
}
