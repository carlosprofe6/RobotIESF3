package com.iesf3.app.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.iesf3.app.ui.viewmodels.RobotViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ClasesScreen(navController: NavController, robotViewModel: RobotViewModel) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // Estado para bloquear botones mientras el robot se mueve
    var isRobotMoving by remember { mutableStateOf(false) }

    // Listado de clases solicitado
    val listaClases = listOf(
        "1 ESO A", "1 ESO B", "1 ESO C",
        "2 ESO A", "2 ESO B", "2 ESO C",
        "3 ESO A", "3 ESO B", "3 ESO C"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Título
        Text(
            text = "Selecciona el Aula de Destino",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 24.dp, top = 16.dp),
            color = Color(0xFF333333)
        )

        // Parrilla de Botones (3 columnas)
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            contentPadding = PaddingValues(8.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(listaClases) { clase ->
                ClaseButton(
                    nombreClase = clase,
                    isEnabled = !isRobotMoving,
                    onClick = {
                        isRobotMoving = true
                        Toast.makeText(context, "Yendo a $clase...", Toast.LENGTH_SHORT).show()

                        coroutineScope.launch {
                            try {
                                // Llamamos a la función pasando el nombre de la clase
                                irClase(robotViewModel, clase)
                            } finally {
                                isRobotMoving = false
                            }
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun ClaseButton(nombreClase: String, isEnabled: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        enabled = isEnabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF0057D9), // Azul corporativo
            disabledContainerColor = Color.Gray
        ),
        modifier = Modifier
            .height(100.dp) // Botones altos para facilitar el toque
            .fillMaxWidth(),
        shape = MaterialTheme.shapes.medium
    ) {
        Text(
            text = nombreClase,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}

/**
 * Función suspendida para gestionar el movimiento a la clase.
 * He añadido el parámetro 'nombreClase' por si quieres usarlo en el futuro para loguear o hablar.
 */
suspend fun irClase(viewModel: RobotViewModel, nombreClase: String) {
    // 1. Mensaje inicial (Opcional)
    viewModel.speak("Iniciando ruta hacia $nombreClase")
    delay(3000)

    // 2. Secuencia de movimientos
    // NOTA: He comentado irDerecha e irIzquierda porque NO existen en tu RobotViewModel actual.
    // Si los implementas en el ViewModel, puedes descomentarlos.

    viewModel.irAdelante()
    delay(2000) // Avanza 2 segundos
    viewModel.parar()

    // viewModel.irDerecha()  <-- Falta implementar en ViewModel
    // delay(500)

    viewModel.irAdelante()
    delay(1000)
    viewModel.parar()

    // viewModel.irIzquierda() <-- Falta implementar en ViewModel
    // delay(500)

    viewModel.irAdelante()
    delay(1000)
    viewModel.parar()

    // 3. Hablar al llegar
    val textoClase = "No encuentro la clase $nombreClase en este entorno."
    viewModel.speak(textoClase)
    delay(4000)
}