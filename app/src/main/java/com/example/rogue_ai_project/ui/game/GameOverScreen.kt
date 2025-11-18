package com.example.rogue_ai_project.ui.game

import android.R.attr.centerX
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rogue_ai_project.R
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

    // ⭐️ NOUVELLE ANIMATION pour la chute des confettis (va de 0f à 1f et RESTART) ⭐️
    val confettiDropProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3500, easing = LinearEasing), // 3.5s pour la chute
            repeatMode = RepeatMode.Restart // IMPORTANT : recommence du début au lieu de faire l'aller-retour
        ),
        label = "confetti_drop"
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
            val dropProgress = (confettiDropProgress + j * 0.1f) % 1f
            val confettiY = dropProgress * size.height

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

    // ⚠️ Assurez-vous que R.drawable.terre est correct (la référence à votre PNG)
    val earthImage = painterResource(id = R.drawable.terre)

    // Taille de la planète en DP, utilisée pour l'Image et convertie en px pour le Canvas
    val earthRadius = 120.dp

    LaunchedEffect(started) {
        if (started) {
            animatedProgress.animateTo(
                targetValue = 1f,
                animationSpec = tween(3000, easing = FastOutSlowInEasing)
            )
        }
    }

    // Utilisation d'un Box pour superposer l'image PNG et le Canvas
    Box(modifier = Modifier.fillMaxSize()) {

        val progress = animatedProgress.value

        // Placement du PNG de la Terre
        if (progress < 0.92f) {
            // Positionnement Y fixe approximatif pour que le centre soit à 70% de la hauteur
            val earthCenterYOffsetDp = 560.dp

            Image(
                painter = earthImage,
                contentDescription = "La Terre",
                modifier = Modifier
                    .size(earthRadius * 2f) // La taille de l'image est 240dp (120dp * 2)
                    .align(Alignment.TopCenter)
                    // Positionnement vertical de l'image (Center Y - Radius)
                    .offset(y = earthCenterYOffsetDp - earthRadius)
                // ⭐️ SUPPRIMÉ : graphicsLayer pour l'opacité. L'image NE DISPARAÎT PLUS progressivement.
            )
        }

        Canvas(modifier = Modifier.fillMaxSize()) {
            // Ces variables DOIVENT être à l'intérieur du Canvas
            val centerX = size.width / 2
            val earthCanvasY = size.height * 0.7f // 0.7f de la hauteur du Canvas (en pixels)

            val startY = size.height * 0.1f
            // Conversion des dp en pixels pour le Canvas
            val earthCanvasRadius = earthRadius.toPx()

            // Trajectoire de la bombe
            val bombPhase = (progress * 1.3f).coerceAtMost(1f)
            // Calcul de Y de la bombe en utilisant earthCanvasY et earthCanvasRadius
            val bombY = startY + (earthCanvasY - startY - earthCanvasRadius) * bombPhase
            val bombX = centerX + sin(bombPhase * 3.14f * 2) * 80f

            // Dessiner la bombe GROSSE et détaillée
            if (bombPhase < 0.92f) {
                val bombSize = 60f

                // Ombre de la bombe sur la terre
                val shadowAlpha = (1f - bombPhase) * 0.3f
                drawCircle(
                    color = Color.Black.copy(alpha = shadowAlpha),
                    radius = 80f + bombPhase * 100f,
                    center = Offset(centerX, earthCanvasY - earthCanvasRadius)
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

                // ⭐️ MODIFICATION : Décalage vertical de l'épicentre (maintenant 80% du rayon)
                val explosionCenterY = earthCanvasY - earthCanvasRadius + (earthCanvasRadius * 0.8f)

                // Onde de choc
                drawCircle(
                    color = Color.White.copy(alpha = (1f - explosionProgress) * 0.8f),
                    radius = explosionRadius * 1.2f,
                    center = Offset(centerX, explosionCenterY)
                )

                // Explosion principale (plusieurs couches)
                drawCircle(
                    color = RogueAIColors.OrangeNeon.copy(alpha = 1f - explosionProgress),
                    radius = explosionRadius,
                    center = Offset(centerX, explosionCenterY)
                )
                drawCircle(
                    color = RogueAIColors.RedDanger.copy(alpha = 1f - explosionProgress),
                    radius = explosionRadius * 0.75f,
                    center = Offset(centerX, explosionCenterY)
                )
                drawCircle(
                    color = Color.Yellow.copy(alpha = 1f - explosionProgress),
                    radius = explosionRadius * 0.5f,
                    center = Offset(centerX, explosionCenterY)
                )
                drawCircle(
                    color = Color.White.copy(alpha = 1f - explosionProgress),
                    radius = explosionRadius * 0.25f,
                    center = Offset(centerX, explosionCenterY)
                )

                // Débris de la terre qui se dispersent
                for (i in 0..20) {
                    val angle = (i * 18f) * (3.14f / 180f)
                    val distance = explosionRadius * 0.8f
                    val debrisX = centerX + cos(angle) * distance
                    // Le point de départ des débris est l'explosionCenterY
                    val debrisY = explosionCenterY + sin(angle) * distance
                    val debrisSize = 20f - explosionProgress * 15f

                    // Débris gris/noir
                    drawCircle(
                        color = Color(0xFF424242).copy(alpha = 1f - explosionProgress),
                        radius = debrisSize,
                        center = Offset(debrisX, debrisY)
                    )
                }

                // Fissures sur la terre restante
                if (explosionProgress < 0.5f) {
                    for (i in 0..8) {
                        val crackAngle = (i * 45f) * (3.14f / 180f)
                        val crackPath = Path().apply {
                            moveTo(centerX, earthCanvasY)
                            lineTo(
                                centerX + cos(crackAngle) * earthCanvasRadius * (0.5f + explosionProgress),
                                earthCanvasY + sin(crackAngle) * earthCanvasRadius * (0.5f + explosionProgress)
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
}