package com.androidtetris.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.androidtetris.NavDestination
import com.androidtetris.R
import com.androidtetris.game.Point
import com.androidtetris.settings.SettingsHandler
import com.androidtetris.ui.components.DropdownMenuSurface
import com.androidtetris.ui.components.TetrisDropdownMenuItem
import com.androidtetris.ui.components.TetrisDropdownMenuItemData
import com.androidtetris.ui.components.TetrisText
import com.androidtetris.ui.theme.LocalColors

/* AndroidTetris application entry point screen */

// Maybe use Surface here for each option?

@Composable
fun HomeScreen(navController: NavController) {
    Column(
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                navController.navigate(
                    route = NavDestination.Tetris.route,
                ) {
                    launchSingleTop = true
                }
            }
        ) {
            Text("Start Game")
        }
        Divider(modifier = Modifier.padding(vertical = 16.dp))
        Surface(
            shape = RoundedCornerShape(10.dp),
            tonalElevation = 8.dp,
            shadowElevation = 8.dp,
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                var invertRotation by remember { mutableStateOf(SettingsHandler.getInvertRotation()) }
                Checkbox(
                    checked = invertRotation,
                    onCheckedChange = { checked ->
                        SettingsHandler.setInvertRotation(checked)
                        invertRotation = checked
                    }
                )
                TetrisText(text = stringResource(id = R.string.invert_rotation))
            }
        }
        // Grid size menu
        val gridWidth = SettingsHandler.getGridWidth()
        val gridHeight = SettingsHandler.getGridHeight()
        var gridSizeMenuExpanded by remember { mutableStateOf(false) }
        DropdownMenuSurface(
            title = stringResource(id = R.string.txt_gridSize),
            selectionText = "${gridWidth}x${gridHeight}",
            modifier = Modifier.padding(vertical = 8.dp),
            menuExpanded = gridSizeMenuExpanded,
            onMenuClick = { gridSizeMenuExpanded = !gridSizeMenuExpanded }
        ) {
            DropdownMenu(
                expanded = gridSizeMenuExpanded,
                onDismissRequest = { gridSizeMenuExpanded = false }
            ) {
                val gridSizes = listOf(
                    Point(10, 22), // Default
                    Point(15, 33),
                    Point(20, 44),
                    Point(30, 66)
                )
                gridSizes.forEach {
                    val width = it.x
                    val height = it.y
                    val selected = width == gridWidth && height == gridHeight
                    TetrisDropdownMenuItem(
                        item = TetrisDropdownMenuItemData(
                            title = "${width}x${height}",
                            selected = selected,
                            onClick = {
                                SettingsHandler.setGridWidth(width)
                                SettingsHandler.setGridHeight(height)
                                gridSizeMenuExpanded = false
                            }
                        )
                    )
                }
            }
        }
        // Starting height setting menu
        var startingHeightMenuExpanded by remember { mutableStateOf(false) }
        val startingHeight = SettingsHandler.getStartingHeight()
        DropdownMenuSurface(
            title = stringResource(id = R.string.txt_startingHeight),
            selectionText = startingHeight.toString(),
            modifier = Modifier.padding(vertical = 8.dp),
            menuExpanded = startingHeightMenuExpanded,
            onMenuClick = { startingHeightMenuExpanded = !startingHeightMenuExpanded }
        ) {
            DropdownMenu(
                expanded = startingHeightMenuExpanded,
                onDismissRequest = { startingHeightMenuExpanded = false }
            ) {
                for (i in 0 until 9) {
                    TetrisDropdownMenuItem(
                        item = TetrisDropdownMenuItemData(
                            title = i.toString(),
                            selected = i == startingHeight,
                            onClick = {
                                SettingsHandler.setStartingHeight(i)
                                startingHeightMenuExpanded = false
                            }
                        )
                    )
                }
            }
        }
        // Game level setting menu
        var gameLevelMenuExpanded by remember { mutableStateOf(false) }
        val gameLevel = SettingsHandler.getGameLevel()
        DropdownMenuSurface(
            title = stringResource(id = R.string.txt_gameLevel),
            selectionText = gameLevel.toString(),
            modifier = Modifier.padding(vertical = 8.dp),
            menuExpanded = gameLevelMenuExpanded,
            onMenuClick = { gameLevelMenuExpanded = !gameLevelMenuExpanded }
        ) {
            DropdownMenu(
                expanded = gameLevelMenuExpanded,
                onDismissRequest = { gameLevelMenuExpanded = false }
            ) {
                for (i in 1 until 19) {
                    TetrisDropdownMenuItem(
                        item = TetrisDropdownMenuItemData(
                            title = i.toString(),
                            selected = i == gameLevel,
                            onClick = {
                                SettingsHandler.setGameLevel(i)
                                gameLevelMenuExpanded = false
                            }
                        )
                    )
                }
            }
        }
    }
}