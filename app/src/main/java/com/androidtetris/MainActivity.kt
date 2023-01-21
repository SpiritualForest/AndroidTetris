package com.androidtetris

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.androidtetris.ui.screens.HomeScreen
import com.androidtetris.ui.screens.tetris.TetrisScreen
import com.androidtetris.ui.theme.AndroidTetrisTheme

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // TODO: handle the savedInstanceState here
        setContent {
            AndroidTetrisTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = NavDestination.Home.route) {
                    composable(route = NavDestination.Home.route) { HomeScreen(navController = navController) }
                    composable(route = NavDestination.Tetris.route) { TetrisScreen() }
                }
                navController.navigate(NavDestination.Home.route)
            }
        }
    }
}

enum class NavDestination(val route: String) {
    Home("home"),
    Tetris("tetris")
}