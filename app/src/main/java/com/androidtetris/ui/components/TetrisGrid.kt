package com.androidtetris.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.height
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
    tetromino: TetrominoCode = TetrominoCode.I
) {
    val gridWidth = 10 // squares
    val squareSizeDp = width / gridWidth
    val squareSizePx = with(LocalDensity.current) { squareSizeDp.toPx() }
    val borderColor: Color = if (isSystemInDarkTheme()) Color.Black else Color.White
    Canvas(modifier = modifier
        .height(height)
        .width(width)
        .border(BorderStroke(1.dp, borderColor))
    ) {
        grid.forEach { (y, subMap) ->
            subMap.forEach { (x, code) ->
                val offset = Offset((x * squareSizePx) + 1, (y * squareSizePx) + 1)
                drawRect(
                    color = tetrominoColors[code]!!,
                    topLeft = offset,
                    size = Size(squareSizePx - 1, squareSizePx - 1)
                )
            }
        }
        // Now the Tetromino
        tetrominoCoordinates.forEach { point ->
            val offset = Offset((point.x * squareSizePx) + 1, (point.y * squareSizePx) + 1)
            drawRect(
                color = tetrominoColors[tetromino]!!,
                topLeft = offset,
                size = Size(squareSizePx-1, squareSizePx-1)
            )
        }
    }
}

// FIXME: named colours?
private val tetrominoColors: Map<TetrominoCode, Color> = mapOf(
    TetrominoCode.I to Color(0xFFFF14A3),
    TetrominoCode.O to Color(0xFFFF141E),
    TetrominoCode.J to Color(0xFFFF8D14),
    TetrominoCode.L to Color(0xFFC400F0),
    TetrominoCode.S to Color(0xFF00F0E8),
    TetrominoCode.T to Color(0xFF00F039),
    TetrominoCode.Z to Color(0xFFB4F202)
)