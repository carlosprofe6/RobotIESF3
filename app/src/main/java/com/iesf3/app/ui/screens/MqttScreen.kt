package com.iesf3.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.iesf3.app.ui.components.BackButton
import com.iesf3.app.ui.viewmodels.RobotViewModel
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction



@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun MQTTScreen(navController: NavController, robotViewModel: RobotViewModel) {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current

    var ip by remember { mutableStateOf(robotViewModel.getMqttIp()) }
    var user by remember { mutableStateOf(robotViewModel.getMqttUser()) }
    var password by remember { mutableStateOf(robotViewModel.getMqttPassword()) }

    Box(modifier = Modifier.fillMaxSize()) {
        BackButton(navController)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Configuración MQTT", style = MaterialTheme.typography.titleLarge)

            TextField(
                value = ip,
                onValueChange = { ip = it },
                label = { Text("IP del Broker") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = {
                    keyboardController?.hide()
                })
            )

            TextField(
                value = user,
                onValueChange = { user = it },
                label = { Text("Usuario") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = {
                    keyboardController?.hide()
                })
            )

            TextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = {
                    keyboardController?.hide()
                })
            )

            Button(
                onClick = {
                    keyboardController?.hide()
                    robotViewModel.setMqttIp(ip)
                    robotViewModel.setMqttUser(user)
                    robotViewModel.setMqttPassword(password)
                    robotViewModel.initMqttIfConfigured(context)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text("Guardar configuración")
            }
        }
    }
}

