package com.example.rogue_ai_project.model

data class RoomInfo(
    val you: PlayerInfo,
    val players: List<PlayerInfo>,
    val roomState: String,
    val level: Int = 1
)

data class PlayerInfo(
    val id: String,
    val name: String,
    val ready: Boolean
)

data class GameStateMessage(
    val state: String,
    val duration: Int? = null,
    val startThreat: Int? = null,
    val gameDuration: Int? = null,
    val win: Boolean? = null,
    val tryHistory: List<TryHistoryItem>? = null
)

data class TryHistoryItem(
    val time: Long,
    val playerId: String,
    val success: Boolean
)
