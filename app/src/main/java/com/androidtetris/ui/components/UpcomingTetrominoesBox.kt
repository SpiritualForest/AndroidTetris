package com.androidtetris.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import com.androidtetris.game.TetrominoCode

@Composable
fun UpcomingTetrominoesBox(
    width: Dp,
    height: Dp,
    tetrominoes: List<TetrominoCode>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.onSizeChanged { size -> calculateTetrominoCoordinates(size) }) {
        tetrominoes.forEach { tetromino ->
            Box(
                modifier = Modifier
                    .width(width)
                    .height(height)
            ) {
                TetrominoCanvas(tetromino = tetromino)
            }
        }
    }
}

@Composable
private fun TetrominoCanvas(
    tetromino: TetrominoCode,
    modifier: Modifier = Modifier
) {
    Canvas(modifier.fillMaxSize()) {
    }
}

private fun tetrominoShape(tetrominoCode: TetrominoCode) = when (tetrominoCode) {
    TetrominoCode.I -> listOf(listOf(1, 1, 1, 1))
    TetrominoCode.O -> listOf(listOf(1, 1), listOf(1, 1))
    TetrominoCode.J -> listOf(listOf(1, 0, 0), listOf(1, 1, 1))
    TetrominoCode.L -> listOf(listOf(0, 0, 1), listOf(1, 1, 1))
    TetrominoCode.S -> listOf(listOf(0, 1, 1), listOf(1, 1, 0))
    TetrominoCode.Z -> listOf(listOf(1, 1, 0), listOf(0, 1, 1))
    TetrominoCode.T -> listOf(listOf(1, 1, 1), listOf(0, 1, 0))
}

private var canvasSize: IntSize = IntSize.Zero

private fun calculateTetrominoCoordinates(size: IntSize) {
    if (size == canvasSize) {
        return
    }
    canvasSize = size
}