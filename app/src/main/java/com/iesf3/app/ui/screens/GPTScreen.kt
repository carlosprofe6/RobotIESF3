package com.iesf3.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.iesf3.app.ui.components.BackButton
import com.iesf3.app.ui.viewmodels.RobotViewModel
import androidx.compose.ui.text.input.ImeAction

@Composable
fun ChatGPTScreen(navController: NavController, robotViewModel: RobotViewModel) {
    var token by remember { mutableStateOf(robotViewModel.getToken()) }
    val speechText by robotViewModel.speechText.collectAsState()
    val messages by robotViewModel.messages.collectAsState()
    var lastSent by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current

    // Detectar cuando hay nueva voz y enviar a GPT si se activó "Hablar con Rafa"
    LaunchedEffect(speechText) {
        if (lastSent.isNotBlank() && speechText != lastSent) {
            robotViewModel.sendMessage(speechText)
            lastSent = ""
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        BackButton(navController)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {


            Text("Comandos de voz", style = MaterialTheme.typography.titleMedium)

            Button(
                onClick = {
                    robotViewModel.toggleListening()
                    lastSent = speechText
                }
            ) {
                Text("Hablar con Rafa")
            }

            if (speechText.isNotBlank()) {
                Text(
                    "Lo último que dijiste: \"$speechText\"",
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            // Mostrar respuesta de ChatGPT (última del assistant)
            val assistantResponse = messages.lastOrNull { it.first == "assistant" }?.second
            if (!assistantResponse.isNullOrBlank()) {
                Text(
                    text = "Respuesta de Rafa:\n$assistantResponse",
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
        }
    }
}
