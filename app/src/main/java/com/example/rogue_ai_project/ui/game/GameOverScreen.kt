package com.example.rogue_ai_project.ui.game

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rogue_ai_project.ui.theme.RogueAIColors
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun GameOverScreen(
    victory: Boolean,
    onBackToHome: () -> Unit
) {
    var animationStarted by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        animationStarted = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = if (victory) {
                        listOf(Color(0xFF001a0d), Color(0xFF003320))
                    } else {
                        listOf(Color(0xFF1a0000), Color(0xFF330000))
                    }
                )
            )
    ) {
        if (victory) {
            VictoryAnimation(animationStarted)
        } else {
            DefeatAnimation(animationStarted)
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(100.dp)) // Marge pour éloigner du bord supérieur

            Text(
                text = if (victory) "VICTOIRE !" else "DÉFAITE",
                style = MaterialTheme.typography.displayLarge,
                fontWeight = FontWeight.Black,
                color = if (victory) RogueAIColors.GreenNeon else RogueAIColors.RedDanger,
                fontSize = 56.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = if (victory) {
                    "MISSION ACCOMPLIE\n\nVous avez empêché l'IA\nde dominer le monde !"
                } else {
                    "MISSION ÉCHOUÉE\n\nL'IA a pris le contrôle\nde la planète..."
                },
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                lineHeight = 32.sp
            )

            // ⭐️ Changement 3 : Utiliser un weight pour pousser le bouton en bas ⭐️
            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = onBackToHome,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (victory) {
                        RogueAIColors.GreenNeon
                    } else {
                        RogueAIColors.RedDanger
                    }
                )
            ) {
                Text(
                    "🏠 RETOUR AU MENU",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Black,
                    color = Color.Black
                )
            }
        }
    }
}

@Composable
fun VictoryAnimation(started: Boolean) {
    val infiniteTransition = rememberInfiniteTransition(label = "victory")

    // Étoiles qui scintillent
    val stars = remember {
        List(80) {
            Triple(
                (0..100).random() / 100f,
                (0..100).random() / 100f,
                (0..10).random() / 10f
            )
        }
    }

    val twinkle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "twinkle"
    )

    // Animation des personnages qui sautent
    val jumpOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -30f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = EaseInOutQuad),
            repeatMode = RepeatMode.Reverse
        ),
        label = "jump"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        // Étoiles scintillantes
        stars.forEach { (x, y, offset) ->
            val alpha = ((twinkle + offset) % 1f) * 0.8f
            val size = 2f + ((twinkle + offset) % 1f) * 4f
            drawCircle(
                color = RogueAIColors.GreenNeon.copy(alpha = alpha),
                radius = size,
                center = Offset(this.size.width * x, this.size.height * y)
            )
        }

        // Personnages détaillés qui célèbrent
        val centerX = size.width / 2
        val bottomY = size.height * 0.75f

        for (i in 0..4) {
            val personX = centerX + (i - 2) * 150f
            val personY = bottomY + jumpOffset * ((i % 2) * 0.5f + 0.5f)
            val scale = if (started) 1f else 0f

            // Corps (rectangle arrondi)
            drawRoundRect(
                color = when (i % 3) {
                    0 -> Color(0xFF00AAFF)
                    1 -> Color(0xFFFF6B9D)
                    else -> Color(0xFFFFAA00)
                },
                topLeft = Offset(personX - 25f * scale, personY + 30f * scale),
                size = androidx.compose.ui.geometry.Size(50f * scale, 80f * scale),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(15f * scale)
            )

            // Tête
            drawCircle(
                color = Color(0xFFFFDBB5),
                radius = 35f * scale,
                center = Offset(personX, personY)
            )

            // Yeux (grands et joyeux)
            drawCircle(
                color = Color.Black,
                radius = 5f * scale,
                center = Offset(personX - 12f * scale, personY - 5f * scale)
            )
            drawCircle(
                color = Color.Black,
                radius = 5f * scale,
                center = Offset(personX + 12f * scale, personY - 5f * scale)
            )

            // Sourire large
            val smilePath = Path().apply {
                moveTo(personX - 18f * scale, personY + 5f * scale)
                quadraticBezierTo(
                    personX, personY + 20f * scale,
                    personX + 18f * scale, personY + 5f * scale
                )
            }
            drawPath(
                path = smilePath,
                color = Color.Black,
                style = Stroke(width = 4f * scale)
            )

            // Bras levés (en V)
            // Bras gauche
            drawLine(
                color = Color(0xFFFFDBB5),
                start = Offset(personX - 20f * scale, personY + 40f * scale),
                end = Offset(personX - 50f * scale, personY - 10f * scale),
                strokeWidth = 12f * scale
            )
            // Main gauche
            drawCircle(
                color = Color(0xFFFFDBB5),
                radius = 10f * scale,
                center = Offset(personX - 50f * scale, personY - 10f * scale)
            )

            // Bras droit
            drawLine(
                color = Color(0xFFFFDBB5),
                start = Offset(personX + 20f * scale, personY + 40f * scale),
                end = Offset(personX + 50f * scale, personY - 10f * scale),
                strokeWidth = 12f * scale
            )
            // Main droite
            drawCircle(
                color = Color(0xFFFFDBB5),
                radius = 10f * scale,
                center = Offset(personX + 50f * scale, personY - 10f * scale)
            )

            // Jambes
            drawLine(
                color = when (i % 3) {
                    0 -> Color(0xFF0088CC)
                    1 -> Color(0xFFCC4477)
                    else -> Color(0xFFCC8800)
                },
                start = Offset(personX - 10f * scale, personY + 110f * scale),
                end = Offset(personX - 15f * scale, personY + 160f * scale),
                strokeWidth = 14f * scale
            )
            drawLine(
                color = when (i % 3) {
                    0 -> Color(0xFF0088CC)
                    1 -> Color(0xFFCC4477)
                    else -> Color(0xFFCC8800)
                },
                start = Offset(personX + 10f * scale, personY + 110f * scale),
                end = Offset(personX + 15f * scale, personY + 160f * scale),
                strokeWidth = 14f * scale
            )

            // Chaussures
            drawCircle(
                color = Color.Black,
                radius = 12f * scale,
                center = Offset(personX - 15f * scale, personY + 160f * scale)
            )
            drawCircle(
                color = Color.Black,
                radius = 12f * scale,
                center = Offset(personX + 15f * scale, personY + 160f * scale)
            )
        }

        // Confettis
        for (j in 0..30) {
            val confettiX = (size.width * ((j * 37) % 100) / 100f)
            val confettiY = (size.height * 0.3f) + ((twinkle + j * 0.1f) % 1f) * size.height * 0.5f
            drawRect(
                color = when (j % 4) {
                    0 -> RogueAIColors.CyanNeon
                    1 -> RogueAIColors.GreenNeon
                    2 -> RogueAIColors.OrangeNeon
                    else -> RogueAIColors.PurpleNeon
                },
                topLeft = Offset(confettiX, confettiY),
                size = androidx.compose.ui.geometry.Size(8f, 12f)
            )
        }
    }
}

@Composable
fun DefeatAnimation(started: Boolean) {
    val animatedProgress = remember { Animatable(0f) }

    LaunchedEffect(started) {
        if (started) {
            animatedProgress.animateTo(
                targetValue = 1f,
                animationSpec = tween(3000, easing = FastOutSlowInEasing)
            )
        }
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        val centerX = size.width / 2
        val startY = size.height * 0.1f
        val earthCenterY = size.height * 0.7f

        // Trajectoire de la bombe
        val progress = animatedProgress.value
        val bombPhase = (progress * 1.3f).coerceAtMost(1f)
        val bombY = startY + (earthCenterY - startY - 200f) * bombPhase
        val bombX = centerX + sin(bombPhase * 3.14f * 2) * 80f

        // TERRE DÉTAILLÉE (beaucoup plus grande)
        val earthRadius = 200f
        val earthY = earthCenterY

        // Terre - Océans (fond bleu)
        drawCircle(
            color = Color(0xFF1E88E5),
            radius = earthRadius,
            center = Offset(centerX, earthY)
        )

        // Continents (formes organiques)
        // Amérique
        val americaPath = Path().apply {
            moveTo(centerX - 50f, earthY - 100f)
            cubicTo(
                centerX - 30f, earthY - 120f,
                centerX - 10f, earthY - 100f,
                centerX, earthY - 80f
            )
            cubicTo(
                centerX - 20f, earthY - 60f,
                centerX - 40f, earthY - 40f,
                centerX - 60f, earthY - 20f
            )
            cubicTo(
                centerX - 70f, earthY - 50f,
                centerX - 70f, earthY - 80f,
                centerX - 50f, earthY - 100f
            )
            close()
        }
        drawPath(americaPath, color = Color(0xFF4CAF50))

        // Europe/Afrique
        val africaPath = Path().apply {
            moveTo(centerX + 20f, earthY - 80f)
            cubicTo(
                centerX + 40f, earthY - 90f,
                centerX + 60f, earthY - 70f,
                centerX + 70f, earthY - 40f
            )
            cubicTo(
                centerX + 80f, earthY,
                centerX + 70f, earthY + 40f,
                centerX + 50f, earthY + 60f
            )
            cubicTo(
                centerX + 30f, earthY + 40f,
                centerX + 10f, earthY,
                centerX + 20f, earthY - 40f
            )
            close()
        }
        drawPath(africaPath, color = Color(0xFF66BB6A))

        // Asie
        val asiaPath = Path().apply {
            moveTo(centerX + 80f, earthY - 100f)
            cubicTo(
                centerX + 120f, earthY - 80f,
                centerX + 140f, earthY - 40f,
                centerX + 120f, earthY
            )
            cubicTo(
                centerX + 100f, earthY - 20f,
                centerX + 90f, earthY - 60f,
                centerX + 80f, earthY - 100f
            )
            close()
        }
        drawPath(asiaPath, color = Color(0xFF81C784))

        // Nuages blancs
        drawCircle(
            color = Color.White.copy(alpha = 0.4f),
            radius = 40f,
            center = Offset(centerX - 100f, earthY + 50f)
        )
        drawCircle(
            color = Color.White.copy(alpha = 0.3f),
            radius = 30f,
            center = Offset(centerX + 120f, earthY - 80f)
        )

        // Dessiner la bombe GROSSE et détaillée
        if (bombPhase < 0.92f) {
            val bombSize = 60f

            // Ombre de la bombe sur la terre
            val shadowAlpha = (1f - bombPhase) * 0.3f
            drawCircle(
                color = Color.Black.copy(alpha = shadowAlpha),
                radius = 80f + bombPhase * 100f,
                center = Offset(centerX, earthY - earthRadius)
            )

            // Corps principal de la bombe (métal noir)
            drawCircle(
                color = Color(0xFF1A1A1A),
                radius = bombSize,
                center = Offset(bombX, bombY)
            )

            // Reflet métallique
            drawCircle(
                color = Color(0xFF444444),
                radius = bombSize * 0.8f,
                center = Offset(bombX, bombY)
            )

            // Bande rouge danger
            drawRect(
                color = RogueAIColors.RedDanger,
                topLeft = Offset(bombX - bombSize, bombY - 10f),
                size = androidx.compose.ui.geometry.Size(bombSize * 2, 20f)
            )

            // Symbole radioactif
            val nuclearSymbol = Path().apply {
                addOval(
                    androidx.compose.ui.geometry.Rect(
                        bombX - 15f, bombY - 15f,
                        bombX + 15f, bombY + 15f
                    )
                )
            }
            drawPath(nuclearSymbol, color = Color.Yellow, style = Stroke(width = 3f))

            // Aileron stabilisateur
            val finPath = Path().apply {
                moveTo(bombX - bombSize * 0.7f, bombY + bombSize * 0.5f)
                lineTo(bombX - bombSize * 1.2f, bombY + bombSize * 1.2f)
                lineTo(bombX - bombSize * 0.4f, bombY + bombSize * 0.8f)
                close()
            }
            drawPath(finPath, color = Color(0xFF2A2A2A))

            // Mèche qui brûle
            val fuseLength = 100f
            val fusePath = Path().apply {
                moveTo(bombX, bombY - bombSize)
                cubicTo(
                    bombX - 20f, bombY - bombSize - 30f,
                    bombX - 30f, bombY - bombSize - 60f,
                    bombX - 15f, bombY - bombSize - fuseLength
                )
            }
            drawPath(
                path = fusePath,
                color = Color(0xFF5D4037),
                style = Stroke(width = 8f)
            )

            // Étincelles sur la mèche
            for (i in 0..5) {
                val sparkProgress = (bombPhase * 10f + i) % 1f
                val sparkY = bombY - bombSize - fuseLength * sparkProgress
                val sparkX = bombX - 15f + sin(sparkProgress * 3.14f) * 10f
                drawCircle(
                    color = RogueAIColors.OrangeNeon.copy(alpha = 1f - sparkProgress),
                    radius = 6f * (1f - sparkProgress),
                    center = Offset(sparkX, sparkY)
                )
            }

            // Flamme de la mèche (grosse et animée)
            val flameSize = 15f + sin(bombPhase * 20f) * 5f
            drawCircle(
                color = RogueAIColors.OrangeNeon,
                radius = flameSize,
                center = Offset(bombX - 15f, bombY - bombSize - fuseLength)
            )
            drawCircle(
                color = Color.Yellow,
                radius = flameSize * 0.6f,
                center = Offset(bombX - 15f, bombY - bombSize - fuseLength)
            )
        }

        // EXPLOSION MASSIVE avec destruction de la terre
        if (progress >= 0.92f) {
            val explosionProgress = (progress - 0.92f) / 0.08f
            val explosionRadius = 400f * explosionProgress

            // Onde de choc
            drawCircle(
                color = Color.White.copy(alpha = (1f - explosionProgress) * 0.8f),
                radius = explosionRadius * 1.2f,
                center = Offset(centerX, earthY - earthRadius)
            )

            // Explosion principale (plusieurs couches)
            drawCircle(
                color = RogueAIColors.OrangeNeon.copy(alpha = 1f - explosionProgress * 0.7f),
                radius = explosionRadius,
                center = Offset(centerX, earthY - earthRadius)
            )
            drawCircle(
                color = RogueAIColors.RedDanger.copy(alpha = 1f - explosionProgress * 0.7f),
                radius = explosionRadius * 0.75f,
                center = Offset(centerX, earthY - earthRadius)
            )
            drawCircle(
                color = Color.Yellow.copy(alpha = 1f - explosionProgress * 0.7f),
                radius = explosionRadius * 0.5f,
                center = Offset(centerX, earthY - earthRadius)
            )
            drawCircle(
                color = Color.White.copy(alpha = 1f - explosionProgress),
                radius = explosionRadius * 0.25f,
                center = Offset(centerX, earthY - earthRadius)
            )

            // Débris de la terre qui se dispersent
            for (i in 0..20) {
                val angle = (i * 18f) * (3.14f / 180f)
                val distance = explosionRadius * 0.8f
                val debrisX = centerX + cos(angle) * distance
                val debrisY = earthY - earthRadius + sin(angle) * distance
                val debrisSize = 20f - explosionProgress * 15f

                // Morceaux de terre verts
                if (i % 3 == 0) {
                    drawCircle(
                        color = Color(0xFF4CAF50).copy(alpha = 1f - explosionProgress),
                        radius = debrisSize,
                        center = Offset(debrisX, debrisY)
                    )
                }
                // Morceaux d'océan bleus
                else {
                    drawCircle(
                        color = Color(0xFF1E88E5).copy(alpha = 1f - explosionProgress),
                        radius = debrisSize,
                        center = Offset(debrisX, debrisY)
                    )
                }
            }

            // Fissures sur la terre restante
            if (explosionProgress < 0.5f) {
                for (i in 0..8) {
                    val crackAngle = (i * 45f) * (3.14f / 180f)
                    val crackPath = Path().apply {
                        moveTo(centerX, earthY)
                        lineTo(
                            centerX + cos(crackAngle) * earthRadius * (0.5f + explosionProgress),
                            earthY + sin(crackAngle) * earthRadius * (0.5f + explosionProgress)
                        )
                    }
                    drawPath(
                        crackPath,
                        color = RogueAIColors.RedDanger.copy(alpha = 0.8f),
                        style = Stroke(width = 8f)
                    )
                }
            }
        }
    }
}

