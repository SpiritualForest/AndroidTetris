package com.androidtetris

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.androidtetris.settings.SettingsHandler
import com.androidtetris.ui.screens.HomeScreen
import com.androidtetris.ui.screens.tetris.TetrisScreen
import com.androidtetris.ui.theme.AndroidTetrisTheme
import com.androidtetris.ui.theme.DarkColors
import com.androidtetris.ui.theme.LightColors
import com.androidtetris.ui.theme.LocalColors
import com.androidtetris.ui.theme.TetrisTheme

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // TODO: handle the savedInstanceState here
        SettingsHandler.openSharedPreferences(this)
        setContent {
            AndroidTetrisTheme {
                val themeColors = if (isSystemInDarkTheme()) DarkColors else LightColors
                val theme = TetrisTheme(
                    colors = themeColors,
                    isDark = isSystemInDarkTheme()
                )
                CompositionLocalProvider(LocalColors provides theme) {
                    val navController = rememberNavController()
                    NavHost(
                        navController = navController,
                        startDestination = NavDestination.Home.route
                    ) {
                        composable(route = NavDestination.Home.route) { HomeScreen(navController = navController) }
                        composable(route = NavDestination.Tetris.route) { TetrisScreen() }
                    }
                    navController.navigate(NavDestination.Home.route) {
                        launchSingleTop = true
                    }
                }
            }
        }
    }
}

enum class NavDestination(val route: String) {
    Home("home"),
    Tetris("tetris")
}