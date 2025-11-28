package com.iesf3.app.ui.components

import android.app.Activity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.zIndex

@Composable
fun BackButton(navController: NavController) {
    val context = LocalContext.current
    val scope: CoroutineScope = rememberCoroutineScope()
    var locked by remember { mutableStateOf(false) }

    Box(modifier = Modifier.padding(8.dp).zIndex(10f)  ) {
        IconButton(
            enabled = !locked,
            onClick = {
                if (locked) return@IconButton
                locked = true

                // 1) Intenta retroceder normalmente
                val handled = navController.navigateUp()

                // 2) Si ya no hay a dónde volver, cierra la Activity (evita pantalla en blanco)
                if (!handled) {
                    (context as? Activity)?.finish()
                }

                // 3) Debounce anti-spam
                scope.launch {
                    delay(300)
                    locked = false
                }
            }
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Volver",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}
