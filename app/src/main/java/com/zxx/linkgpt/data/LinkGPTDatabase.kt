package com.zxx.linkgpt.data

import android.content.Context
import androidx.room.*
import com.zxx.linkgpt.data.models.BotDetailData
import com.zxx.linkgpt.data.models.BotHistoryData

@Database(
    entities = [BotHistoryData::class, BotDetailData::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converter::class)
abstract class LinkGPTDatabase : RoomDatabase() {
    abstract fun linkGPTDao(): LinkGPTDao

    companion object {
        @Volatile
        private var INSTANCE: LinkGPTDatabase? = null

        fun getDatabase(context: Context): LinkGPTDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LinkGPTDatabase::class.java, "linkgpt_database"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}
