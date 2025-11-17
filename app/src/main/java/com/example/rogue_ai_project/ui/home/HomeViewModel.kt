package com.example.rogue_ai_project.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rogue_ai_project.data.GameRepository
import com.example.rogue_ai_project.util.SfxManager
import com.example.rogue_ai_project.util.isValidRoomCode
import com.example.rogue_ai_project.util.normalizeRoomCode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val sfxManager: SfxManager,
    private val repository: GameRepository = GameRepository()
) : ViewModel() {

    private val _roomCode = MutableStateFlow("")
    val roomCode: StateFlow<String> = _roomCode.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _navigationEvent = MutableStateFlow<String?>(null)
    val navigationEvent: StateFlow<String?> = _navigationEvent.asStateFlow()

    fun createRoom() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            runCatching { repository.createRoom() }
                .onSuccess { code ->
                    _roomCode.value = code
                    _navigationEvent.value = code
                }
                .onFailure { e ->
                    _error.value = e.message ?: "Erreur création salle"
                }

            _isLoading.value = false
        }
    }

    fun joinRoom(code: String) {
        val normalized = normalizeRoomCode(code)

        if (!isValidRoomCode(normalized)) {
            _error.value = "Code invalide (6 caractères A-Z, 0-9)"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            runCatching { repository.roomExists(normalized) }
                .onSuccess { exists ->
                    if (exists) {
                        _navigationEvent.value = normalized
                    } else {
                        _error.value = "Salle introuvable"
                    }
                }
                .onFailure { e ->
                    _error.value = e.message ?: "Erreur connexion"
                }

            _isLoading.value = false
        }
    }

    fun clearNavigation() {
        _navigationEvent.value = null
        _roomCode.value = ""
    }

}

