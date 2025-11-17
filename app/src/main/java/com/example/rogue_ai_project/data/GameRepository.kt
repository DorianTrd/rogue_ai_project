package com.example.rogue_ai_project.data

import com.example.rogue_ai_project.network.RoomsApi

class GameRepository(private val api: RoomsApi = RoomsApi()) {

    suspend fun createRoom(): String = api.createRoom()

    suspend fun roomExists(code: String): Boolean = api.roomExists(code)
}
