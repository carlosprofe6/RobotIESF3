package com.iesf3.app.navigation

sealed class AppScreens (val route: String){
    object MainScreen : AppScreens("main_screen")
    object Movement : AppScreens("movement_screen")
    object MQTT : AppScreens("mqtt_screen")
    object ChatGPT : AppScreens("chatgpt_screen")
    object TTS : AppScreens("tts_screen")
    object TextChat : AppScreens("text_chat_screen")
    object Points : AppScreens("points_screen")
}