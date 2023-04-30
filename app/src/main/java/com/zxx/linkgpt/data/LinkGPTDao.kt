package com.zxx.linkgpt.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
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

//    @Query("UPDATE detail_table SET temperature = :temperature, topP = :topP, presencePenalty = :presencePenalty, frequencyPenalty = :frequencyPenalty WHERE name = :name")
//    suspend fun adjustBot(
//        name: String,
//        temperature: Float,
//        topP: Float,
//        presencePenalty: Float,
//        frequencyPenalty: Float,
//    )

    @Query(
        "SELECT detail_table.name, history_table.output, history_table.time " +
                "FROM detail_table LEFT OUTER JOIN (" +
                "history_table JOIN (" +
                "SELECT name, MAX(time) AS max_time FROM history_table GROUP BY name" +
                ") tmp ON history_table.name = tmp.name AND history_table.time = tmp.max_time" +
                ") ON detail_table.name = history_table.name ORDER BY history_table.time DESC"
    )
    suspend fun getBotList(): List<BotBriefData>

    @Insert
    suspend fun insertHistory(botHistoryData: BotHistoryData)

    @Query("UPDATE history_table SET output = :output WHERE name = :name AND time = (SELECT MAX(time) FROM history_table WHERE name = :name)")
    suspend fun completeChatOutput(name: String, output: String)

    @Query("UPDATE history_table SET input = :input, time = :time WHERE name = :name AND time = (SELECT MAX(time) FROM history_table WHERE name = :name)")
    suspend fun changeChatInput(name: String, input: String, time: Calendar)

    @Query("SELECT * FROM history_table WHERE name = :name AND time > (SELECT startTime from detail_table WHERE name = :name) AND input IS NOT NULL ORDER BY time ASC")
    suspend fun getValidHistory(name: String): List<BotHistoryData>

    @Query("SELECT * FROM history_table WHERE name = :name ORDER BY time ASC")
    suspend fun getHistory(name: String): List<BotHistoryData>

    @Query("SELECT * FROM detail_table WHERE name = :name")
    suspend fun getDetail(name: String): BotDetailData

    @Update
    suspend fun updateDetail(detailData: BotDetailData)
}
