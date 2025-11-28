package com.iesf3.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.iesf3.app.ui.components.BackButton
import com.iesf3.app.ui.viewmodels.RobotViewModel

@Composable
fun TTSScreen(navController: NavController, robotViewModel: RobotViewModel) {
    val focusManager = LocalFocusManager.current
    var text by remember { mutableStateOf("") }

    // Usamos Box para colocar el botón de volver arriba sin mover nada del contenido
    Box(modifier = Modifier.fillMaxSize()) {
        // Flechita para volver al menú principal
        BackButton(navController)

        // El contenido principal va dentro de esta columna
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Título de la pantalla
            Text("Texto a Voz (TTS)", style = MaterialTheme.typography.titleLarge)

            // Campo donde el usuario escribe el mensaje que el robot dirá en voz alta
            TextField(
                value = text,
                onValueChange = { text = it },
                label = { Text("Mensaje para el robot") },
                modifier = Modifier.fillMaxWidth()
            )

            // Botón que lanza la acción de hablar
            Button(
                onClick = {
                    robotViewModel.speak(text) // El robot habla
                    focusManager.clearFocus()  // Quitamos el teclado
                }
            ) {
                Text("PLAY")
            }
        }
    }
}
