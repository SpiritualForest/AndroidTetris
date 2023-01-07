package com.androidtetris.ui.screens.tetris

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.androidtetris.R
import com.androidtetris.game.Direction
import com.androidtetris.ui.components.GameActionButton
import com.androidtetris.ui.components.TetrisGrid
import com.androidtetris.ui.components.TetrisText
import com.androidtetris.ui.components.UpcomingTetrominoesBox

/* AndroidTetris TetrisScreen: the composable that actually displays the gameplay */

@Composable
fun TetrisScreen(
    gridWidth: Int = 10,
    gridHeight: Int = 22
) {
    val viewModel by remember { mutableStateOf(TetrisScreenViewModel(gridWidth, gridHeight)) }
    val isGhostEnabled by remember { mutableStateOf(viewModel.ghostEnabled) }
    val backgroundColor = if (isSystemInDarkTheme()) Color.Black else Color.White
    Column(modifier = Modifier
            .background(backgroundColor)
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier.weight(0.3f)
            ) {
                // Left side column, contains upcoming tetrominoes grid, stats, ghost chip
                UpcomingTetrominoesBox(
                    width = 120.dp,
                    height = 200.dp,
                    viewModel = viewModel,
                    modifier = Modifier.padding(bottom = 32.dp)
                )
                Stats(viewModel)
                Row(
                    modifier = Modifier.padding(top = 32.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Switch(
                        checked = isGhostEnabled,
                        onCheckedChange = { viewModel.setGhostEnabled(it) },
                    )
                    Text(
                        text = "Ghost"
                    )
                }
            }
            Column(
                modifier = Modifier.weight(0.7f),
                horizontalAlignment = Alignment.End
            ) {
                // Right side column, contains the tetris game grid
                val fraction = 0.8f
                TetrisGrid(
                    width = 180.dp,
                    height = 396.dp,
                    viewModel = viewModel,
                    gridWidth = gridWidth,
                    gridHeight = gridHeight
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth(fraction = fraction)
                        .padding(top = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    GameActionButton(
                        drawable = R.drawable.arrow_up,
                        actionDelay = 100L,
                        onActionDown = { viewModel.rotate() }
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth(fraction = fraction),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    GameActionButton(
                        drawable = R.drawable.arrow_left,
                        onActionDown = { viewModel.move(Direction.Left) }
                    )
                    GameActionButton(
                        drawable = R.drawable.arrow_right,
                        onActionDown = { viewModel.move(Direction.Right) }
                    )
                }
                Box(
                    modifier = Modifier.fillMaxWidth(fraction = fraction),
                    contentAlignment = Alignment.Center
                ) {
                    GameActionButton(
                        drawable = R.drawable.arrow_down,
                        onActionDown = { viewModel.move(Direction.Down) }
                    )
                }
            }
        }
    }
}

@Composable
private fun Stats(
    viewModel: TetrisScreenViewModel,
    modifier: Modifier = Modifier
) {
    val gameStats by remember { derivedStateOf { viewModel.statsState } }
    Column(modifier = modifier) {
        TetrisText("Lines: ${gameStats.lines}")
        TetrisText("Score: ${gameStats.score}")
        TetrisText("Level: ${gameStats.level}")
    }
}