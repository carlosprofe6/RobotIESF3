package com.iesf3.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.iesf3.app.ui.screens.*
import com.iesf3.app.ui.viewmodels.RobotViewModel

@Composable
fun AppNavigation(robotViewModel: RobotViewModel) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = AppScreens.MainScreen.route
    ) {
        composable(AppScreens.MainScreen.route) {
            MainScreen(navController = navController, robotViewModel = robotViewModel)
        }

        composable(AppScreens.ChatGPT.route) {
            ChatGPTScreen(navController = navController, robotViewModel = robotViewModel)
        }

        composable(AppScreens.Clases.route) {
            ClasesScreen(navController = navController, robotViewModel = robotViewModel)
        }

    }
}
