package com.example.rogue_ai_project.ui.home

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rogue_ai_project.R
import com.example.rogue_ai_project.ui.common.RogueAIBackground
import com.example.rogue_ai_project.ui.theme.RogueAIColors
import kotlinx.coroutines.delay
import androidx.compose.ui.graphics.graphicsLayer
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToLobby: (String) -> Unit
) {
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
        Box(modifier = Modifier.fillMaxSize().statusBarsPadding(), contentAlignment = Alignment.TopCenter) {
            var showRobot by remember { mutableStateOf(false) }


            LaunchedEffect(Unit) {
                delay(3000L)
                showRobot = true
            }

            val infinite = rememberInfiniteTransition()
            val floatOffset by infinite.animateFloat(
                initialValue = 0f,
                targetValue = 18f,
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = 1400, easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse
                )
            )

            val alpha by animateFloatAsState(targetValue = if (showRobot) 0.9f else 0f, animationSpec = tween(500))

            Image(
                painter = painterResource(id = R.drawable.robot),
                contentDescription = "Robot background",
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = floatOffset.dp)
                    .graphicsLayer { this.alpha = alpha }
                    .align(Alignment.TopCenter),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                  Box(contentAlignment = Alignment.Center) {
                    val outlineOffset = 3.dp

                       Text(
                        text = "ROGUE AI",
                        style = MaterialTheme.typography.displayLarge,
                        fontWeight = FontWeight.Black,
                        color = Color.Black,
                        textAlign = TextAlign.Center,
                        lineHeight = 64.sp,
                        modifier = Modifier.offset(x = outlineOffset, y = outlineOffset)
                    )
                    Text(
                        text = "ROGUE AI",
                        style = MaterialTheme.typography.displayLarge,
                        fontWeight = FontWeight.Black,
                        color = Color.Black,
                        textAlign = TextAlign.Center,
                        lineHeight = 64.sp,
                        modifier = Modifier.offset(x = -outlineOffset, y = outlineOffset)
                    )
                    Text(
                        text = "ROGUE AI",
                        style = MaterialTheme.typography.displayLarge,
                        fontWeight = FontWeight.Black,
                        color = Color.Black,
                        textAlign = TextAlign.Center,
                        lineHeight = 64.sp,
                        modifier = Modifier.offset(x = outlineOffset, y = -outlineOffset)
                    )
                    Text(
                        text = "ROGUE AI",
                        style = MaterialTheme.typography.displayLarge,
                        fontWeight = FontWeight.Black,
                        color = Color.Black,
                        textAlign = TextAlign.Center,
                        lineHeight = 64.sp,
                        modifier = Modifier.offset(x = -outlineOffset, y = -outlineOffset)
                    )
                    Text(
                        text = "ROGUE AI",
                        style = MaterialTheme.typography.displayLarge,
                        fontWeight = FontWeight.Black,
                        color = Color.Black,
                        textAlign = TextAlign.Center,
                        lineHeight = 64.sp,
                        modifier = Modifier.offset(x = 0.dp, y = outlineOffset)
                    )
                    Text(
                        text = "ROGUE AI",
                        style = MaterialTheme.typography.displayLarge,
                        fontWeight = FontWeight.Black,
                        color = Color.Black,
                        textAlign = TextAlign.Center,
                        lineHeight = 64.sp,
                        modifier = Modifier.offset(x = 0.dp, y = -outlineOffset)
                    )
                    Text(
                        text = "ROGUE AI",
                        style = MaterialTheme.typography.displayLarge,
                        fontWeight = FontWeight.Black,
                        color = Color.Black,
                        textAlign = TextAlign.Center,
                        lineHeight = 64.sp,
                        modifier = Modifier.offset(x = outlineOffset, y = 0.dp)
                    )
                    Text(
                        text = "ROGUE AI",
                        style = MaterialTheme.typography.displayLarge,
                        fontWeight = FontWeight.Black,
                        color = Color.Black,
                        textAlign = TextAlign.Center,
                        lineHeight = 64.sp,
                        modifier = Modifier.offset(x = -outlineOffset, y = 0.dp)
                    )

                     Text(
                        text = "ROGUE AI",
                        style = MaterialTheme.typography.displayLarge,
                        fontWeight = FontWeight.Black,
                        color = RogueAIColors.CyanNeon,
                        textAlign = TextAlign.Center,
                        lineHeight = 64.sp
                    )
                }

                Spacer(modifier = Modifier.height(50.dp))

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