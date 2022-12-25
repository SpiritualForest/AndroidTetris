package com.androidtetris.ui.screens

import android.util.Log
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Checkbox
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.androidtetris.TetrisScreenViewModel
import com.androidtetris.game.Direction
import com.androidtetris.ui.components.TetrisGrid

/* AndroidTetris TetrisScreen: the composable that actually displays the gameplay */

@Composable
fun TetrisScreen() {
    val viewModel by remember { mutableStateOf(TetrisScreenViewModel()) }
    val uiState = viewModel.uiState
    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier.weight(0.5f)
            ) {
                // Left side column, contains upcoming tetrominoes grid, stats, ghost chip
                TetrisGrid(
                    width = 120.dp,
                    height = 200.dp,
                    modifier = Modifier.padding(bottom = 32.dp)
                )
                StatsText("Lines:")
                StatsText("Score:")
                StatsText("Level:")
                StatsText("Time:")
                Row(modifier = Modifier.padding(top = 32.dp)) {
                    Checkbox(
                        checked = uiState.ghostEnabled,
                        onCheckedChange = { checked ->
                            Log.d("TetrisScreen", "checked is $checked")
                            viewModel.setGhostEnabled(checked)
                        },
                    )
                    Text(
                        text = "Ghost enabled",
                        color = if (isSystemInDarkTheme()) Color.White else Color.Black,
                    )
                }
            }
            Column(
                modifier = Modifier.weight(0.5f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Right side column, contains the tetris game grid
                TetrisGrid(
                    width = 200.dp,
                    height = 440.dp,
                    grid = uiState.grid,
                    tetrominoCoordinates = uiState.coordinates,
                    tetromino = uiState.tetromino,
                    ghostEnabled = uiState.ghostEnabled,
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 32.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Button(
                        onClick = { viewModel.api.rotate() }
                    ) {
                        Text("Rotate")
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = { viewModel.api.move(Direction.Left) }
                    ) {
                        Text("Left")
                    }
                    Button(
                        onClick = { viewModel.api.move(Direction.Right) }
                    ) {
                        Text("Right")
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Button(
                        onClick = { viewModel.api.move(Direction.Down) }
                    ) {
                        Text("Down")
                    }
                }
            }
        }
    }
}

@Composable
private fun StatsText(text: String) {
    Text(
        text = text,
        color = if (isSystemInDarkTheme()) Color.White else Color.Black,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}