package com.example.rogue_ai_project.data

import com.example.rogue_ai_project.model.PlayerBoard
import com.example.rogue_ai_project.network.RoomSocket
import kotlinx.coroutines.flow.StateFlow

/**
 * Repository responsible for gameplay-related actions and state.
 *
 * This class acts as a thin abstraction layer over [RoomSocket],
 * exposing only the data and operations needed during the game phase.
 */
class GamePlayRepository(private val socket: RoomSocket) {

    fun observePlayerBoard(): StateFlow<PlayerBoard?> = socket.playerBoard

    fun sendExecuteAction(commandId: String, action: String) =
        socket.sendExecuteAction(commandId, action)
}
