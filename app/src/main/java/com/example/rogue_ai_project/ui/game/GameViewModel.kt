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

/**
 * ViewModel for the gameplay screen.
 *
 * This ViewModel manages:
 * - the player's board state (commands, instructions, threat level),
 * - the remaining time for the current instruction,
 * - game over and victory state,
 * - playing sound effects via [SfxManager].
 *
 * All flows are exposed as read-only [StateFlow] for the UI.
 */
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

    /**
     * Observe the player board and game state from repositories.
     *
     * Updates commands, instruction, threat, remaining time, and game over/victory state.
     */
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
        // Observe game state (lobby, end state)
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
        // Update remaining instruction time every 100ms
         viewModelScope.launch {
            while (isActive) {
                val instr = _instruction.value
                if (instr != null) {
                    val now = System.currentTimeMillis()
                    val elapsed = now - instr.timestampCreation
                    val remaining = (instr.timeout - elapsed).coerceAtLeast(0)
                    _timeRemaining.value = remaining
                } else {
                    _timeRemaining.value = 0L
                }
                delay(100L)
            }
        }
    }
    /**
     * Send an execute action request for a specific command.
     */
    fun executeAction(commandId: String, action: String) {
        gamePlayRepository.sendExecuteAction(commandId, action)
    }

    /**
     * Called when the ViewModel is cleared.
     *
     * Stops SFX loop and resets all internal state.
     */
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