package com.androidtetris.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

interface ThemeColors {
    val BorderColor: Color
    val BackgroundColor: Color
    val ForegroundColor: Color
    val SurfaceColor: Color
}

object DarkColors : ThemeColors {
    override val BorderColor = Color(0xFFF9F9F9)
    override val BackgroundColor = Color(0xFF191919)
    override val ForegroundColor = Color(0xFFF9F9F9) // Used for Text, icon tint, etc.
    override val SurfaceColor = Color.LightGray
}

object LightColors : ThemeColors {
    override val BorderColor = Color.Black
    override val BackgroundColor = Color(0xFFeeeeee) // Color(0xFFF9F9F9)
    override val ForegroundColor = Color.Black
    override val SurfaceColor = Color.LightGray
}

data class TetrisTheme(
    val colors: ThemeColors,
    val isDark: Boolean
)

val LocalColors = staticCompositionLocalOf {
    TetrisTheme(
        colors = LightColors,
        isDark = false
    )
}