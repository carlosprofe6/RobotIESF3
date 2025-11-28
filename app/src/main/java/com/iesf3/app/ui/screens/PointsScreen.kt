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
fun PointsScreen(navController: NavController, robotViewModel: RobotViewModel) {
    // Obtenemos la lista de destinos que el robot tiene disponibles para navegar
    val destinations by robotViewModel.destinationsList.observeAsState(initial = emptyList())

    // Usamos un Box para superponer el botón de volver sin romper el layout
    Box(modifier = Modifier.fillMaxSize()) {

        // Flechita para volver atrás (top-left sin molestar)
        BackButton(navController)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Título de la pantalla
            Text("Puntos del Mapa", style = MaterialTheme.typography.titleLarge)
// Si no hay puntos, se avisa

        }
    }
}