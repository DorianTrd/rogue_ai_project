package com.example.rogue_ai_project.ui.lobby

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.rogue_ai_project.data.LobbyRepository
import com.example.rogue_ai_project.network.RoomSocket

/**
 * Factory for creating LobbyViewModel instances.
 *
 * Allows passing dependencies (roomCode, RoomSocket) to the ViewModel.
 * Required because LobbyViewModel has constructor parameters.
 */
class LobbyViewModelFactory(
    private val roomCode: String,
    private val roomSocket: RoomSocket
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LobbyViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LobbyViewModel(
                roomCode = roomCode,
                repository = LobbyRepository(roomSocket)
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
