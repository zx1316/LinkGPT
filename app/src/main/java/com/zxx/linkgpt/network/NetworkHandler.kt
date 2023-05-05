package com.zxx.linkgpt.network

import android.util.Base64
import com.fasterxml.jackson.databind.ObjectMapper
import com.zxx.linkgpt.data.models.BotDetailData
import com.zxx.linkgpt.data.models.BotHistoryData
import com.zxx.linkgpt.network.models.ReplyData
import com.zxx.linkgpt.network.models.UserDetailData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import okhttp3.Headers
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.util.Calendar
import java.util.Date
import java.util.Random
import java.util.concurrent.TimeUnit

class NetworkHandler {
    companion object {
        @JvmStatic
        val MAPPER = ObjectMapper()
    }

    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(5, TimeUnit.SECONDS)
        .readTimeout(300, TimeUnit.SECONDS)
        .retryOnConnectionFailure(false)
        .build()
    private val headers = Headers.Builder()
        .add("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7")
        .add("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8,en-GB;q=0.7,en-US;q=0.6")
        .add("Cache-Control", "max-age=0")
        .add("Connection", "keep-alive")
        .add("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/112.0.0.0 Safari/537.36 Edg/112.0.1722.48")
        .build()
    private var reason: String = ""

    fun getReason(): String {
        return reason
    }

    /**
     * Verify if the user is authorized and get the user's usage of tokens.
     * @author zx1316
     * @param user The user's name.
     * @param host The server's hostname. Can be ipv4/ipv6/domain.
     * @param port The server's port.
     * @return If the connection fails, return null. Otherwise return the user's detail.
     * @see UserDetailData
     */
    suspend fun checkUser(user: String, host: String, port: Int): UserDetailData? =
        withContext(Dispatchers.IO) {
            // Delete '=' at the end of the base64 string because the server can still parse it.
            // But why the output of fucking Android Base64.encodeToString has '\n'???
            /*
            val encoded = Base64.encodeToString(user.toByteArray(), Base64.URL_SAFE)
                .replace("=", "")
                .replace("\n", "")
            val url = HttpUrl.Builder().scheme("http").host(host).port(port)
                .addPathSegment("index.html")
                .addQueryParameter("dat", encoded)
                .build()
            val request = Request.Builder().url(url).headers(headers).get().build()
            return@withContext processWebpage(request, UserDetailData::class.java)
            */
            delay(1000)
            return@withContext UserDetailData(
                authorized = true,
                todayUsage = 233,
                maxUsage = 23333
            )
        }

    /**
     * Submit the data for chatting to the server and get the reply of GPT-3.5-Turbo.
     * @author zx1316
     * @param host The server's hostname. Can be ipv4/ipv6/domain.
     * @param port The server's port.
     * @param user User's name
     * @param history the chat history of bot from the database.
     * @param detail the detail of bot from the database.
     * @return If the connection fails, return null. Otherwise return the reply of GPT-3.5-Turbo from the server.
     * @see ReplyData
     */
    suspend fun getReply(host: String, port: Int, user: String, history: List<BotHistoryData>, detail: BotDetailData): ReplyData? =
        withContext(Dispatchers.IO) {
            /*
            val submitData = SubmitData(
                userName = user,
                bot = detail.name,
                summary = detail.summary,
                settings = detail.settings,
                history = history as ArrayList<BotHistoryData>,
                temperature = detail.temperature,
                topP = detail.topP,
                presencePenalty = detail.presencePenalty,
                frequencyPenalty = detail.frequencyPenalty,
                summaryCutoff = detail.summaryCutoff
            )
            val url = HttpUrl.Builder().scheme("http").host(host).port(port).addPathSegment("/index.html").build()
            // To simplify, we don't use a ByteArrayOutputStream.
            val deflater = Deflater(6, true)
            val buf = ByteArray(8192)              // Big enough for our application
            deflater.setInput(MAPPER.writeValueAsBytes(submitData))
            deflater.finish()
            val len = deflater.deflate(buf)
            deflater.end()
            val requestBody = MultipartBody.Builder()
                .addFormDataPart("input", String(buf, 0, len))
                .addFormDataPart("encode", "编码")
                .build()
            val request = Request.Builder().url(url).headers(headers).post(requestBody).build()
            return@withContext processWebpage(request, ReplyData::class.java)
            */
            delay(2000)
            val calendar = Calendar.getInstance()
            calendar.time = Date(0)
            val random = Random()
            if (random.nextBoolean()) {
                return@withContext ReplyData(
                    status = "OK",
                    message = ("测试test".repeat(6) + "\n\n").repeat(2) + "12345678".repeat(6),
                    newSummary = "",
                    startTime = calendar,
                    summaryCutoff = calendar,
                    lastUsage = 233,
                    todayUsage = 1000,
                    maxUsage = 10000
                )
            }
            return@withContext ReplyData(
                status = "???????",
                message = "",
                newSummary = "",
                startTime = calendar,
                summaryCutoff = calendar,
                lastUsage = 0,
                todayUsage = 1000,
                maxUsage = 10000
            )
        }

    private fun <T> processWebpage(request: Request, clazz: Class<T>): T? {
        try {
            val response = httpClient.newCall(request).execute()
            if (response.code != 200) {
                reason = "bad_code"
                return null
            }
            val startMatch = "<textarea id=\"magicInput\" name=\"input\" placeholder=\"在这里输入字符串\">"
            val endMatch = "</textarea>"
            val responseBody = response.body
            responseBody?.let {
                val webpage = responseBody.string()
                val startResult: Pair<Int, String>? = webpage.findAnyOf(List(1) { startMatch })
                val endResult: Pair<Int, String>? = webpage.findAnyOf(List(1) { endMatch })
                if (startResult == null || endResult == null) {
                    reason = "bad_webpage"
                    return null
                }
                val startPos = startResult.first + startMatch.length
                val endPos = endResult.first
                val raw = webpage.substring(startPos, endPos)
                return try {
                    MAPPER.readValue(String(Base64.decode(raw, Base64.DEFAULT)), clazz)
                } catch (e: Exception) {
                    reason = "bad_format"
                    null
                }
            }
            reason = "empty_response"
            return null
        } catch (e: IOException) {
            e.printStackTrace()
            reason = "time_out"
            return null
        }
    }
}
