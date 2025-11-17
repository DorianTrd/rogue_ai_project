package com.example.rogue_ai_project.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Couleurs du jeu
object RogueAIColors {
    val DarkBackground = Color(0xFF1A1A2E)
    val DarkSurface = Color(0xFF16213E)
    val CardBackground = Color(0xFF1E1E2E)
    val CardBorder = Color(0xFF2A2A3E)

    val CyanNeon = Color(0xFF00D9FF)
    val GreenNeon = Color(0xFF00FF88)
    val OrangeNeon = Color(0xFFFFAA00)
    val RedDanger = Color(0xFFFF3333)
    val PurpleNeon = Color(0xFFAA00FF)

    val TextPrimary = Color.White
    val TextSecondary = Color(0xFFB8B8D1)
    val TextDisabled = Color(0xFF666677)
}

private val DarkColorScheme = darkColorScheme(
    primary = RogueAIColors.CyanNeon,
    secondary = RogueAIColors.GreenNeon,
    tertiary = RogueAIColors.OrangeNeon,
    error = RogueAIColors.RedDanger,
    background = RogueAIColors.DarkBackground,
    surface = RogueAIColors.DarkSurface,
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onBackground = RogueAIColors.TextPrimary,
    onSurface = RogueAIColors.TextPrimary
)

@Composable
fun RogueAITheme(
    darkTheme: Boolean = true, // Toujours dark
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography(),
        content = content
    )
}