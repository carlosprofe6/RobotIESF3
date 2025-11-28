package com.iesf3.app.ui.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.iesf3.app.R
import com.iesf3.app.ui.viewmodels.RobotViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun MainScreen(navController: NavController, robotViewModel: RobotViewModel) {
    val speechText by robotViewModel.speechText.collectAsState()
    val isListening by robotViewModel.isListening.observeAsState(false)
    val context = LocalContext.current

    // Scope para lanzar la rutina en segundo plano
    val coroutineScope = rememberCoroutineScope()

    // Estado para controlar si el botón está activo (para evitar dobles clicks)
    var isRoutineRunning by remember { mutableStateOf(false) }

    // --- LÓGICA DE VOZ ---
    LaunchedEffect(key1 = speechText) {
        if (speechText.contains("oye paco", ignoreCase = true) ||
            speechText.contains("彼得", ignoreCase = true) ||
            speechText.contains("你得", ignoreCase = true)) {
            val prompt = speechText.replace("oye paco", "", ignoreCase = true)
            Log.d("LaunchedEffect", "COMANDO DE VOZ DETECTADO")
            robotViewModel.sendMessage(prompt)
        }
    }

    LaunchedEffect(Unit) { robotViewModel.startListening() }

    Box(modifier = Modifier
        .fillMaxSize()
        .padding(24.dp)
    ) {
        // --- CABECERA: SWITCH ESCUCHAR ---
        Row(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .statusBarsPadding()
                .padding(top = 4.dp)
                .height(IntrinsicSize.Min),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Escuchar:", fontSize = 14.sp)
            Spacer(Modifier.width(8.dp))
            Switch(
                checked = isListening,
                onCheckedChange = { on ->
                    if (on) robotViewModel.startListening() else robotViewModel.stopListening()
                }
            )
        }

        // --- CONTENIDO PRINCIPAL: LOGO Y BOTÓN ---
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // 1. LOGOTIPO DEL INSTITUTO
            // Asegúrate de que existe R.drawable.logo_colegio
            Image(
                painter = painterResource(id = R.drawable.logo_ies),
                contentDescription = "Logo F3",
                modifier = Modifier
                    .size(250.dp)
                    .padding(bottom = 40.dp),
                contentScale = ContentScale.Fit
            )

            TitleComponent(title = "Bienvenido al IES Fernando III")

            Spacer(modifier = Modifier.height(32.dp))

            // 2. BOTÓN DE RUTINA (SALUDAR)
            Button(
                onClick = {
                    if (!isRoutineRunning) {
                        isRoutineRunning = true
                        coroutineScope.launch {
                            try {
                                // EJECUCIÓN DE LA RUTINA
                                performGreetingRoutine(robotViewModel)
                            } catch (e: Exception) {
                                Toast.makeText(context, "Error en rutina", Toast.LENGTH_SHORT).show()
                            } finally {
                                isRoutineRunning = false
                            }
                        }
                    }
                },
                enabled = !isRoutineRunning,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0057D9)),
                modifier = Modifier
                    .height(80.dp)
                    .width(260.dp)
            ) {
                Text(
                    text = if (isRoutineRunning) "Saludando..." else "Saludar y Moverse",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // --- PIE DE PÁGINA: TEXTO DEBUG ---
        Box(modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 16.dp)) {
            Text(
                text = "Listening: $speechText",
                fontSize = 12.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
        }
    }
}

// --- LÓGICA DE LA RUTINA (Suspend function) ---
// Usamos delays porque RobotViewModel no tiene callbacks de finalización
suspend fun performGreetingRoutine(viewModel: RobotViewModel) {
    // 1. Avanzar
    viewModel.irAdelante()
    delay(2000) // Avanza durante 2 segundos (aprox 0.5 - 0.6 metros)
    viewModel.parar()

    delay(500) // Pequeña pausa antes de hablar

    // 2. Hablar
    // Calculamos el delay según la longitud del texto o fijo si es siempre igual
    val textoSaludo = "¡Hola! Bienvenidos al Instituto Fernando III, centro de excelencia."
    viewModel.speak(textoSaludo)
    delay(6000) // Esperamos 6 segundos a que termine de hablar (estimación)

    // 3. Retroceder
    viewModel.irAtras()
    delay(2000) // Retrocede el mismo tiempo que avanzó
    viewModel.parar()
}

@Composable
fun TitleComponent(title: String) {
    Text(
        text = title,
        color = Color.Black,
        fontSize = 28.sp,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center
    )
}