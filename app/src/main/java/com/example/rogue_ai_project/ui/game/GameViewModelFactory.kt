package com.example.rogue_ai_project.ui.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.rogue_ai_project.data.GamePlayRepository
import com.example.rogue_ai_project.data.LobbyRepository
import com.example.rogue_ai_project.network.RoomSocket
import com.example.rogue_ai_project.util.SfxManager

/**
 * Factory responsible for creating instances of [GameViewModel].
 *
 * This factory injects all required dependencies needed during
 * the gameplay phase, including repositories and sound effects manager.
 */
class GameViewModelFactory(
    private val roomSocket: RoomSocket,
    private val sfxManager: SfxManager
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GameViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GameViewModel(
                gamePlayRepository = GamePlayRepository(roomSocket),
                lobbyRepository = LobbyRepository(roomSocket),
                sfxManager = sfxManager
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}