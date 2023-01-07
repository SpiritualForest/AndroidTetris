package com.androidtetris.ui.components

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun TetrisText(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        color = if (isSystemInDarkTheme()) Color.White else Color.Black,
        modifier = modifier
    )
}