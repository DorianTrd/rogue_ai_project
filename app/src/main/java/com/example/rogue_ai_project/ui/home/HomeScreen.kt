package com.example.rogue_ai_project.ui.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.rogue_ai_project.ui.common.RogueAIBackground
import com.example.rogue_ai_project.ui.common.RogueAICard
import com.example.rogue_ai_project.ui.theme.RogueAIColors
import com.example.rogue_ai_project.util.SfxManager

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToLobby: (String) -> Unit
) {
    val roomCode by viewModel.roomCode.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()
    val navigationEvent by viewModel.navigationEvent.collectAsStateWithLifecycle()

    var showJoinDialog by remember { mutableStateOf(false) }

    LaunchedEffect(navigationEvent) {
        navigationEvent?.let { code ->
            onNavigateToLobby(code)
            viewModel.clearNavigation()

        }
    }

    RogueAIBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "ROGUE AI\nOVERRIDE",
                style = MaterialTheme.typography.displayLarge,
                fontWeight = FontWeight.Black,
                color = RogueAIColors.CyanNeon,
                textAlign = TextAlign.Center,
                lineHeight = 64.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Jouez ensemble. Survivez 45s.\nEmpêchez l'IA de dominer le monde.",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = RogueAIColors.TextSecondary
            )

            Spacer(modifier = Modifier.height(48.dp))

            Button(
                onClick = { viewModel.createRoom() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                enabled = !isLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = RogueAIColors.CyanNeon
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text(
                        "🎮 CRÉER UNE PARTIE",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = { showJoinDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                enabled = !isLoading,
                border = BorderStroke(2.dp, RogueAIColors.CyanNeon)
            ) {
                Text(
                    "🔗 REJOINDRE AVEC CODE",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = RogueAIColors.CyanNeon
                )
            }

            error?.let { err ->
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "⚠️ $err",
                    color = RogueAIColors.RedDanger,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }

    if (showJoinDialog) {
        JoinGameDialog(
            onDismiss = { showJoinDialog = false },
            onJoin = { code ->
                showJoinDialog = false
                viewModel.joinRoom(code)
            }
        )
    }
}

@Composable
fun JoinGameDialog(
    onDismiss: () -> Unit,
    onJoin: (String) -> Unit
) {
    var code by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = RogueAIColors.CardBackground,
        title = {
            Text(
                "Rejoindre une partie",
                color = RogueAIColors.CyanNeon,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {
                Text(
                    "Entrez le code de la partie (6 caractères)",
                    color = RogueAIColors.TextSecondary
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = code,
                    onValueChange = { code = it.take(6).uppercase() },
                    label = { Text("Code", color = RogueAIColors.TextSecondary) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = RogueAIColors.CyanNeon,
                        unfocusedBorderColor = RogueAIColors.CardBorder,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onJoin(code) },
                enabled = code.length == 6,
                colors = ButtonDefaults.buttonColors(
                    containerColor = RogueAIColors.CyanNeon
                )
            ) {
                Text("Rejoindre", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annuler", color = RogueAIColors.TextSecondary)
            }
        }
    )
}