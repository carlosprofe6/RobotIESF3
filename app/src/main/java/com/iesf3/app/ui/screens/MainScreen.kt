package com.iesf3.app.ui.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
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

    // Estado para controlar si la rutina está en ejecución
    var isRoutineRunning by remember { mutableStateOf(false) }


    Box(modifier = Modifier
        .fillMaxSize()
        .padding(4.dp) // Padding general de la pantalla
    ) {

        // --- CENTRO: LOGOTIPO Y TÍTULO ---
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(bottom = 30.dp), // Dejar espacio para la botonera inferior
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo_ies),
                contentDescription = "Logo Instituto",
                modifier = Modifier
                    .size(220.dp)
                    .padding(bottom = 10.dp),
                contentScale = ContentScale.FillBounds
            )
            TitleComponent(title = "IES Fernando III")
        }

        // --- PARTE INFERIOR: BOTONERA DE 3 BOTONES ---
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
        ) {
            // FILA DE BOTONES
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp) // Espacio entre botones
            ) {
                // BOTÓN 1: IR A CLASE
                MenuButton(
                    text = "Ir a clase",
                    onClick = {
                        // Asegúrate de que esta ruta coincida con la definida en tu NavHost
                        navController.navigate("clases_screen")
                    },
                    modifier = Modifier.weight(1f), // Ocupa 33%
                    color = Color(0xFF0057D9)
                )

                // BOTÓN 2: SALUDAR
                MenuButton(
                    text = if (isRoutineRunning) "..." else "Saludar",
                    onClick = {
                        if (!isRoutineRunning) {
                            isRoutineRunning = true
                            coroutineScope.launch {
                                try {
                                    performGreetingRoutine(robotViewModel)
                                } finally {
                                    isRoutineRunning = false
                                }
                            }
                        }
                    },
                    modifier = Modifier.weight(1f), // Ocupa 33%
                    color = if (isRoutineRunning) Color.Gray else Color(0xFF009688) // Verde azulado
                )

                // BOTÓN 3: COMANDOS DE VOZ
                MenuButton(
                    text = "Comandos\nde voz",
                    onClick = {
                        // Navegar a pantalla de chat o comandos
                        navController.navigate("chatgpt_screen")
                        // O mostrar mensaje si no hay pantalla aún:
                        //Toast.makeText(context, "Modo escucha activo", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.weight(1f), // Ocupa 33%
                    color = Color(0xFFFF9800) // Naranja
                )
            }
        }
    }
}

// --- COMPONENTE BOTÓN PERSONALIZADO ---
@Composable
fun MenuButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    color: Color = Color(0xFF0057D9)
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(70.dp), // Altura fija para uniformidad
        colors = ButtonDefaults.buttonColors(containerColor = color),
        shape = RoundedCornerShape(12.dp),
        contentPadding = PaddingValues(4.dp) // Padding interno reducido para que quepa texto
    ) {
        Text(
            text = text,
            fontSize = 13.sp, // Texto un poco más pequeño para que quepa
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            lineHeight = 16.sp
        )
    }
}

// --- LÓGICA DE LA RUTINA ---
suspend fun performGreetingRoutine(viewModel: RobotViewModel) {


    viewModel.irIzquierda()
    delay(500)
    viewModel.irDerecha()
    delay(500)

    viewModel.mirarArriba()
    delay(500)
    viewModel.mirarAbajo()

    delay(500)
    viewModel.speak("¡Hola! Bienvenidos al Instituto Fernando tercero, centro de excelencia.")
    delay(5000) // Tiempo estimado de habla

    viewModel.speak("Soy un asistente educativo entrenado para ayudarte")



}

@Composable
fun TitleComponent(title: String) {
    Text(
        text = title,
        color = Color.Black,
        fontSize = 26.sp,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center
    )
}