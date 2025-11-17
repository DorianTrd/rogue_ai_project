package com.example.rogue_ai_project.data

import com.example.rogue_ai_project.model.GameStateMessage
import com.example.rogue_ai_project.model.RoomInfo
import com.example.rogue_ai_project.network.RoomSocket
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow

class LobbyRepository(private val socket: RoomSocket) {

    fun connect(roomCode: String, scope: CoroutineScope) =
        socket.openRoomConnection(roomCode, scope)

    fun disconnect() = socket.closeRoomConnection()

    fun observeRoomInfo(): StateFlow<RoomInfo?> = socket.roomInfo

    fun observeGameState(): StateFlow<GameStateMessage?> =
        socket.gameState

    fun sendReady(ready: Boolean) = socket.sendReady(ready)

    fun refreshName() = socket.refreshName()
}
