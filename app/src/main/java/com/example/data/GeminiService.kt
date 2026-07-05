package com.example.data

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

interface GeminiApiService {
    @POST("v1beta/models/gemini-3.5-flash:generateContent")
    suspend fun generateContent(
        @Query("key") apiKey: String,
        @Body requestBody: okhttp3.RequestBody
    ): ResponseBody
}

object RetrofitClient {
    private const val BASE_URL = "https://generativelanguage.googleapis.com/"

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    val service: GeminiApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .build()
        retrofit.create(GeminiApiService::class.java)
    }

    // Helper to construct request JSON body
    fun createRequestBody(prompt: String): okhttp3.RequestBody {
        val partsObj = JSONObject().apply {
            put("text", prompt)
        }
        val partsArray = JSONArray().apply {
            put(partsObj)
        }
        val contentsObj = JSONObject().apply {
            put("parts", partsArray)
        }
        val contentsArray = JSONArray().apply {
            put(contentsObj)
        }
        val rootObj = JSONObject().apply {
            put("contents", contentsArray)
        }
        return rootObj.toString().toRequestBody("application/json".toMediaType())
    }

    // Helper to extract text from raw response
    fun extractTextFromResponse(responseString: String): String? {
        return try {
            val root = JSONObject(responseString)
            val candidates = root.optJSONArray("candidates")
            val firstCandidate = candidates?.optJSONObject(0)
            val content = firstCandidate?.optJSONObject("content")
            val parts = content?.optJSONArray("parts")
            val firstPart = parts?.optJSONObject(0)
            firstPart?.optString("text")
        } catch (e: Exception) {
            null
        }
    }
}
