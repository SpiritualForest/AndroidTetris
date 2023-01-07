package com.androidtetris.ui.components

import android.graphics.PointF
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.androidtetris.game.TetrominoCode
import com.androidtetris.ui.screens.tetris.TetrisScreenViewModel

// FIXME: TetrominoCanvas height keeps changing by one pixel on each recomposition. Find out why.

@Composable
fun UpcomingTetrominoesBox(
    width: Dp,
    height: Dp,
    viewModel: TetrisScreenViewModel,
    modifier: Modifier = Modifier
) {
    val upcomingState by remember { derivedStateOf { viewModel.upcomingTetrominoesState} }
    val tetrominoes = upcomingState.tetrominoes
    val borderColor = if (isSystemInDarkTheme()) Color.White else Color.Black
    Column(
        modifier = modifier
            .size(width = width, height = height)
            .border(BorderStroke(width = 1.dp, color = borderColor))
    ) {
        tetrominoes.forEach { tetromino ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
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
    val density = LocalDensity.current
    val squareSizePx = with(density) { 15.dp.toPx() }
    Canvas(modifier.fillMaxSize()) {
        calculateTetrominoCoordinates(size, density)
        val coordinates = TetrominoCoordinatesMap[tetromino]
        coordinates?.forEach {
            val offset = Offset(it.x + 1, it.y+ 1)
            drawRect(
                color = getTetrominoColor(tetrominoCode = tetromino),
                topLeft = offset,
                size = Size(squareSizePx - 1, squareSizePx - 1)
            )
        }
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

private var calculatedCoordinates = false
private var TetrominoCoordinatesMap: Map<TetrominoCode, List<PointF>> = mapOf()

private fun calculateTetrominoCoordinates(
    size: Size,
    density: Density,
    squareSizeDp: Dp = 15.dp
) {
    // Calculate the coordinates for all the tetrominoes at once
    // Only do this when the Canvas's size changes.
    if (calculatedCoordinates) {
        return
    }
    calculatedCoordinates = true
    val coordinatesMap: HashMap<TetrominoCode, List<PointF>> = hashMapOf()
    TetrominoCode.values().forEach {
        val shape = tetrominoShape(it)
        val squareSizePx = with(density) { squareSizeDp.toPx() }
        val horizontalCenter = (size.width / 2) - (squareSizePx * shape.first().size / 2)
        val verticalCenter = (size.height / 2) - (squareSizePx * shape.size / 2)
        var y = verticalCenter
        val coordinatesList: MutableList<PointF> = mutableListOf()
        shape.forEach { valueList ->
            var x = horizontalCenter
            valueList.forEach { value ->
                // A value is either 0 or 1.
                // 1 means add this position, 0 means skip.
                if (value == 1) {
                    // Add this
                    coordinatesList.add(PointF(x, y))
                }
                x += squareSizePx
            }
            y += squareSizePx
        }
        coordinatesMap[it] = coordinatesList
    }
    TetrominoCoordinatesMap = coordinatesMap.toMap()
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