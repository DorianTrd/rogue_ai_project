package com.example.rogue_ai_project.ui.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rogue_ai_project.data.GamePlayRepository
import com.example.rogue_ai_project.data.LobbyRepository
import com.example.rogue_ai_project.model.Command
import com.example.rogue_ai_project.model.Instruction
import com.example.rogue_ai_project.util.SfxManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

class GameViewModel(
    private val gamePlayRepository: GamePlayRepository,
    private val lobbyRepository: LobbyRepository,
    private val sfxManager: SfxManager
) : ViewModel() {

    private val _commands = MutableStateFlow<List<Command>>(emptyList())
    val commands: StateFlow<List<Command>> = _commands.asStateFlow()

    private val _instruction = MutableStateFlow<Instruction?>(null)
    val instruction: StateFlow<Instruction?> = _instruction.asStateFlow()

    private val _threat = MutableStateFlow(25)
    val threat: StateFlow<Int> = _threat.asStateFlow()

    private val _timeRemaining = MutableStateFlow(0L)
    val timeRemaining: StateFlow<Long> = _timeRemaining.asStateFlow()

    private val _gameOver = MutableStateFlow(false)
    val gameOver: StateFlow<Boolean> = _gameOver.asStateFlow()

    private val _victory = MutableStateFlow(false)
    val victory: StateFlow<Boolean> = _victory.asStateFlow()

    init {
        sfxManager.startRandomLoop()
        observeGame()
    }

    private fun observeGame() {
        viewModelScope.launch {
            gamePlayRepository.observePlayerBoard().collect { board ->
                board?.let {
                    _commands.value = it.board.commands
                    _instruction.value = it.instruction
                    _threat.value = it.threat

                    val now = System.currentTimeMillis()
                    val elapsed = now - it.instruction.timestampCreation
                    val remaining = (it.instruction.timeout - elapsed).coerceAtLeast(0)
                    _timeRemaining.value = remaining
                }
            }
        }

        viewModelScope.launch {
            lobbyRepository.observeGameState().collect { state ->
                when (state?.state) {
                    "end_state" -> {
                        _gameOver.value = true
                        _victory.value = state.win == true
                        sfxManager.stopRandomLoop()
                    }
                    else -> {
                        if (state?.state == "lobby_ready" || state?.state == "lobby_waiting") {
                            _gameOver.value = false
                            _victory.value = false
                        }
                    }
                }
            }
        }

        // Ticker coroutine: met à jour le chrono de façon continue en se basant sur l'instruction courante
        viewModelScope.launch {
            while (isActive) {
                val instr = _instruction.value
                if (instr != null) {
                    val now = System.currentTimeMillis()
                    val elapsed = now - instr.timestampCreation
                    val remaining = (instr.timeout - elapsed).coerceAtLeast(0)
                    _timeRemaining.value = remaining
                } else {
                    // Pas d'instruction : afficher 0
                    _timeRemaining.value = 0L
                }
                delay(100L) // mise à jour toutes les 100ms
            }
        }
    }

    fun executeAction(commandId: String, action: String) {
        gamePlayRepository.sendExecuteAction(commandId, action)
    }

    override fun onCleared() {
        sfxManager.stopRandomLoop()
        super.onCleared()
        _commands.value = emptyList()
        _instruction.value = null
        _threat.value = 25
        _gameOver.value = false
        _victory.value = false
    }
}