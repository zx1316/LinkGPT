package com.zxx.linkgpt.network

import android.util.Base64
import com.fasterxml.jackson.databind.ObjectMapper
import com.zxx.linkgpt.network.models.ReplyData
import com.zxx.linkgpt.network.models.SubmitData
import com.zxx.linkgpt.network.models.UserDetailData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.*
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.concurrent.TimeUnit
import java.util.zip.GZIPOutputStream

class NetworkHandler {
    companion object {
        @JvmStatic
        val MAPPER = ObjectMapper()
    }

    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(150, TimeUnit.SECONDS)
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
            val url = HttpUrl.Builder().host(host).port(port).scheme("http")
                .addQueryParameter("dat", Base64.encodeToString(user.toByteArray(), Base64.URL_SAFE))
                .build()
            val request = Request.Builder().url(url).headers(headers).get().build()
            return@withContext processWebpage(request, UserDetailData::class.java)
        }

    /**
     * Submit the data for chatting to the server and get the reply of GPT-3.5-Turbo.
     * @author zx1316
     * @param host The server's hostname. Can be ipv4/ipv6/domain.
     * @param port The server's port.
     * @param submit The data sent to the server.
     * @return If the connection fails, return null. Otherwise return the reply of GPT-3.5-Turbo from the server.
     * @see SubmitData
     * @see ReplyData
     * @see SubmitDataGenerator
     */
    suspend fun getReply(host: String, port: Int, submit: SubmitData): ReplyData? =
        withContext(Dispatchers.IO) {
            val url = HttpUrl.Builder().host(host).port(port).scheme("http").build()
            val out = ByteArrayOutputStream()
            val gzip = GZIPOutputStream(out)
            MAPPER.writeValue(gzip, submit)
            gzip.finish()
            val requestBody = MultipartBody.Builder()
                .addFormDataPart("input", out.toString())
                .addFormDataPart("upload", "提交")
                .build()
            val request = Request.Builder().url(url).headers(headers).post(requestBody).build()
            return@withContext processWebpage(request, ReplyData::class.java)
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
                    reason = "bad_format"
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
