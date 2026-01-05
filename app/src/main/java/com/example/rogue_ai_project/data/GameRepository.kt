package com.example.rogue_ai_project.data

import com.example.rogue_ai_project.network.RoomsApi

/**
 * Repository responsible for game-related backend operations.
 *
 * This repository provides a clean abstraction over [RoomsApi],
 * allowing ViewModels and UI layers to interact with room-related
 * network operations without depending directly on the API layer.
 */
class GameRepository(private val api: RoomsApi = RoomsApi()) {

    suspend fun createRoom(): String = api.createRoom()

    suspend fun roomExists(code: String): Boolean = api.roomExists(code)
}
