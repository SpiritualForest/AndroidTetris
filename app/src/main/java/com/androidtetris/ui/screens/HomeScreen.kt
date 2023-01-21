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
import androidx.compose.material3.DropdownMenuItem
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
import com.androidtetris.settings.SettingsHandler
import com.androidtetris.ui.components.TetrisText

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
        Surface(
            shape = RoundedCornerShape(10.dp),
            tonalElevation = 8.dp,
            shadowElevation = 8.dp,
        ) {
            // Grid size menu
            Row(modifier = Modifier.fillMaxWidth()) {
                var menuExpanded by remember { mutableStateOf(true) }
                TetrisText(stringResource(id = R.string.txt_gridSize))
                DropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false }
                ) {
                    // 10 x 22
                    DropdownMenuItem(
                        text = { TetrisText("10x22") },
                        onClick = {
                            SettingsHandler.setGridWidth(10)
                            SettingsHandler.setGridHeight(22)
                        }
                    )
                    // 15 x 33
                    DropdownMenuItem(
                        text = { TetrisText("15x33") },
                        onClick = {
                            SettingsHandler.setGridWidth(15)
                            SettingsHandler.setGridHeight(33)
                        }
                    )
                    // 20 x 44
                    DropdownMenuItem(
                        text = { TetrisText("20x44") },
                        onClick = {
                            SettingsHandler.setGridWidth(20)
                            SettingsHandler.setGridHeight(44)
                        }
                    )
                    // 30 x 66
                    DropdownMenuItem(
                        text = { TetrisText("30x66") },
                        onClick = {
                            SettingsHandler.setGridWidth(30)
                            SettingsHandler.setGridHeight(66)
                        }
                    )
                }
            }
        }
    }
}