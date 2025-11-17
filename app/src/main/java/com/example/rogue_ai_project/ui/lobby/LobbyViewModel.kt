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

    private fun observeRoom() {
        viewModelScope.launch {
            repository.observeRoomInfo().collect { info ->
                info?.let { updatePlayers(it) }
            }
        }

        viewModelScope.launch {
            repository.observeGameState().collect { state ->
                if (state?.state == "game_start") {
                    _gameStarted.value = true
                }
            }
        }
    }

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

    fun toggleReady() {
        val newReady = !_isReady.value
        repository.sendReady(newReady)
    }

    fun refreshName() {
        repository.refreshName()
    }

    override fun onCleared() {
        super.onCleared()
        repository.disconnect()
    }
}
