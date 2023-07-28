package com.androidtetris.ui.screens.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.androidtetris.ui.components.TetrisDropdownMenuItemData

data class HomeScreenUiState(
    val startingHeightMenuItems: List<TetrisDropdownMenuItemData> = listOf(),
    val gameLevelMenuItems: List<TetrisDropdownMenuItemData> = listOf(),
    val gridSizeMenuItems: List<TetrisDropdownMenuItemData> = listOf()
)

class HomeScreenViewModel : ViewModel() {
    var uiState by mutableStateOf(HomeScreenUiState())
        private set
}