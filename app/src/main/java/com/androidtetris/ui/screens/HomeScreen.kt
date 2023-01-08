package com.androidtetris.ui.screens

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.androidtetris.ui.theme.DarkColors
import com.androidtetris.ui.theme.LightColors
import com.androidtetris.ui.theme.LocalColors
import com.androidtetris.ui.theme.TetrisTheme

/* AndroidTetris application entry point screen */

@Composable
fun HomeScreen() {
    Column(
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val themeColors = if (isSystemInDarkTheme()) DarkColors else LightColors
        val theme = TetrisTheme(
            colors = themeColors,
            isDark = isSystemInDarkTheme()
        )
        CompositionLocalProvider(LocalColors provides theme) {
            
        }
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = { /* Go to TetrisScreen */ }
        ) {
            Text("Start Game")
        }
    }
}