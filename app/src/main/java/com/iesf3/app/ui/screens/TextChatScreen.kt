import android.content.Context
import android.view.inputmethod.InputMethodManager
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.iesf3.app.ui.viewmodels.RobotViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextChatScreen(navController: NavController, robotViewModel: RobotViewModel) {
    var currentMessage by remember { mutableStateOf("") }

    // Obtener la lista de mensajes desde el ViewModel
    val messages = robotViewModel.messages.collectAsState()

    // Estado de desplazamiento de la lista
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    // Usado para evitar desplazarse automáticamente en la primera carga de mensajes
    var hasLoadedInitially by remember { mutableStateOf(false) }

    // Para ocultar el teclado
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current

    LaunchedEffect(messages.value) {
        // Solo desplazarse automáticamente si no es la primera carga
        if (hasLoadedInitially) {
            listState.animateScrollToItem(messages.value.size)
        } else {
            hasLoadedInitially = true
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable {
                // Ocultar el teclado cuando se hace clic fuera del campo de texto
                focusManager.clearFocus()
                hideKeyboard(context)
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            // TopAppBar con los botones de "Volver" y "Borrar"
            TopAppBar(
                title = { Text(text = "Chat") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        // Llamar al método para borrar mensajes en el ViewModel
                        robotViewModel.clearMessages()
                    }) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Borrar chat")
                    }
                }
            )

            // Mostrar los mensajes en una LazyColumn
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp)
            ) {
                items(messages.value) { (role, message) ->
                    ChatMessageItem(role = role, message = message)
                }
            }


            // Caja de texto y botón para enviar mensajes
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BasicTextField(
                    value = currentMessage,
                    onValueChange = { currentMessage = it },
                    modifier = Modifier
                        .weight(1f)
                        .padding(8.dp)
                        .background(Color.LightGray, shape = MaterialTheme.shapes.small)
                        .padding(16.dp),
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Send),
                    keyboardActions = KeyboardActions(onSend = {
                        if (currentMessage.isNotBlank()) {
                            robotViewModel.sendMessage(currentMessage)
                            currentMessage = ""
                            // Desplazar al último mensaje después de enviar
                            coroutineScope.launch {
                                listState.animateScrollToItem(messages.value.size)
                            }
                            focusManager.clearFocus() // Ocultar el teclado
                            hideKeyboard(context)    // Asegurarse de que el teclado se oculte
                        }
                    })
                )
                Button(
                    onClick = {
                        if (currentMessage.isNotBlank()) {
                            robotViewModel.sendMessage(currentMessage)
                            currentMessage = ""
                            // Desplazar al último mensaje después de enviar
                            coroutineScope.launch {
                                listState.animateScrollToItem(messages.value.size)
                            }
                            focusManager.clearFocus() // Ocultar el teclado
                            hideKeyboard(context)    // Asegurarse de que el teclado se oculte
                        }
                    },
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Text("Send")
                }
            }
        }
    }
}

@Composable
fun ChatMessageItem(role: String, message: String) {
    val backgroundColor = when (role) {
        "user" -> Color.Blue
        "assistant" -> Color.Gray
        else -> Color.LightGray
    }
    val textColor = when (role) {
        "user" -> Color.White
        "assistant" -> Color.Black
        else -> Color.Black
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 8.dp)
    ) {
        if (role == "assistant") {
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = "Assistant Avatar",
                modifier = Modifier.size(40.dp).padding(end = 8.dp),
                tint = Color.Gray
            )
        }
        Column(
            modifier = Modifier
                .background(color = backgroundColor, shape = MaterialTheme.shapes.medium)
                .padding(8.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = if (role == "user") "You: " else "ChatGPT",
                style = MaterialTheme.typography.bodySmall.copy(color = textColor),
                modifier = Modifier.padding(bottom = 2.dp)
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodySmall.copy(color = textColor),
                modifier = Modifier.fillMaxWidth()
            )
        }
        if (role == "user") {
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = "User Avatar",
                modifier = Modifier.size(40.dp),
                tint = Color.Blue
            )
        }
    }
}


// Función para ocultar el teclado
fun hideKeyboard(context: Context) {
    val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
}
