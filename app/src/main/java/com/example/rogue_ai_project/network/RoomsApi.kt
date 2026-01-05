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

/**
 * REST API client for room-related operations.
 *
 * This class communicates with the backend using HTTP requests to:
 * - create new game rooms,
 * - check whether a room exists.
 *
 * All network calls are executed on the IO dispatcher.
 */
class RoomsApi(
    private val client: OkHttpClient = defaultClient(),
    private val baseUrl: String = "https://backend.rogueai.surpuissant.io"
) {

    /**
     * Create a new room on the backend.
     *
     * Sends a POST request to the `/create-room` endpoint
     * and returns the generated room code.
     *
     * @return the newly created room code.
     * @throws IllegalStateException if the HTTP request fails or the response is invalid.
     */
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

    /**
     * Check whether a room exists on the backend.
     *
     * Sends a GET request to the `/room-exists/{roomCode}` endpoint.
     *
     * @param roomCode the room code to verify.
     * @return true if the room exists, false otherwise.
     */
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

        /**
         * Create a default OkHttpClient for REST API calls.
         *
         * - Enables basic HTTP logging,
         * - Sets reasonable connect and read timeouts.
         */
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
