package com.example.rogue_ai_project

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.rogue_ai_project.data.GameRepository
import com.example.rogue_ai_project.network.RoomSocket
import com.example.rogue_ai_project.ui.game.GameOverScreen
import com.example.rogue_ai_project.ui.game.GameScreen
import com.example.rogue_ai_project.ui.game.GameViewModel
import com.example.rogue_ai_project.ui.game.GameViewModelFactory
import com.example.rogue_ai_project.ui.home.HomeScreen
import com.example.rogue_ai_project.ui.home.HomeViewModel
import com.example.rogue_ai_project.ui.home.HomeViewModelFactory
import com.example.rogue_ai_project.ui.lobby.LobbyScreen
import com.example.rogue_ai_project.ui.lobby.LobbyViewModel
import com.example.rogue_ai_project.ui.lobby.LobbyViewModelFactory
import com.example.rogue_ai_project.ui.theme.RogueAITheme
import com.example.rogue_ai_project.util.SfxManager

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RogueAITheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    RogueAIApp()
                }
            }
        }
    }
}

sealed class Screen {
    object Home : Screen()
    data class Lobby(val roomCode: String) : Screen()
    data class Game(val roomCode: String) : Screen()
    data class GameOver(val victory: Boolean) : Screen()
}

@Composable
fun RogueAIApp() {
    val context = LocalContext.current
    var screenKey by remember { mutableIntStateOf(0) }
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Home) }

    // 1. Créez et souvenez-vous du GameRepository
    val gameRepository = remember { GameRepository() } // CRÉATION DE LA DÉPENDANCE MANQUANTE

    val roomSocket = remember(screenKey) { RoomSocket() }
    val sfxManager = remember(screenKey) { SfxManager(context) }

    DisposableEffect(screenKey) {
        onDispose {
            roomSocket.resetAll()
            sfxManager.release()
        }
    }

    when (val screen = currentScreen) {
        is Screen.Home -> {
            key(screenKey) {
                roomSocket.resetAll()

                // Fournir une clé unique pour forcer la recréation du ViewModel quand screenKey change
                val homeViewModel: HomeViewModel = viewModel(
                    key = "home-$screenKey",
                    factory = HomeViewModelFactory(
                        sfxManager = sfxManager,
                        gameRepository = gameRepository // AJOUT DU PARAMÈTRE MANQUANT
                    )
                )
                HomeScreen(
                    viewModel = homeViewModel,
                    onNavigateToLobby = { roomCode ->
                        currentScreen = Screen.Lobby(roomCode)
                    }
                )
            }
        }

        is Screen.Lobby -> {
            key(screenKey) {
                // Utiliser une clé unique qui inclut roomCode et screenKey
                val lobbyViewModel: LobbyViewModel = viewModel(
                    key = "lobby-${screen.roomCode}-$screenKey",
                    factory = LobbyViewModelFactory(screen.roomCode, roomSocket)
                )
                LobbyScreen(
                    viewModel = lobbyViewModel,
                    roomCode = screen.roomCode,
                    onGameStart = {
                        currentScreen = Screen.Game(screen.roomCode)
                    },
                    onBackToHome = {
                        screenKey++
                        currentScreen = Screen.Home
                    }
                )
            }
        }

        is Screen.Game -> {
            key(screenKey) {
                val gameViewModel: GameViewModel = viewModel(
                    key = "game-${screen.roomCode}-$screenKey",
                    factory = GameViewModelFactory(roomSocket, sfxManager)
                )
                GameScreen(
                    viewModel = gameViewModel,
                    onGameOver = { victory ->
                        currentScreen = Screen.GameOver(victory)
                    }
                )
            }
        }

        is Screen.GameOver -> {
            GameOverScreen(
                victory = screen.victory,
                onBackToHome = {
                    screenKey++
                    currentScreen = Screen.Home
                }
            )
        }
    }
}