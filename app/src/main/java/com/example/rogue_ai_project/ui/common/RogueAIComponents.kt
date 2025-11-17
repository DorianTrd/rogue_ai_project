package com.example.rogue_ai_project.ui.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.rogue_ai_project.ui.theme.RogueAIColors

@Composable
fun RogueAIBackground(
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        RogueAIColors.DarkBackground,
                        RogueAIColors.DarkSurface
                    )
                )
            )
    ) {
        content()
    }
}

@Composable
fun RogueAICard(
    modifier: Modifier = Modifier,
    glowing: Boolean = false,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = RogueAIColors.CardBackground
        ),
        border = BorderStroke(
            if (glowing) 2.dp else 1.dp,
            if (glowing) RogueAIColors.CyanNeon else RogueAIColors.CardBorder
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            content = content
        )
    }
}