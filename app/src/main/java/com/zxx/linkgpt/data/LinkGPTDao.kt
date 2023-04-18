package com.zxx.linkgpt.data

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.zxx.linkgpt.data.models.BotBriefData
import com.zxx.linkgpt.data.models.BotDetailData
import com.zxx.linkgpt.data.models.BotHistoryData
import java.util.*

@Dao
interface LinkGPTDao {
    @Query("DELETE FROM history_table WHERE name = :name; DELETE FROM detail_table WHERE name = :name;")
    suspend fun deleteBot(name: String)

    @Insert
    suspend fun newBot(botDetailData: BotDetailData)

    @Query("UPDATE detail_table SET temperature = :temperature, topP = :topP, presencePenalty = :presencePenalty, frequencyPenalty = :frequencyPenalty, image = :image WHERE name = :name;")
    suspend fun adjustBot(name: String, temperature: Double, topP: Double, presencePenalty: Double, frequencyPenalty: Double, image: Uri)

    @Query("SELECT detail_table.name, history_table.output, history_table.time, detail_table.image, detail_table.personality " +
            "FROM detail_table LEFT OUTER JOIN (" +
            "history_table JOIN (" +
            "SELECT name, MAX(time) AS max_time FROM history_table ORDER BY name" +
            ") temp ON history_table.name = temp.name AND history_table.time = temp.max_time" +
            ") ON detail_table.name = history_table.name;")
    suspend fun getBotList(): LiveData<List<BotBriefData>>

    @Insert
    suspend fun insertHistory(botHistoryData: BotHistoryData)

    @Query("SELECT * FROM history_table WHERE name = :name and time > ALL(SELECT summaryTime from detail_table WHERE name = :name);")
    suspend fun getValidHistory(name: String): LiveData<List<BotHistoryData>>

    @Query("SELECT * FROM detail_table WHERE name = :name;")
    suspend fun getDetail(name: String): LiveData<BotDetailData>

    @Query("UPDATE detail_table SET summary = :summary, summaryTime = :time WHERE name = :name;")
    suspend fun updateSummary(name: String, summary: String, time: Date)
}