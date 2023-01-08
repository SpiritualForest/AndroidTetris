package com.androidtetris.ui.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.androidtetris.ui.theme.LocalColors

// Just a wrapper so we don't have to add the colour to every Text() we use anywhere

@Composable
fun TetrisText(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        color = LocalColors.current.colors.ForegroundColor,
        modifier = modifier
    )
}