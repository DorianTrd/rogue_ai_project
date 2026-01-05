package com.example.rogue_ai_project.ui.lobby

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.rogue_ai_project.model.LobbyPlayer

/**
 * Lobby screen composable.
 *
 * Displays:
 * - Room code and player list
 * - Player ready status
 * - Buttons to change name, toggle ready, and go back
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LobbyScreen(
    viewModel: LobbyViewModel,
    roomCode: String,
    onGameStart: () -> Unit,
    onBackToHome: () -> Unit
) {
    val players by viewModel.players.collectAsStateWithLifecycle()
    val isReady by viewModel.isReady.collectAsStateWithLifecycle()
    val gameStarted by viewModel.gameStarted.collectAsStateWithLifecycle()

    LaunchedEffect(gameStarted) {
        if (gameStarted) {
            onGameStart()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Salle : $roomCode") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "En attente des joueurs",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "${players.size} / 6 joueurs",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    if (players.size < 2) {
                        Text(
                            text = "Minimum 2 joueurs requis",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Joueurs",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(players) { player ->
                    PlayerCard(player = player)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = { viewModel.refreshName() },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Changer nom")
                }

                Button(
                    onClick = { viewModel.toggleReady() },
                    modifier = Modifier.weight(1f),
                    colors = if (isReady) {
                        ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondary
                        )
                    } else {
                        ButtonDefaults.buttonColors()
                    }
                ) {
                    Text(if (isReady) "Prêt ✓" else "Pas prêt")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Bouton de retour au menu
            OutlinedButton(
                onClick = { onBackToHome() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Retour au menu")
            }
        }
    }
}

/**
 * Composable displaying a single player's info in the lobby.
 *
 * - Highlights if the player is ready
 * - Shows host badge if player is host
 */
@Composable
fun PlayerCard(player: LobbyPlayer) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = if (player.isReady) {
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            )
        } else {
            CardDefaults.cardColors()
        }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = player.displayName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                if (player.isHost) {
                    Text(
                        text = "Hôte",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            if (player.isReady) {
                Text(
                    text = "✓ Prêt",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            } else {
                Text(
                    text = "En attente...",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
