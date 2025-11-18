package com.example.rogue_ai_project.ui.game

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.rogue_ai_project.model.Command
import com.example.rogue_ai_project.model.Instruction
import androidx.compose.foundation.layout.statusBarsPadding

@Composable
fun GameScreen(
    viewModel: GameViewModel,
    onGameOver: (Boolean) -> Unit
) {
    val commands by viewModel.commands.collectAsStateWithLifecycle()
    val instruction by viewModel.instruction.collectAsStateWithLifecycle()
    val threat by viewModel.threat.collectAsStateWithLifecycle()
    val timeRemaining by viewModel.timeRemaining.collectAsStateWithLifecycle()
    val gameOver by viewModel.gameOver.collectAsStateWithLifecycle()
    val victory by viewModel.victory.collectAsStateWithLifecycle()

    LaunchedEffect(gameOver) {
        if (gameOver) {
            onGameOver(victory)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1A1A2E),
                        Color(0xFF16213E)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            GameHeader(
                threat = threat,
                timeRemaining = timeRemaining
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp)
            ) {
                instruction?.let { instr ->
                    InstructionCard(instruction = instr)
                    Spacer(modifier = Modifier.height(12.dp))
                }

                Text(
                    text = "⚡ PANNEAUX DE CONTRÔLE",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF00D9FF),
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(commands) { command ->
                        CommandCard(
                            command = command,
                            onAction = { action ->
                                viewModel.executeAction(command.id, action)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun GameHeader(threat: Int, timeRemaining: Long) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (threat >= 80) 1.1f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(500),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Surface(
        color = Color(0xFF0F1419),
        tonalElevation = 8.dp,
        shadowElevation = 12.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Threat
                Column {
                    Text(
                        text = "🚨 MENACE GLOBALE",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFFFF6B6B),
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "$threat%",
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.Black,
                        color = when {
                            threat >= 80 -> Color(0xFFFF3333)
                            threat >= 50 -> Color(0xFFFFAA00)
                            else -> Color(0xFF00FF88)
                        },
                        modifier = Modifier.scale(pulseScale)
                    )
                }

                // Timer
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "⏱ TEMPS RESTANT",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF00D9FF),
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${timeRemaining / 1000}s",
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.Black,
                        color = if (timeRemaining < 5000) Color(0xFFFF3333) else Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Progress bar avec gradient
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFF2A2A3E))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(threat / 100f)
                        .clip(RoundedCornerShape(6.dp))
                        .background(
                            Brush.horizontalGradient(
                                colors = when {
                                    threat >= 80 -> listOf(Color(0xFFFF3333), Color(0xFFFF0000))
                                    threat >= 50 -> listOf(Color(0xFFFFAA00), Color(0xFFFF6B00))
                                    else -> listOf(Color(0xFF00FF88), Color(0xFF00D9FF))
                                }
                            )
                        )
                )
            }
        }
    }
}

@Composable
fun InstructionCard(instruction: Instruction) {
    val infiniteTransition = rememberInfiniteTransition(label = "glow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1E1E2E)
        ),
        border = BorderStroke(
            2.dp,
            Color(0xFF00D9FF).copy(alpha = glowAlpha)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF00FF88))
                )
                Text(
                    text = "INSTRUCTION ACTIVE",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color(0xFF00D9FF),
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = instruction.instructionText,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                lineHeight = 28.sp
            )
        }
    }
}

@Composable
fun CommandCard(
    command: Command,
    onAction: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1E1E2E)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        border = BorderStroke(1.dp, Color(0xFF2A2A3E))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = command.name,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
                maxLines = 2,
                minLines = 2,
                color = Color(0xFFB8B8D1),
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                when (command.styleType) {
                    "toggle" -> {
                        ToggleControl(
                            actualStatus = command.actualStatus,
                            onToggle = { onAction("toggle") }
                        )
                    }
                    "lever_button" -> {
                        LeverControl(
                            actualStatus = command.actualStatus,
                            onToggle = { onAction("toggle") }
                        )
                    }
                    "onoff_button" -> {
                        OnOffButtonControl(
                            actualStatus = command.actualStatus,
                            onToggle = { onAction("toggle") }
                        )
                    }
                    "custom_button" -> {
                        CustomButtonControl(
                            actualStatus = command.actualStatus,
                            actionPossible = command.actionPossible,
                            onAction = { action -> onAction(action) }
                        )
                    }
                    "slider" -> {
                        SliderControl(
                            actualStatus = command.actualStatus,
                            actionPossible = command.actionPossible,
                            onValueChange = { value -> onAction(value) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ToggleControl(
    actualStatus: String,
    onToggle: () -> Unit
) {
    val isActive = actualStatus == "active"
    val rotation by animateFloatAsState(
        targetValue = if (isActive) 360f else 0f,
        animationSpec = tween(300),
        label = "rotation"
    )

    Button(
        onClick = onToggle,
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent
        ),
        contentPadding = PaddingValues(0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = if (isActive) {
                            listOf(Color(0xFF00FF88), Color(0xFF00CC6A))
                        } else {
                            listOf(Color(0xFF3A3A4A), Color(0xFF2A2A3A))
                        }
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
                .border(
                    2.dp,
                    if (isActive) Color(0xFF00FF88) else Color(0xFF4A4A5A),
                    RoundedCornerShape(12.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (isActive) "⚡" else "○",
                    style = MaterialTheme.typography.headlineLarge,
                    fontSize = 32.sp,
                    modifier = Modifier.rotate(rotation)
                )
                Text(
                    text = if (isActive) "ACTIF" else "INACTIF",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Black,
                    color = if (isActive) Color.Black else Color.White
                )
            }
        }
    }
}

@Composable
fun LeverControl(
    actualStatus: String,
    onToggle: () -> Unit
) {
    val isActive = actualStatus == "active"
    val offset by animateDpAsState(
        targetValue = if (isActive) (-8).dp else 8.dp,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "lever"
    )

    Button(
        onClick = onToggle,
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent
        ),
        contentPadding = PaddingValues(0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = if (isActive) {
                            listOf(Color(0xFFFF9500), Color(0xFFFF6B00))
                        } else {
                            listOf(Color(0xFF4A4A5A), Color(0xFF3A3A4A))
                        }
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
                .border(
                    2.dp,
                    if (isActive) Color(0xFFFFAA00) else Color(0xFF5A5A6A),
                    RoundedCornerShape(12.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.offset(y = offset)
            ) {
                Text(
                    text = if (isActive) "▲" else "▼",
                    style = MaterialTheme.typography.headlineLarge,
                    fontSize = 36.sp,
                    color = if (isActive) Color.Black else Color.White
                )
                Text(
                    text = if (isActive) "LEVÉ" else "BAISSÉ",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Black,
                    color = if (isActive) Color.Black else Color.White
                )
            }
        }
    }
}

@Composable
fun OnOffButtonControl(
    actualStatus: String,
    onToggle: () -> Unit
) {
    val isActive = actualStatus == "active"
    val scale by animateFloatAsState(
        targetValue = if (isActive) 1.05f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "scale"
    )

    Button(
        onClick = onToggle,
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
            .scale(scale),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent
        ),
        contentPadding = PaddingValues(0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        colors = if (isActive) {
                            listOf(Color(0xFF00D9FF), Color(0xFF0099CC))
                        } else {
                            listOf(Color(0xFF2A2A3E), Color(0xFF1A1A2E))
                        }
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
                .border(
                    3.dp,
                    if (isActive) Color(0xFF00FFFF) else Color(0xFF3A3A5A),
                    RoundedCornerShape(12.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (isActive) "■" else "□",
                    style = MaterialTheme.typography.headlineLarge,
                    fontSize = 32.sp,
                    color = if (isActive) Color.Black else Color(0xFF00D9FF)
                )
                Text(
                    text = if (isActive) "ON" else "OFF",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Black,
                    color = if (isActive) Color.Black else Color.White
                )
            }
        }
    }
}

@Composable
fun CustomButtonControl(
    actualStatus: String,
    actionPossible: List<String>,
    onAction: (String) -> Unit
) {
    if (actionPossible.size == 1 && actionPossible.first() == "toggle") {
        val isActive = actualStatus == "active"
        Button(
            onClick = { onAction("toggle") },
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent
            ),
            contentPadding = PaddingValues(0.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.sweepGradient(
                            colors = if (isActive) {
                                listOf(Color(0xFFAA00FF), Color(0xFF7700CC), Color(0xFFAA00FF))
                            } else {
                                listOf(Color(0xFF3A3A4A), Color(0xFF2A2A3A))
                            }
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .border(
                        2.dp,
                        if (isActive) Color(0xFFCC00FF) else Color(0xFF4A4A5A),
                        RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isActive) "✓" else "✗",
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Black,
                    color = if (isActive) Color.White else Color(0xFF666677)
                )
            }
        }
    } else {
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            actionPossible.forEach { action ->
                Button(
                    onClick = { onAction(action) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(28.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (actualStatus == action) {
                            Color(0xFFAA00FF)
                        } else {
                            Color(0xFF3A3A4A)
                        }
                    ),
                    contentPadding = PaddingValues(4.dp)
                ) {
                    Text(
                        text = action,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun SliderControl(
    actualStatus: String,
    actionPossible: List<String>,
    onValueChange: (String) -> Unit
) {
    val maxValue = actionPossible.maxOfOrNull { it.toIntOrNull() ?: 0 } ?: 10
    val currentValue = actualStatus.toIntOrNull() ?: 0
    val progress = currentValue.toFloat() / maxValue.toFloat()

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // Jauge visuelle cyberpunk
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFF1A1A2E))
                .border(2.dp, Color(0xFF00D9FF), RoundedCornerShape(8.dp))
        ) {
            // Remplissage de la jauge
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(progress)
                    .clip(RoundedCornerShape(6.dp))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFF00D9FF),
                                Color(0xFF00FF88),
                                Color(0xFFFFAA00)
                            )
                        )
                    )
            )

            // Valeur affichée
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = actualStatus,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Black,
                    color = if (progress > 0.3f) Color.Black else Color(0xFF00D9FF),
                    fontSize = 24.sp
                )
            }
        }

        // Boutons de contrôle en grille compacte
        if (actionPossible.size <= 5) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                actionPossible.forEach { value ->
                    SliderButton(
                        value = value,
                        isSelected = actualStatus == value,
                        onClick = { onValueChange(value) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        } else {
            Column(
                verticalArrangement = Arrangement.spacedBy(3.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    actionPossible.take(5).forEach { value ->
                        SliderButton(
                            value = value,
                            isSelected = actualStatus == value,
                            onClick = { onValueChange(value) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    actionPossible.drop(5).forEach { value ->
                        SliderButton(
                            value = value,
                            isSelected = actualStatus == value,
                            onClick = { onValueChange(value) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    val remaining = 5 - actionPossible.drop(5).size
                    repeat(remaining) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
fun SliderButton(
    value: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(26.dp),
        contentPadding = PaddingValues(2.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) {
                Color(0xFF00D9FF)
            } else {
                Color(0xFF2A2A3E)
            }
        ),
        border = if (isSelected) {
            BorderStroke(2.dp, Color(0xFF00FFFF))
        } else {
            BorderStroke(1.dp, Color(0xFF3A3A4A))
        },
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = if (isSelected) 6.dp else 2.dp
        ),
        shape = RoundedCornerShape(6.dp)
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
            fontSize = 11.sp,
            color = if (isSelected) Color.Black else Color(0xFF00D9FF)
        )
    }
}