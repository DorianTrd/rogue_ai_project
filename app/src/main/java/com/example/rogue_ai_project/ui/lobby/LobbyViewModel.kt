package com.example.rogue_ai_project.ui.lobby

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rogue_ai_project.data.LobbyRepository
import com.example.rogue_ai_project.model.LobbyPlayer
import com.example.rogue_ai_project.model.RoomInfo
import com.example.rogue_ai_project.network.RoomSocket
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for the Lobby screen.
 *
 * Responsible for:
 * - Managing the list of players
 * - Tracking ready and host status
 * - Observing room and game state via LobbyRepository
 */
class LobbyViewModel(
    private val roomCode: String,
    private val repository: LobbyRepository = LobbyRepository(RoomSocket())
) : ViewModel() {

    private val _players = MutableStateFlow<List<LobbyPlayer>>(emptyList())
    val players: StateFlow<List<LobbyPlayer>> = _players.asStateFlow()

    private val _isReady = MutableStateFlow(false)
    val isReady: StateFlow<Boolean> = _isReady.asStateFlow()

    private val _isHost = MutableStateFlow(false)
    val isHost: StateFlow<Boolean> = _isHost.asStateFlow()

    private val _gameStarted = MutableStateFlow(false)
    val gameStarted: StateFlow<Boolean> = _gameStarted.asStateFlow()

    init {
        repository.connect(roomCode, viewModelScope)
        observeRoom()
    }

    /**
     * Observes room info and game state from the repository.
     */
    private fun observeRoom() {
        // Observe room info (players, your status, host)
        viewModelScope.launch {
            repository.observeRoomInfo().collect { info ->
                info?.let { updatePlayers(it) }
            }
        }
        // Observe game state (start of the game)
        viewModelScope.launch {
            repository.observeGameState().collect { state ->
                if (state?.state == "game_start") {
                    _gameStarted.value = true
                }
            }
        }
    }

    /**
     * Updates the lobby player list and current player's status.
     *
     * Determines host (first player in the list) and ready status.
     */
    private fun updatePlayers(info: RoomInfo) {
        val lobbyPlayers = info.players.map { player ->
            LobbyPlayer(
                id = player.id,
                displayName = player.name,
                isReady = player.ready,
                isHost = player.id == info.players.first().id
            )
        }
        _players.value = lobbyPlayers
        _isReady.value = info.you.ready
        _isHost.value = info.you.id == info.players.first().id
    }

    /**
     * Toggles the ready status of the current player.
     *
     * Sends the new ready state to the repository.
     */
    fun toggleReady() {
        val newReady = !_isReady.value
        repository.sendReady(newReady)
    }

    /**
     * Requests the repository to refresh the current player's display name.
     */
    fun refreshName() {
        repository.refreshName()
    }

    /**
     * Cleanup on ViewModel clearance.
     *
     * Disconnects from the room to avoid memory leaks.
     */
    override fun onCleared() {
        super.onCleared()
        repository.disconnect()
    }
}
