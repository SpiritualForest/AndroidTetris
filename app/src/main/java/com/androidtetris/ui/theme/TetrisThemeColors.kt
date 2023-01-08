package com.androidtetris.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

interface ThemeColors {
    val BorderColor: Color
    val BackgroundColor: Color
    val ForegroundColor: Color
}

object DarkColors : ThemeColors {
    override val BorderColor = Color(0xFFF9F9F9)
    override val BackgroundColor = Color(0xFF191919)
    override val ForegroundColor = Color(0xFFF9F9F9) // Used for Text, icon tint, etc.
}

object LightColors : ThemeColors {
    override val BorderColor = Color.Black
    override val BackgroundColor = Color(0xFFF9F9F9)
    override val ForegroundColor = Color.Black
}

data class TetrisTheme(
    val colors: ThemeColors,
    val isDark: Boolean
)

val LocalColors = staticCompositionLocalOf<TetrisTheme> {
    error("Test")
}