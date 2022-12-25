package com.androidtetris.ui.components

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.androidtetris.game.Point
import com.androidtetris.game.TetrominoCode

// The sole purpose of this composable is to display tetrominoes

@Composable
fun TetrisGrid(
    width: Dp,
    height: Dp,
    modifier: Modifier = Modifier,
    grid: Map<Int, Map<Int, TetrominoCode>> = emptyMap(),
    tetrominoCoordinates: Array<Point> = arrayOf(),
    tetromino: TetrominoCode = TetrominoCode.I,
    ghostEnabled: Boolean = false
) {
    val gridWidth = 10 // squares
    val borderColor: Color = if (isSystemInDarkTheme()) Color.White else Color.Black

    val widthInPx = LocalDensity.current.run { width.toPx() }

    Box(modifier = modifier
        .height(height)
        .width(width)
        .background(Color.Blue.copy(alpha = 0.3f))
    ) {
        Canvas(
            modifier = modifier
                .fillMaxSize()
                .background(Color.Red.copy(alpha = 0.3f))
                .border(BorderStroke(1.dp, borderColor))
        ) {
            // FIXME: height doesn't change after horizontal padding
            val squareSizePx = size.width / gridWidth
            grid.forEach { (y, subMap) ->
                subMap.forEach { (x, code) ->
                    val offset = Offset((x * squareSizePx) + 1, (y * squareSizePx) + 1)
                    drawRect(
                        color = getTetrominoColor(code),
                        topLeft = offset,
                        size = Size(squareSizePx - 1, squareSizePx - 1)
                    )
                }
            }
            // Now the Tetromino
            tetrominoCoordinates.forEach { point ->
                val offset = Offset((point.x * squareSizePx) + 1, (point.y * squareSizePx) + 1)
                drawRect(
                    color = getTetrominoColor(tetromino),
                    topLeft = offset,
                    size = Size(squareSizePx - 1, squareSizePx - 1)
                )
            }
        }
    }
}

// FIXME: named colours?
private fun getTetrominoColor(tetrominoCode: TetrominoCode): Color {
    return when (tetrominoCode) {
        TetrominoCode.I -> Color(0xFFFF14A3)
        TetrominoCode.O -> Color(0xFFFF141E)
        TetrominoCode.J -> Color(0xFFFF8D14)
        TetrominoCode.L -> Color(0xFFC400F0)
        TetrominoCode.S -> Color(0xFF00F0E8)
        TetrominoCode.T -> Color(0xFF00F039)
        TetrominoCode.Z -> Color(0xFFB4F202)
    }
}