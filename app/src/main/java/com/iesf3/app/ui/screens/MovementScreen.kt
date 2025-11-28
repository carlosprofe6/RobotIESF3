package com.iesf3.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.livedata.observeAsState
import androidx.navigation.NavController
import com.iesf3.app.ui.components.BackButton
import com.iesf3.app.ui.viewmodels.RobotViewModel

@Composable
fun MovementScreen(navController: NavController, robotViewModel: RobotViewModel) {
    // Esto recoge las ubicaciones del robot (las que tiene memorizadas)
    val destinations by robotViewModel.destinationsList.observeAsState(initial = emptyList())

    // Usamos Box para que el botón de volver esté superpuesto arriba sin mover nada más
    Box(modifier = Modifier.fillMaxSize()) {
        // Botón pequeñito con una flechita para volver atrás
        BackButton(navController)

        // Aquí va el contenido real de la pantalla
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Título de la pantalla
            Text("Movimiento", style = MaterialTheme.typography.titleLarge)

            // Primera fila: Avanzar / Parar / Atrás
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(onClick = { robotViewModel.irAdelante() }, modifier = Modifier.weight(1f)) {
                    Text("Avanzar", fontSize = 12.sp)
                }
                Button(onClick = { robotViewModel.parar() }, modifier = Modifier.weight(1f)) {
                    Text("Parar", fontSize = 12.sp)
                }
                Button(onClick = { robotViewModel.irAtras() }, modifier = Modifier.weight(1f)) {
                    Text("Atrás", fontSize = 12.sp)
                }
            }

            // Segunda fila: movimiento de cabeza (subir/bajar)
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Cabeza", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.weight(1f)) // Empuja los botones a la derecha
                Button(onClick = { robotViewModel.mirarArriba() }) {
                    Text("Arriba", fontSize = 12.sp)
                }
                Button(onClick = { robotViewModel.mirarAbajo() }) {
                    Text("Abajo", fontSize = 12.sp)
                }
            }

            // Línea divisora para separar secciones
            Divider()

            // Seguimiento de personas: empezar o parar
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Seguimiento", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.weight(1f))
                Button(onClick = { robotViewModel.comenzarSeguimiento() }) {
                    Text("Comenzar")
                }
                Button(onClick = { robotViewModel.pararSeguimiento() }) {
                    Text("Detener")
                }
            }

            // Otra línea divisora
            Divider()

        }
    }
}


