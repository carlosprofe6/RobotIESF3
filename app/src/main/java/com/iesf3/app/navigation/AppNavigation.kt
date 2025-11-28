package com.iesf3.app.navigation

import TextChatScreen
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

        composable(AppScreens.Movement.route) {
            MovementScreen(navController = navController, robotViewModel = robotViewModel)
        }

        composable(AppScreens.MQTT.route) {
            MQTTScreen(navController = navController, robotViewModel = robotViewModel)
        }

        composable(AppScreens.ChatGPT.route) {
            ChatGPTScreen(navController = navController, robotViewModel = robotViewModel)
        }

        composable(AppScreens.TTS.route) {
            TTSScreen(navController = navController, robotViewModel = robotViewModel)
        }

        composable(AppScreens.TextChat.route) {
            TextChatScreen(navController = navController, robotViewModel = robotViewModel)
        }

        composable(AppScreens.Points.route) {
            PointsScreen(navController = navController, robotViewModel = robotViewModel)
        }
    }
}
