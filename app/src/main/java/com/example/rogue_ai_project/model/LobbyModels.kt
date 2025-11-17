package com.example.rogue_ai_project.model

data class LobbyPlayer(
    val id: String,
    val displayName: String,
    val isReady: Boolean,
    val isHost: Boolean
)