package com.androidtetris.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// The sole purpose of this composable is to display tetrominoes

@Composable
fun TetrisGrid(width: Dp, height: Dp) {
    Canvas(modifier = Modifier
        .height(height)
        .width(width)
        .border(BorderStroke(1.dp, Color.Black)) // TODO: theming required here, respect system ui mode!
    ) {
    }
}