// Este es como el nombre de la carpeta donde está este archivo.
package com.iesf3.app

// Cosas básicas de Android: para manejar la pantalla, mensajes, logs, etc.
import android.os.Bundle

// Esto es para mostrar cosas en pantalla, usar botones, columnas, etc.
// Piensa que es como un montón de piezas de Lego para construir la interfaz.
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.*
import com.iesf3.app.robot.RobotConnectionService

// Esto sirve para lanzar tareas en segundo plano, como decirle al robot que haga algo sin bloquear la app.

// Esta clase Pose se usa para indicar posiciones (como coordenadas) del robot.

// Aquí traemos partes del proyecto: navegación, conexión al robot, habilidades, etc.
import com.iesf3.app.navigation.AppNavigation
import com.iesf3.app.robot.RobotManager
import com.iesf3.app.robot.SkillApiService
import com.iesf3.app.ui.theme.MyApplicationTheme
import com.iesf3.app.ui.viewmodels.RobotViewModel

// Esta línea activa lo que usamos para "inyectar cosas automáticamente"
// Básicamente, en vez de estar creando objetos a mano, se generan solos.
// Es como que te den los ingredientes de la receta ya cortados y listos.
import dagger.hilt.android.AndroidEntryPoint

// Algunas cosas extra para el tema de videollamadas, por si el robot puede hablar con alguien.
import javax.inject.Inject

// Esta es la clase principal que se abre al iniciar la app.
// Aquí es donde empieza todo lo que ve el usuario.
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    // Aquí pedimos que nos den (ya listo) el objeto que maneja el robot.
    @Inject
    lateinit var robotManager: RobotManager

    // Esto se encarga de conectar el robot con la app.
    @Inject
    lateinit var robotConnectionService: RobotConnectionService

    // Esto se encarga de las "habilidades" del robot (como si fueran talentos o trucos especiales).
    @Inject
    lateinit var skillApiService: SkillApiService

    // Esto es como una caja donde guardamos y consultamos la información del robot.
    // Nos ayuda a separar los datos de la pantalla.
    private val robotViewModel : RobotViewModel by viewModels()

    // Esto se ejecuta cuando se abre esta pantalla por primera vez.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Aquí podríamos activar que el robot escuche por dónde se mueve, pero está comentado por ahora.
        // robotManager.addNavigationListener(this)

        // Aquí decimos lo que se va a mostrar en pantalla.
        // Es como decir: “vale, vamos a montar toda la interfaz”.
        setContent {
            // Le aplicamos el diseño visual que hemos definido (colores, fuentes, etc).
            MyApplicationTheme {
                // Surface es como una hoja donde pintamos todo.
                Surface(
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Esto lanza la navegación entre pantallas de la app.
                    // Le pasamos el robotViewModel para que las pantallas sepan qué hace el robot.
                    AppNavigation(robotViewModel = robotViewModel)
                }
            }
        }
    }

    // Esto se ejecuta cuando se cierra la pantalla o la app.
    override fun onDestroy() {
        super.onDestroy()

        // Aquí podríamos quitar el "escucha" del robot, si lo hubiéramos puesto.
        // Está comentado por ahora.
        // robotManager.removeNavigationListener(this)
    }
}
