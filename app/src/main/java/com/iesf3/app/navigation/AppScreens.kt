package com.iesf3.app.navigation

sealed class AppScreens (val route: String){
    object MainScreen : AppScreens("main_screen")
    object ChatGPT : AppScreens("chatgpt_screen")
    object Clases : AppScreens("clases_screen")

}