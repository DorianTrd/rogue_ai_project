package com.example.rogue_ai_project.network

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class RoomsApi(
    private val client: OkHttpClient = defaultClient(),
    private val baseUrl: String = "https://backend.rogueai.surpuissant.io"
) {

    suspend fun createRoom(): String = withContext(Dispatchers.IO) {
        val request = Request.Builder()
            .url("$baseUrl/create-room")
            .post("{}".toRequestBody("application/json".toMediaType()))
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) error("HTTP error: ${response.code}")
            val body = response.body?.string() ?: error("Empty response")
            val json = JSONObject(body)
            json.optString("roomCode", "")
        }
    }

    suspend fun roomExists(roomCode: String): Boolean = withContext(Dispatchers.IO) {
        val request = Request.Builder()
            .url("$baseUrl/room-exists/$roomCode")
            .get()
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) return@withContext false
            val body = response.body?.string() ?: return@withContext false
            val json = JSONObject(body)
            json.optBoolean("exists", false)
        }
    }

    companion object {
        private fun defaultClient(): OkHttpClient {
            val logging = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BASIC
            }
            return OkHttpClient.Builder()
                .addInterceptor(logging)
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .build()
        }
    }
}
