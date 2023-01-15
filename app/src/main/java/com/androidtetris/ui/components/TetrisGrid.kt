package com.androidtetris.ui.components

import android.graphics.BitmapFactory
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.drawable.toDrawable
import com.androidtetris.R
import com.androidtetris.ui.screens.tetris.TetrisScreenViewModel
import com.androidtetris.game.TetrominoCode
import com.androidtetris.ui.theme.LocalColors

// The sole purpose of this composable is to display tetrominoes

@Composable
fun TetrisGrid(
    width: Dp,
    height: Dp,
    viewModel: TetrisScreenViewModel,
    modifier: Modifier = Modifier,
    gridWidth: Int = 10,
    gridHeight: Int = 22
) {
    // State handling
    remember { mutableStateOf(viewModel.tetrisGridState.recompositionCount) } // Used to trigger recomposition
    val gridState = viewModel.tetrisGridState
    val grid = gridState.grid
    val tetrominoCoordinates = gridState.tetrominoCoordinates
    val tetromino = gridState.tetromino
    val pauseImage = ContextCompat.getDrawable(LocalContext.current, R.drawable.pause_circle)?.toBitmap()!!.asImageBitmap()
    val colors = LocalColors.current.colors

    Canvas(
        modifier = modifier
            .height(height)
            .width(width)
            .border(BorderStroke(1.dp, LocalColors.current.colors.BorderColor))
    ) {
        if (viewModel.gameState.gamePaused) {
            val horizontalCenter = (size.width / 2) - (pauseImage.width / 2)
            val verticalCenter = (size.height / 2) - (pauseImage.height / 2)
            drawImage(
                image = pauseImage,
                topLeft = Offset(x = horizontalCenter, y = verticalCenter),
                colorFilter = ColorFilter.tint(colors.ForegroundColor)
            )
            return@Canvas
        }
        val squareWidthPx = size.width / gridWidth
        val squareHeightPx = size.height / gridHeight
        grid.forEach { (y, subMap) ->
            subMap.forEach { (x, tetrominoCode) ->
                drawSquare(
                    x = x.toFloat(),
                    y = y.toFloat(),
                    squareWidthPx = squareWidthPx,
                    squareHeightPx = squareHeightPx,
                    color = getTetrominoColor(tetrominoCode)
                )
            }
        }
        // Now the Tetromino
        tetrominoCoordinates.forEach { point ->
            drawSquare(
                x = point.x.toFloat(),
                y = point.y.toFloat(),
                squareWidthPx = squareWidthPx,
                squareHeightPx = squareHeightPx,
                color = getTetrominoColor(tetromino)
            )
        }
        // And ghost
        if (viewModel.ghostEnabled) {
            gridState.ghostCoordinates.forEach { point ->
                drawSquare(
                    x = point.x.toFloat(),
                    y = point.y.toFloat(),
                    squareWidthPx = squareWidthPx,
                    squareHeightPx = squareHeightPx,
                    color = getTetrominoColor(tetromino).copy(alpha = 0.3f)
                )
            }
        }
    }
}

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

private fun DrawScope.drawSquare(
    x: Float,
    y: Float,
    squareWidthPx: Float,
    squareHeightPx: Float,
    color: Color
) {
    val offset = Offset((x * squareWidthPx) + 1, (y * squareHeightPx) + 1)
    drawRect(
        color = color,
        topLeft = offset,
        size = Size(squareWidthPx - 1, squareHeightPx - 1)
    )
}